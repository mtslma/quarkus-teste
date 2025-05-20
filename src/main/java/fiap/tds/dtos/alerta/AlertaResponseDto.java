package fiap.tds.dtos.alerta;

import java.time.LocalDateTime;

public record AlertaResponseDto(
        int id,
        boolean deleted,
        LocalDateTime dataCriacao,
        String nomeAlerta,
        String descricaoAlerta,
        String nivelGravidade,
        int idLinha,
        int idEstacao
) {
    public AlertaResponseDto {
    }
}
