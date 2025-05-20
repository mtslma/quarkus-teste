package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.manutencao.ManutencaoCreateDto;
import fiap.tds.dtos.manutencao.ManutencaoResponseDto;
import fiap.tds.entities.Manutencao;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.*;

import java.time.LocalDateTime;
import java.util.List;

public class ManutencaoService {

    private final ColaboradorRepository colaboradorRepository = new ColaboradorRepository();
    private final LinhaRepository linhaRepository = new LinhaRepository();
    private final EstacaoRepository estacaoRepository = new EstacaoRepository();
    private final ManutencaoRepository manutencaoRepository = new ManutencaoRepository();

    // Função para registrar manutenções
    public void registrar(ManutencaoCreateDto dto) {
        // Validação dos campos nulos
        if (dto == null) {
            throw new BadRequestException("O objeto manutenção não pode ser nulo.");
        }
        if (dto.nomeManutencao() == null || dto.nomeManutencao().trim().isEmpty()) {
            throw new BadRequestException("O nome do manutencao é obrigatório.");
        }
        if (dto.descricaoManutencao() == null || dto.descricaoManutencao().trim().isEmpty()) {
            throw new BadRequestException("A descrição do manutencao é obrigatória.");
        }
        // Validação do nível de prioridade
        if (dto.nivelPrioridade() == null
                || (!dto.nivelPrioridade().equalsIgnoreCase("BAIXA")
                && !dto.nivelPrioridade().equalsIgnoreCase("MÉDIA")
                && !dto.nivelPrioridade().equalsIgnoreCase("ALTA"))) {
            throw new BadRequestException("O nível de prioridade da manutenção deve ser \"BAIXA\", \"MÉDIA\" ou \"ALTA\".");
        }
        // Validando IDs de linhas e estações
        var linhaExistente = linhaRepository.buscarPorId(dto.idLinha());
        if (linhaExistente == null || linhaExistente.isDeleted()) {
            throw new BadRequestException("A linha informada não existe.");
        }
        var estacaoExistente = estacaoRepository.buscarPorId(dto.idEstacao());
        if (estacaoExistente == null || estacaoExistente.isDeleted()) {
            throw new BadRequestException("A estação informada não existe.");
        }
        // Convertendo DTO para entidade
        var manutencao = new Manutencao();
        manutencao.setNomeManutencao(dto.nomeManutencao());
        manutencao.setDescricaoManutencao(dto.descricaoManutencao());
        manutencao.setNivelPrioridade(dto.nivelPrioridade());
        manutencao.setIdEstacao(dto.idEstacao());
        manutencao.setIdLinha(dto.idLinha());
        manutencao.setDeleted(false);
        manutencao.setDataCriacao(LocalDateTime.now());

        manutencaoRepository.registrar(manutencao);
    }

    // Função para buscar manutenções com lógica de validação
    public SearchListDto<ManutencaoResponseDto> buscar(int page, String nome, String descricao, String nivelPrioridade, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        var resultado = manutencaoRepository.buscar(nome, descricao, nivelPrioridade, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<Manutencao> linhas = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<ManutencaoResponseDto> pageData = List.of();
        if (start < totalItems) {
            pageData = linhas.subList(start, end).stream()
                    .map(this::converterManutencaoParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }

    // Função para buscar manutenção por ID
    public ManutencaoResponseDto buscarPorId(int id) {
        var manutecaoExistente = manutencaoRepository.buscarPorId(id);
        if (manutecaoExistente == null) {
            throw new NotFoundException("Manutenção com o ID " + id + " não encontrada");
        }
        // Montando o DTO, tem que estar em ordem
        return converterManutencaoParaDto(manutecaoExistente);
    }

    public void atualizar(int id, ManutencaoCreateDto dto) {
        // Validação dos campos nulos
        if (dto == null) {
            throw new BadRequestException("O objeto manutenção não pode ser nulo.");
        }
        if (dto.nomeManutencao() == null || dto.nomeManutencao().trim().isEmpty()) {
            throw new BadRequestException("O nome do manutencao é obrigatório.");
        }
        if (dto.descricaoManutencao() == null || dto.descricaoManutencao().trim().isEmpty()) {
            throw new BadRequestException("A descrição do manutencao é obrigatória.");
        }
        // Validação do nível de prioridade
        if (dto.nivelPrioridade() == null || (!dto.nivelPrioridade().equalsIgnoreCase("BAIXA")
                && !dto.nivelPrioridade().equalsIgnoreCase("MÉDIA")
                && !dto.nivelPrioridade().equalsIgnoreCase("ALTA"))) {
            throw new BadRequestException("O nível de prioridade da manutenção deve ser \"BAIXA\", \"MÉDIA\" ou \"ALTA\".");
        }
        // Validando IDs de linhas e estações
        var linhaExistente = linhaRepository.buscarPorId(dto.idLinha());
        if (linhaExistente == null || linhaExistente.isDeleted()) {
            throw new BadRequestException("A linha informada não existe.");
        }
        var estacaoExistente = estacaoRepository.buscarPorId(dto.idEstacao());
        if (estacaoExistente == null || estacaoExistente.isDeleted()) {
            throw new BadRequestException("A estação informada não existe.");
        }
        // Convertendo DTO para entidade
        var manutencao = new Manutencao();
        manutencao.setNomeManutencao(dto.nomeManutencao());
        manutencao.setDescricaoManutencao(dto.descricaoManutencao());
        manutencao.setNivelPrioridade(dto.nivelPrioridade());
        manutencao.setIdEstacao(dto.idEstacao());
        manutencao.setIdLinha(dto.idLinha());
        manutencao.setDeleted(false);
        manutencao.setDataCriacao(LocalDateTime.now());

        manutencaoRepository.atualizar(id, manutencao);
    }

    // Função para deletar um alerta
    public void deletar(int id) {

        var manutencaoExistente = manutencaoRepository.buscarPorId(id);

        if (manutencaoExistente == null || manutencaoExistente.isDeleted()){
            throw new NotFoundException("Manutenção com ID " + id + " não encontrada");
        }

        manutencaoRepository.deletar(id);
    }

    // Função que converte Manutencao em CreateManutencaoDto
    private ManutencaoResponseDto converterManutencaoParaDto(Manutencao a) {
        return new ManutencaoResponseDto(
                a.getId(),
                a.isDeleted(),
                a.getDataCriacao(),
                a.getNomeManutencao(),
                a.getDescricaoManutencao(),
                a.getNivelPrioridade(),
                a.getIdLinha(),
                a.getIdEstacao()
        );
    }
}
