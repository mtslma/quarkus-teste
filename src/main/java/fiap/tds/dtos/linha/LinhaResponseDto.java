package fiap.tds.dtos.linha;

import java.time.LocalDateTime;

public record LinhaResponseDto(
        int id,
        boolean deleted,
        String nomeLinha,
        int numeroLinha,
        String statusLinha,
        LocalDateTime dataCriacao) {
    public LinhaResponseDto {
    }
}
