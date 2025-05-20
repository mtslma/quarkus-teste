package fiap.tds.services;

import fiap.tds.dtos.*;
import fiap.tds.dtos.alerta.AlertaCreateDto;
import fiap.tds.dtos.alerta.AlertaResponseDto;
import fiap.tds.entities.Alerta;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.AlertaRepository;
import fiap.tds.repositories.EstacaoRepository;
import fiap.tds.repositories.LinhaRepository;

import java.time.LocalDateTime;
import java.util.List;

public class AlertaService {

    // Repositories
    private final AlertaRepository alertaRepository = new AlertaRepository();
    private final LinhaRepository linhaRepository = new LinhaRepository();
    private final EstacaoRepository estacaoRepository = new EstacaoRepository();

    // Função para registrar alertas
    public void registrar(AlertaCreateDto dto) {
        // Validação de campos nulos
        if (dto == null) {
            throw new BadRequestException("O objeto alerta não pode ser nulo.");
        }
        if (dto.nomeAlerta() == null || dto.nomeAlerta().isBlank()) {
            throw new BadRequestException("O nome do alerta é obrigatório.");
        }
        if (dto.descricaoAlerta() == null || dto.descricaoAlerta().isBlank()) {
            throw new BadRequestException("A descrição do alerta é obrigatória.");
        }
        // Validação de nível de gravidade
        if (dto.nivelGravidade() == null ||
                (!dto.nivelGravidade().equalsIgnoreCase("LEVE")
                && !dto.nivelGravidade().equalsIgnoreCase("MÉDIO")
                && !dto.nivelGravidade().equalsIgnoreCase("GRAVE"))) {
            throw new BadRequestException("O status da estação deve ser \"LEVE\", \"MÉDIO\" ou \"GRAVE\".");
        }
        // Validação dos IDs das linhas e estações
        if (dto.idLinha() <= 0) {
            throw new BadRequestException("ID da linha deve ser maior que zero.");
        }
        if (dto.idEstacao() <= 0) {
            throw new BadRequestException("ID da estação deve ser maior que zero.");
        }
        var linha = linhaRepository.buscarPorId(dto.idLinha());
        if (linha == null || linha.isDeleted()) {
            throw new BadRequestException("A linha informada não existe ou foi excluída.");
        }
        var estacao = estacaoRepository.buscarPorId(dto.idEstacao());
        if (estacao == null || estacao.isDeleted()) {
            throw new BadRequestException("A estação informada não existe ou foi excluída.");
        }
        // Conversão do DTO para entidade
        var alerta = new Alerta();
        alerta.setNomeAlerta(dto.nomeAlerta());
        alerta.setDescricaoAlerta(dto.descricaoAlerta());
        alerta.setNivelGravidade(dto.nivelGravidade().toUpperCase());
        alerta.setIdLinha(dto.idLinha());
        alerta.setIdEstacao(dto.idEstacao());
        alerta.setDeleted(false);
        alerta.setDataCriacao(LocalDateTime.now());
        alertaRepository.registrar(alerta);
    }



    // Função para buscar por alertas
    public SearchListDto<AlertaResponseDto> buscar(int page, String nome, String nivel, String descricao, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        // Chamando o repositório
        var resultado = alertaRepository.buscar(nome, nivel, descricao, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<Alerta> alertas = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<AlertaResponseDto> pageData = List.of();
        if (start < totalItems) {
            pageData = alertas.subList(start, end).stream()
                    .map(this::converterAlertaParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }


    // Função para buscar um alerta por ID
    public AlertaResponseDto buscarPorId(int id) {
        // Validação da existência do alerta
        var alertaExistente = alertaRepository.buscarPorId(id);
        if (alertaExistente == null || alertaExistente.isDeleted()) {
            throw new NotFoundException("Alerta com o ID " + id + " não encontrado");
        }
        return converterAlertaParaDto(alertaExistente);
    }



    // Função para atualizar um alerta
    public void atualizar(int id, AlertaCreateDto dto) {
        // Validação de campos nulos
        if (dto == null) {
            throw new BadRequestException("O objeto alerta não pode ser nulo.");
        }
        var alertaExistente = alertaRepository.buscarPorId(id);
        if (alertaExistente == null || alertaExistente.isDeleted()) {
            throw new NotFoundException("Alerta não encontrado");
        }
        if (dto.descricaoAlerta() == null || dto.descricaoAlerta().isBlank()) {
            throw new BadRequestException("A descrição do alerta é obrigatória.");
        }
        // Validação de nível de gravidade
        if (dto.nivelGravidade() == null ||
                (!dto.nivelGravidade().equalsIgnoreCase("LEVE")
                        && !dto.nivelGravidade().equalsIgnoreCase("MÉDIO")
                        && !dto.nivelGravidade().equalsIgnoreCase("GRAVE"))) {
            throw new BadRequestException("O status da estação deve ser \"LEVE\", \"MÉDIO\" ou \"GRAVE\".");
        }
        // Validação dos IDs das linhas e estações
        var linha = linhaRepository.buscarPorId(dto.idLinha());
        if (linha == null || linha.isDeleted()) {
            throw new BadRequestException("A linha informada não existe ou foi excluída.");
        }
        var estacao = estacaoRepository.buscarPorId(dto.idEstacao());
        if (estacao == null || estacao.isDeleted()) {
            throw new BadRequestException("A estação informada não existe ou foi excluída.");
        }
        // Conversão do DTO para entidade
        alertaExistente.setDescricaoAlerta(dto.descricaoAlerta());
        alertaExistente.setNivelGravidade(dto.nivelGravidade().toUpperCase());
        alertaExistente.setIdLinha(dto.idLinha());
        alertaExistente.setIdEstacao(dto.idEstacao());
        alertaExistente.setNomeAlerta(dto.nomeAlerta()); // se estiver no DTO

        alertaRepository.atualizar(alertaExistente);
    }



    // Função para deletar um alerta
    public void deletar(int id) {
        var alertaExistente = alertaRepository.buscarPorId(id);

        if (alertaExistente == null || alertaExistente.isDeleted()){
            throw new NotFoundException("Alerta com ID " + id + " não encontrada");
        }
        alertaRepository.deletar(id);
    }



    // Função que transforma Alerta em AlertaResponseDto
    private AlertaResponseDto converterAlertaParaDto(Alerta a) {
        return new AlertaResponseDto(
                a.getId(), a.isDeleted(), a.getDataCriacao(),
                a.getNomeAlerta(), a.getDescricaoAlerta(),
                a.getNivelGravidade(), a.getIdLinha(), a.getIdEstacao()
        );
    }
}
