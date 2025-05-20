package fiap.tds.dtos.mensagemContato;


import java.time.LocalDateTime;

public record MensagemContatoResponseDto(int id, boolean deleted, LocalDateTime dataCriacao, String nome, String email, String mensagem) {
}
