package fiap.tds.dtos.alerta;

public record AlertaCreateDto(
        String nomeAlerta,
        String descricaoAlerta,
        String nivelGravidade,
        int idLinha,
        int idEstacao
) {
    public AlertaCreateDto {
    }
}
