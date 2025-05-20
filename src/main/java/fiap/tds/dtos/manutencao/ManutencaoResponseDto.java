package fiap.tds.dtos.manutencao;

import java.time.LocalDateTime;

public record ManutencaoResponseDto(
        int id,
        boolean deleted,
        LocalDateTime dataCriacao,
        String nomeManutencao,
        String descricaoManutencao,
        String nivelPrioridade,
        int idLinha,
        int idEstacao
) {
    public ManutencaoResponseDto {
    }
}
