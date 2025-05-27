package fiap.tds.dtos.cidadeDto;

import java.math.BigDecimal;

public record CreateCidadeDto(
        String cep,
        String nomeCidade
)  {
    public CreateCidadeDto {
    }
}
