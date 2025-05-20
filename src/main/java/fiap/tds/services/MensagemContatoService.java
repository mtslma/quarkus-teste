package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.mensagemContato.MensagemContatoCreateDto;
import fiap.tds.dtos.mensagemContato.MensagemContatoResponseDto;
import fiap.tds.entities.MensagemContato;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.repositories.MensagemContatoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class MensagemContatoService {

    private final MensagemContatoRepository mensagemContatoRepository;

    public MensagemContatoService() {
        this.mensagemContatoRepository = new MensagemContatoRepository();
    }

    public void registrar(MensagemContatoCreateDto dto) {
        if (dto.nome() == null || dto.email() == null || dto.mensagem() == null) {
            throw new BadRequestException("Todos os campos (nome, email, mensagem) são obrigatórios.");
        }

         // Convertendo DTO em entidade
        var mensagemContato = new MensagemContato();
        mensagemContato.setNome(dto.nome());
        mensagemContato.setEmail(dto.email());
        mensagemContato.setMensagem(dto.mensagem());
        mensagemContato.setDeleted(false);
        mensagemContato.setDataCriacao(LocalDateTime.now());

        mensagemContatoRepository.registrar(mensagemContato);
    }

    // Função para buscar as mensagens com lógica de validação
    public SearchListDto<MensagemContatoResponseDto> buscar(int page, String name, String text, String email, String direction) {
        if (page < 1) {
            throw new BadRequestException("O número da página deve ser maior ou igual a 1.");
        }
        final int PAGE_SIZE = 9;
        var resultado = mensagemContatoRepository.buscar(name, text, email, direction);
        // Informações de paginação
        int totalItems = resultado.totalItems();
        List<MensagemContato> mensagensContato = resultado.data();
        int start = Math.max((page - 1) * PAGE_SIZE, 0);
        int end = Math.min(start + PAGE_SIZE, totalItems);
        // Criando a lista que será retornada
        List<MensagemContatoResponseDto> pageData = List.of();
        if (start < totalItems) {
            pageData = mensagensContato.subList(start, end).stream()
                    .map(this::converterMensagemContatoParaDto)
                    .toList();
        }
        return new SearchListDto<>(page, direction, PAGE_SIZE, totalItems, pageData);
    }

    // Função para deletar mensagem de contato
    public void deletar(int id) {
        mensagemContatoRepository.deletarMensagemContato(id);
    }

    // Converte MensagemContato em MensagemContatoDto
    private MensagemContatoResponseDto converterMensagemContatoParaDto(MensagemContato a) {
        return new MensagemContatoResponseDto(
                a.getId(),
                a.isDeleted(),
                a.getDataCriacao(),
                a.getNome(),
                a.getEmail(),
                a.getMensagem()
        );
    }

}
