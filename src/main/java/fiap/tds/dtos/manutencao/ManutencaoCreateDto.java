package fiap.tds.dtos.manutencao;

public record ManutencaoCreateDto(
        String nomeManutencao,
        String descricaoManutencao,
        String nivelPrioridade,
        int idLinha,
        int idEstacao
) {
    public ManutencaoCreateDto {
    }
}
