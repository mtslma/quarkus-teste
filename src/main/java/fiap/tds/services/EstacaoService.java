package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.estacao.EstacaoCreateDto;
import fiap.tds.dtos.estacao.EstacaoResponseDto;
import fiap.tds.entities.Estacao;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.EstacaoRepository;
import fiap.tds.repositories.LinhaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class EstacaoService {

    // Repositories
    private final EstacaoRepository estacaoRepository = new EstacaoRepository();
    private final LinhaRepository linhaRepository = new LinhaRepository();


    // Função para registrar uma nova linha
    public void registrar(EstacaoCreateDto dto) {
        // Validação de campos nulos
        var estacaoExistente = linhaRepository.buscarPorId(dto.idLinha());
        if (estacaoExistente == null || estacaoExistente.isDeleted()){
            throw new BadRequestException("A linha com ID " + dto.idLinha() + " não existe");
        }
        if (dto.nomeEstacao() == null || dto.nomeEstacao().isBlank()) {
            throw new BadRequestException("O nome da estação não pode estar vazio.");
        }
        if (dto.inicioOperacao() == null || dto.fimOperacao() == null) {
            throw new BadRequestException("Os horários de início e fim de operação são obrigatórios.");
        }
        // Validação do status da linha
        if (!dto.statusEstacao().equalsIgnoreCase("NORMAL")
                && !dto.statusEstacao().equalsIgnoreCase("PARCIAL")
                && !dto.statusEstacao().equalsIgnoreCase("INTERROMPIDO")){
            throw new BadRequestException("O status da linha deve ser \"NORMAL\", \"PARCIAL\" OU \"INTERROMPIDO\"");
        }
        // Conversão do DTO para entidade
        var estacao = new Estacao();
        estacao.setNomeEstacao(dto.nomeEstacao());
        estacao.setStatusEstacao(dto.statusEstacao().toUpperCase());
        estacao.setInicioOperacao(dto.inicioOperacao());
        estacao.setFimOperacao(dto.fimOperacao());
        estacao.setIdLinha(dto.idLinha());
        estacao.setDeleted(false);
        estacao.setDataCriacao(LocalDateTime.now());
        estacaoRepository.registrar(estacao);
    }


    // Função que busca estações
    public SearchListDto<EstacaoResponseDto> buscar(int page, String nome, String status, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        // Chamando o repositório
        var resultado = estacaoRepository.buscar(nome, status, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<Estacao> estacoes = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<EstacaoResponseDto> pageData = List.of();

        if (start < totalItems) {
            pageData = estacoes.subList(start, end).stream()
                    .map(this::converterEstacaoParaDto)
                    .toList();
        }

        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }

    public EstacaoResponseDto buscarPorId(int id) {
        var estacaoExistente = estacaoRepository.buscarPorId(id);
        // Validação da existência da estação
        if (estacaoExistente == null) {
            throw new NotFoundException("Estação com o ID " + id + " não encontrada");
        }
        return converterEstacaoParaDto(estacaoExistente);
    }

    // Função que atualiza apenas o status da estação
    public void atualizar(int id, EstacaoCreateDto dto){
        // Validação de campos nulos
        if (dto == null) {
            throw new BadRequestException("O objeto estação não pode ser nulo.");
        }
        // Validação de campos nulos
        var estacaoExistente = linhaRepository.buscarPorId(dto.idLinha());
        if (estacaoExistente == null || estacaoExistente.isDeleted()){
            throw new BadRequestException("A linha com ID " + dto.idLinha() + " não existe");
        }
        if (dto.nomeEstacao() == null || dto.nomeEstacao().isBlank()) {
            throw new BadRequestException("O nome da estação não pode estar vazio.");
        }
        if (dto.inicioOperacao() == null || dto.fimOperacao() == null) {
            throw new BadRequestException("Os horários de início e fim de operação são obrigatórios.");
        }
        // Verificação do status da estação
        String status = dto.statusEstacao();
        if (!"NORMAL".equalsIgnoreCase(status) &&
                !"PARCIAL".equalsIgnoreCase(status) &&
                !"INTERROMPIDO".equalsIgnoreCase(status)) {
            throw new BadRequestException("O status da estação deve ser \"NORMAL\", \"PARCIAL\" ou \"INTERROMPIDO\"");
        }
        // Conversão do DTO para entidade
        var estacao = new Estacao();
        estacao.setNomeEstacao(dto.nomeEstacao());
        estacao.setStatusEstacao(dto.statusEstacao().toUpperCase());
        estacao.setInicioOperacao(dto.inicioOperacao());
        estacao.setFimOperacao(dto.fimOperacao());
        estacao.setIdLinha(dto.idLinha());
        estacao.setDeleted(false);
        estacao.setDataCriacao(LocalDateTime.now());

        estacaoRepository.atualizar(id, estacao);
    }


    // Função que atualiza apenas o status de uma estação
    public void atualizarStatus(int id, EstacaoCreateDto dto){
        var estacaoExistente = estacaoRepository.buscarPorId(id);

        if (estacaoExistente == null) {
            throw new NotFoundException("Linha com ID " + id + " não encontrada");
        }

        // Verificação do status da estação
        String status = dto.statusEstacao();
        if (!"NORMAL".equalsIgnoreCase(status) &&
                !"PARCIAL".equalsIgnoreCase(status) &&
                !"INTERROMPIDO".equalsIgnoreCase(status)) {
            throw new BadRequestException("O status da estação deve ser \"NORMAL\", \"PARCIAL\" ou \"INTERROMPIDO\"");
        }

        estacaoRepository.atualizarStatus(id, dto.statusEstacao());

    }


    // Função que deleta uma estação
    public void deletar(int id) {
        var estacaoExistente = estacaoRepository.buscarPorId(id);
        if (estacaoExistente == null || estacaoExistente.isDeleted()) {
            throw new NotFoundException("Estação com ID " + id + " não encontrada.");
        }
        estacaoRepository.deletarEstacao(id);
    }

    // Função que transforma Estacao em EstacaoResponseDto
    private EstacaoResponseDto converterEstacaoParaDto(Estacao a) {
        return new EstacaoResponseDto(
                a.getId(),
                a.getIdLinha(),
                a.isDeleted(),
                a.getDataCriacao(),
                a.getNomeEstacao(),
                a.getStatusEstacao(),
                a.getInicioOperacao(),
                a.getFimOperacao()
        );
    }
}
