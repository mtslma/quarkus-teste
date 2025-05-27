package fiap.tds.infrastructure; // Pacote alterado

import com.google.gson.reflect.TypeToken;
import fiap.tds.dtos.ResponseNominatimDto;
import fiap.tds.dtos.ResponseViaCepDto;
import fiap.tds.exceptions.NotFoundException;

import com.google.gson.Gson;

import java.math.BigDecimal;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.naming.ServiceUnavailableException;

public class ViaCep {

    private static final Logger logger = LogManager.getLogger(ViaCep.class);
    private static final String VIA_CEP_URL = "https://viacep.com.br/ws/";
    private static final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search";
    // IMPORTANTE: Substitua pelo User-Agent da SUA aplicação!
    private static final String NOMINATIM_USER_AGENT = "ponto-seguro-fiap/1.0 (devmtslma@gmail.com)";

    private final HttpClient httpClient;
    private final Gson gson;

    public record Coordenadas(BigDecimal latitude, BigDecimal longitude) {}
    public record EnderecoCompleto(String nomeCidade, String uf, String logradouro, String bairro, Coordenadas coordenadas) {}

    public ViaCep() {
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .build();
        this.gson = new Gson();
    }

    public ResponseViaCepDto buscarViaCep(String cep) throws ServiceUnavailableException { // Tipo de retorno ajustado
        if (cep == null || cep.trim().isEmpty()) {
            throw new IllegalArgumentException("CEP não pode ser nulo ou vazio.");
        }
        String cepFormatado = cep.replaceAll("[^0-9]", "");
        if (cepFormatado.length() != 8) {
            throw new IllegalArgumentException("CEP inválido. Deve conter 8 dígitos numéricos.");
        }

        String urlString = VIA_CEP_URL + cepFormatado + "/json/";
        logger.info("Consultando ViaCEP: {}", urlString);

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .timeout(Duration.ofSeconds(10))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                logger.debug("Resposta ViaCEP: {}", responseBody);
                ResponseViaCepDto viaCepResponse = gson.fromJson(responseBody, ResponseViaCepDto.class); // Uso ajustado
                if (viaCepResponse.erro()) {
                    logger.warn("CEP {} não encontrado no ViaCEP.", cepFormatado);
                    throw new NotFoundException("CEP " + cepFormatado + " não encontrado.");
                }
                return viaCepResponse;
            } else {
                logger.error("Erro ao consultar ViaCEP para {}. Status: {}", cepFormatado, response.statusCode());
                throw new ServiceUnavailableException("Serviço ViaCEP indisponível ou retornou erro. Status: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrompida durante a chamada ao ViaCEP para {}: {}", cepFormatado, e.getMessage());
            throw new ServiceUnavailableException("Consulta ao ViaCEP interrompida.");
        } catch (Exception e) {
            logger.error("Erro ao conectar ou processar resposta do ViaCEP para {}: {}", cepFormatado, e.getMessage(), e);
            throw new ServiceUnavailableException("Erro ao comunicar com o serviço ViaCEP: " + e.getMessage());
        }
    }

    private Coordenadas geocodificarComNominatim(String logradouro, String cidade, String estado) throws ServiceUnavailableException {
        if (cidade == null || cidade.isBlank()) {
            logger.warn("Nome da cidade está vazio, não é possível geocodificar com Nominatim.");
            throw new IllegalArgumentException("Nome da cidade é necessário para geocodificação.");
        }

        StringBuilder queryBuilder = new StringBuilder();
        if (logradouro != null && !logradouro.isBlank()) {
            queryBuilder.append(logradouro).append(", ");
        }
        queryBuilder.append(cidade);
        if (estado != null && !estado.isBlank()) {
            queryBuilder.append(", ").append(estado);
        }
        queryBuilder.append(", Brasil");

        String queryString = queryBuilder.toString();
        logger.info("Geocodificando com Nominatim: '{}'", queryString);

        try {
            String encodedQuery = URLEncoder.encode(queryString, StandardCharsets.UTF_8);
            String urlString = String.format("%s?q=%s&format=json&addressdetails=1&limit=1", NOMINATIM_URL, encodedQuery);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlString))
                    .header("User-Agent", NOMINATIM_USER_AGENT)
                    .timeout(Duration.ofSeconds(15))
                    .GET()
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                String responseBody = response.body();
                logger.debug("Resposta Nominatim: {}", responseBody);

                var listType = new TypeToken<List<ResponseNominatimDto>>() {}.getType();
                List<ResponseNominatimDto> results = gson.fromJson(responseBody, listType);

                if (results != null && !results.isEmpty()) {
                    ResponseNominatimDto result = results.get(0);
                    if (result.lat() != null && result.lon() != null) {
                        return new Coordenadas(new BigDecimal(result.lat()), new BigDecimal(result.lon()));
                    } else {
                        logger.warn("Nominatim retornou resultado sem lat/lon para: {}", queryString);
                    }
                } else {
                    logger.warn("Nenhum resultado encontrado no Nominatim para: {}", queryString);
                }
            } else {
                logger.error("Erro ao consultar Nominatim para '{}'. Status: {}", queryString, response.statusCode());
                throw new ServiceUnavailableException("Serviço de geocodificação (Nominatim) indisponível ou retornou erro. Status: " + response.statusCode());
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            logger.error("Thread interrompida durante geocodificação para '{}': {}", queryString, e.getMessage());
            throw new ServiceUnavailableException("Geocodificação com Nominatim interrompida.");
        } catch (Exception e) {
            logger.error("Erro na geocodificação com Nominatim para '{}': {}", queryString, e.getMessage(), e);
            throw new ServiceUnavailableException("Erro ao comunicar com o serviço de geocodificação (Nominatim): " + e.getMessage());
        }
        logger.error("Falha ao obter coordenadas válidas do Nominatim para: {}", queryString);
        throw new NotFoundException("Não foi possível determinar as coordenadas geográficas para o endereço fornecido.");
    }

    public EnderecoCompleto obterEnderecoCompletoPorCep(String cep) throws ServiceUnavailableException {
        ResponseViaCepDto viaCepInfo = buscarViaCep(cep); // Uso ajustado

        if (viaCepInfo == null || viaCepInfo.localidade() == null) {
            throw new NotFoundException("Não foi possível obter informações de cidade para o CEP: " + cep);
        }

        Coordenadas coordenadasReais = geocodificarComNominatim(
                viaCepInfo.logradouro(),
                viaCepInfo.localidade(),
                viaCepInfo.uf()
        );

        return new EnderecoCompleto(
                viaCepInfo.localidade(),
                viaCepInfo.uf(),
                viaCepInfo.logradouro(),
                viaCepInfo.bairro(),
                coordenadasReais
        );
    }
}
