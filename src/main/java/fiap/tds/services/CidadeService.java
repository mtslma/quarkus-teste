package fiap.tds.services;

import fiap.tds.dtos.SearchListDto; // Certifique-se que esta classe genérica existe
import fiap.tds.dtos.cidadeDto.CreateCidadeDto;
import fiap.tds.dtos.cidadeDto.ResponseCidadeDto;
import fiap.tds.entities.Cidade;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.infrastructure.ViaCep;
import fiap.tds.repositories.CidadeRepository; // Importe o CidadeRepository

import javax.naming.ServiceUnavailableException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.regex.Pattern;

public class CidadeService {


    private final CidadeRepository cidadeRepository = new CidadeRepository();
    private final ViaCep viaCepService = new ViaCep(); // Instanciação atualizada
    private static final int PAGE_SIZE = 10;

    // Regex para validar CEP (formato XXXXX-XXX ou XXXXXXXX)
    private static final Pattern CEP_PATTERN = Pattern.compile("^\\d{5}-?\\d{3}$");

    public ResponseCidadeDto registrar(CreateCidadeDto dto) throws ServiceUnavailableException {
        if (dto.cep() == null || dto.cep().isBlank()) {
            throw new BadRequestException("O CEP é obrigatório.");
        }
        String cepLimpo = dto.cep().replaceAll("[^0-9]", "");
        if (!CEP_PATTERN.matcher(dto.cep()).matches() || cepLimpo.length() != 8) {
            throw new BadRequestException("Formato de CEP inválido. Use XXXXX-XXX ou XXXXXXXX.");
        }

        ViaCep.EnderecoCompleto enderecoInfo; // Tipo atualizado
        try {
            enderecoInfo = viaCepService.obterEnderecoCompletoPorCep(cepLimpo);
        } catch (NotFoundException e) {
            throw new BadRequestException("CEP não encontrado ou inválido: " + cepLimpo);
        } catch (ServiceUnavailableException e) {
            throw new ServiceUnavailableException("Não foi possível obter os dados do endereço para o CEP: " + cepLimpo + ". " + e.getMessage());
        }

        if (enderecoInfo == null || enderecoInfo.nomeCidade() == null || enderecoInfo.coordenadas() == null) {
            throw new ServiceUnavailableException("Não foi possível obter informações completas do endereço para o CEP: " + cepLimpo);
        }

        String nomeCidadeFinal = (dto.nomeCidade() != null && !dto.nomeCidade().isBlank()) ? dto.nomeCidade() : enderecoInfo.nomeCidade();

        // Validações de latitude e longitude (se a API de geocodificação real as retornar)
        BigDecimal latitude = enderecoInfo.coordenadas().latitude();
        BigDecimal longitude = enderecoInfo.coordenadas().longitude();

        if (latitude.compareTo(new BigDecimal("-90")) < 0 || latitude.compareTo(new BigDecimal("90")) > 0) {
            throw new BadRequestException("Latitude inválida obtida para o CEP. Deve estar entre -90 e 90.");
        }
        if (longitude.compareTo(new BigDecimal("-180")) < 0 || longitude.compareTo(new BigDecimal("180")) > 0) {
            throw new BadRequestException("Longitude inválida obtida para o CEP. Deve estar entre -180 e 180.");
        }


        var cidade = new Cidade();
        cidade.setNomeCidade(nomeCidadeFinal);
        cidade.setLat(latitude);
        cidade.setLon(longitude);
        cidade.setDataCriacao(LocalDateTime.now());
        cidade.setDeleted(false);
        // Campos como UF, logradouro, bairro do enderecoInfo podem ser armazenados
        // se sua entidade Cidade e tabela tiverem esses campos.

        cidadeRepository.registrar(cidade);
        return buscarPorId(cidade.getIdCidade()); // Busca para retornar com as contagens
    }

    // Função para buscar cidades registradas
    public SearchListDto<ResponseCidadeDto> buscar(int page, String nome, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }

        var resultadoRepository = cidadeRepository.buscar(nome, direction);

        List<Cidade> cidades = resultadoRepository.data();
        int totalItems = resultadoRepository.totalItems();

        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);

        List<ResponseCidadeDto> pageData = List.of();
        if (start < totalItems && !cidades.isEmpty()) {
            pageData = cidades.subList(start, Math.min(end, cidades.size())).stream()
                    .map(this::converterCidadeParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }

    public ResponseCidadeDto buscarPorId(int id) {
        var cidade = cidadeRepository.buscarPorId(id);
        if (cidade == null) {
            throw new NotFoundException("Cidade com o ID " + id + " não encontrada.");
        }
        return converterCidadeParaDto(cidade);
    }

    public ResponseCidadeDto atualizar(int id, CreateCidadeDto dto) throws ServiceUnavailableException {
        ResponseCidadeDto cidadeExistenteDto = buscarPorId(id);

        String nomeCidadeAtualizado = dto.nomeCidade();
        BigDecimal latAtualizada = cidadeExistenteDto.lat();
        BigDecimal lonAtualizada = cidadeExistenteDto.lon();

        if (dto.cep() != null && !dto.cep().isBlank()) {
            String cepLimpo = dto.cep().replaceAll("[^0-9]", "");
            if (!CEP_PATTERN.matcher(dto.cep()).matches() || cepLimpo.length() != 8) {
                throw new BadRequestException("Formato de CEP inválido para atualização. Use XXXXX-XXX ou XXXXXXXX.");
            }
            try {
                ViaCep.EnderecoCompleto enderecoInfo = viaCepService.obterEnderecoCompletoPorCep(cepLimpo); // Tipo atualizado
                if (enderecoInfo != null && enderecoInfo.coordenadas() != null) {
                    latAtualizada = enderecoInfo.coordenadas().latitude();
                    lonAtualizada = enderecoInfo.coordenadas().longitude();
                    if (nomeCidadeAtualizado == null || nomeCidadeAtualizado.isBlank()){
                        nomeCidadeAtualizado = enderecoInfo.nomeCidade();
                    }
                }
            } catch (NotFoundException e) {
                throw new BadRequestException("CEP " + cepLimpo + " não encontrado para atualização.");
            }
        }

        if (nomeCidadeAtualizado == null || nomeCidadeAtualizado.isBlank()) {
            throw new BadRequestException("O nome da cidade é obrigatório para atualização.");
        }
        if (latAtualizada.compareTo(new BigDecimal("-90")) < 0 || latAtualizada.compareTo(new BigDecimal("90")) > 0) {
            throw new BadRequestException("Latitude inválida. Deve estar entre -90 e 90.");
        }
        if (lonAtualizada.compareTo(new BigDecimal("-180")) < 0 || lonAtualizada.compareTo(new BigDecimal("180")) > 0) {
            throw new BadRequestException("Longitude inválida. Deve estar entre -180 e 180.");
        }

        Cidade cidadeParaAtualizar = new Cidade();
        cidadeParaAtualizar.setIdCidade(id);
        cidadeParaAtualizar.setNomeCidade(nomeCidadeAtualizado);
        cidadeParaAtualizar.setLat(latAtualizada);
        cidadeParaAtualizar.setLon(lonAtualizada);
        cidadeParaAtualizar.setDataCriacao(cidadeExistenteDto.dataCriacao());
        cidadeParaAtualizar.setDeleted(cidadeExistenteDto.deleted());

        cidadeRepository.atualizar(id, cidadeParaAtualizar);
        return buscarPorId(id);
    }

    public void deletar(int id) {
        buscarPorId(id);
        cidadeRepository.deletar(id);
    }

    private ResponseCidadeDto converterCidadeParaDto(Cidade cidade) {
        if (cidade == null) return null;
        return new ResponseCidadeDto(
                cidade.getIdCidade(),
                cidade.isDeleted(),
                cidade.getDataCriacao(),
                cidade.getNomeCidade(),
                cidade.getQuantidadeOcorrencias(),
                cidade.getQuantidadeAbrigos(),
                cidade.getLat(),
                cidade.getLon()
        );
    }
}