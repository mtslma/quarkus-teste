package fiap.tds.dtos.sessaoColaboradorDto;

import fiap.tds.dtos.colaborador.ColaboradorResponseDto;

import java.time.LocalDateTime;

public record SessaoColaboradorResponseDto(
        String tokenSessao,
        String statusSessao, LocalDateTime dataLogin,
        LocalDateTime dataLogout, ColaboradorResponseDto colaboradorResponseDto) {
    public SessaoColaboradorResponseDto {
    }
}
