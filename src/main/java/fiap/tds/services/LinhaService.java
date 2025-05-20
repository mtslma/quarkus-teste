package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.linha.LinhaCreateDto;
import fiap.tds.dtos.linha.LinhaResponseDto;
import fiap.tds.entities.Linha;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.LinhaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class LinhaService {

    private final LinhaRepository linhaRepository = new LinhaRepository();

    // Função para registrar uma nova linha
    public void registrar(LinhaCreateDto dto) {
        // Validação de dados null
        if (dto.nomeLinha() == null){
            throw new BadRequestException("O nome da linha não pode ser vazio");
        }
        // Verificação do status da linha
        if (!dto.statusLinha().equalsIgnoreCase("NORMAL")
            && !dto.statusLinha().equalsIgnoreCase("PARCIAL")
            && !dto.statusLinha().equalsIgnoreCase("INTERROMPIDO")){
            throw new BadRequestException("O status da linha deve ser \"NORMAL\", \"PARCIAL\" OU \"INTERROMPIDO\"");
        }
        // Conversão do DTO para entidade
        var linha = new Linha();
        linha.setNomeLinha(dto.nomeLinha());
        linha.setNumeroLinha(dto.numeroLinha());
        linha.setStatusLinha(dto.statusLinha().toUpperCase());
        linha.setDeleted(false);
        linha.setDataCriacao(LocalDateTime.now());
        linhaRepository.registrar(linha);
    }

    // Função para buscar linhas
    public SearchListDto<LinhaResponseDto> buscar(int page, String nome, String status, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        // Chamando o repositório
        var resultado = linhaRepository.buscar(nome, status, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<Linha> linhas = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<LinhaResponseDto> pageData = List.of();
        if (start < totalItems) {
            pageData = linhas.subList(start, end).stream()
                    .map(this::converterLinhaParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }

    // Função para buscar linha por ID
    public LinhaResponseDto buscarPorId(int id) {
        var linhaExistente = linhaRepository.buscarPorId(id);
        if (linhaExistente == null) {
            throw new NotFoundException("Linha com o ID " + id + " não encontrada");
        }
        return converterLinhaParaDto(linhaExistente);
    }

    // Função para atualizar apenas o status de uma linha
    public void atualizarStatusLinha(int id, LinhaCreateDto dto){
        var linhaExistente = linhaRepository.buscarPorId(id);

        if (linhaExistente == null) {
            throw new NotFoundException("Linha com ID " + id + " não encontrada");
        }

        // Verificação do status da linha
        String status = dto.statusLinha();
        if (!"NORMAL".equalsIgnoreCase(status) &&
                !"PARCIAL".equalsIgnoreCase(status) &&
                !"INTERROMPIDO".equalsIgnoreCase(status)) {
            throw new BadRequestException("O status da linha deve ser \"NORMAL\", \"PARCIAL\" ou \"INTERROMPIDO\"");
        }

        linhaRepository.atualizarStatus(id, dto.statusLinha());
    }

    // Função para atualizar linha
    public void atualizar(int id, LinhaCreateDto dto) {

        // Validação de campos nulos
        var linhaExistente = linhaRepository.buscarPorId(id);

        if (linhaExistente == null || linhaExistente.isDeleted()) {
            throw new NotFoundException("Linha com ID " + id + " não encontrada");
        }

        // Validação de dados null
        if (dto.nomeLinha() == null){
            throw new BadRequestException("O nome da linha não pode ser vazio");
        }

        // Verificação do status da linha
        if (!dto.statusLinha().equalsIgnoreCase("NORMAL")
                && !dto.statusLinha().equalsIgnoreCase("PARCIAL")
                && !dto.statusLinha().equalsIgnoreCase("INTERROMPIDO")){
            throw new BadRequestException("O status da linha deve ser \"NORMAL\", \"PARCIAL\" OU \"INTERROMPIDO\"");
        }

        // Conversão do DTO para entidade
        var linha = new Linha();
        linha.setNomeLinha(dto.nomeLinha());
        linha.setNumeroLinha(dto.numeroLinha());
        linha.setStatusLinha(dto.statusLinha().toUpperCase());
        linha.setDeleted(false);
        linha.setDataCriacao(LocalDateTime.now());

        linhaRepository.atualizar(id, linha);
    }


    public void deletar(int id) {
        var linhaExistente = linhaRepository.buscarPorId(id);
        if (linhaExistente == null) {
            throw new NotFoundException("Linha com ID " + id + " não encontrada");
        }
        linhaRepository.deletar(id);
    }
    // Função para converter Linha em LinhaResponseDto
    private LinhaResponseDto converterLinhaParaDto(Linha a) {
        return new LinhaResponseDto(
                a.getId(),
                a.isDeleted(),
                a.getNomeLinha(),
                a.getNumeroLinha(),
                a.getStatusLinha(),
                a.getDataCriacao()
        );
    }


}
