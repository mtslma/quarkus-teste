package fiap.tds.dtos.usuarioDto;

import java.time.LocalDateTime;

public record ResponseUsuarioDto(
        int idUsuario,
        boolean deleted,
        LocalDateTime dataCriacao,
        String nomeUsuario,
        String tipoUsuario,
        int idCidade,
        String telefoneContato
) {
}
