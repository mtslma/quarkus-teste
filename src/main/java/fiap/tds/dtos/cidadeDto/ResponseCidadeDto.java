package fiap.tds.dtos.cidadeDto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ResponseCidadeDto(
        int idCidade,
        boolean deleted,
        LocalDateTime dataCriacao,
        String nomeCidade,
        int quantidadeOcorrencias,
        int quantidadeAbrigos,
        BigDecimal lat,
        BigDecimal lon
) {
    public ResponseCidadeDto {
    }
}
