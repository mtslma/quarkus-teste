package fiap.tds.dtos.sessaoUsuarioDto;

import fiap.tds.dtos.usuarioDto.ResponseUsuarioDto;

import java.time.LocalDateTime;

public record ResponseSessaoUsuarioDto(
        String tokenSessao,
        String statusSessao, LocalDateTime dataLogin,
        LocalDateTime dataLogout, ResponseUsuarioDto responseUsuarioDto
) {
}
