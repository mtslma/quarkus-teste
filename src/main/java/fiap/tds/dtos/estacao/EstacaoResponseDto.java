package fiap.tds.dtos.estacao;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record EstacaoResponseDto(
        int id,
        int idLinha,
        boolean deleted,
        LocalDateTime dataCriacao,
        String nomeEstacao,
        String statusEstacao,
        LocalTime inicioOperacao,
        LocalTime fimOperacao
) {
    public EstacaoResponseDto {
    }
}
