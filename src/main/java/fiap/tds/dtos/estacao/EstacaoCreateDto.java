package fiap.tds.dtos.estacao;

import java.time.LocalDateTime;
import java.time.LocalTime;

public record EstacaoCreateDto(
        int idLinha, String nomeEstacao,
        String statusEstacao, LocalTime inicioOperacao,
        LocalTime fimOperacao) {
    public EstacaoCreateDto {
    }
}

