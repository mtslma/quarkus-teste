package fiap.tds.dtos.colaborador;

import fiap.tds.entities.AutenticaColaborador;

import java.time.LocalDateTime;

public record ColaboradorResponseDto(int id, boolean deleted, LocalDateTime dataCriacao, String nomeColaborador, String tipoColaborador) {
    public ColaboradorResponseDto {
    }
}
