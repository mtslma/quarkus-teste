package fiap.tds.dtos.colaborador;

import fiap.tds.entities.AutenticaColaborador;

import java.time.LocalDateTime;

public record ColaboradorCreateDto(
        String nomeColaborador,
        String tipoColaborador,
        AutenticaColaborador autenticaColaborador) {
    public ColaboradorCreateDto {
    }
}
