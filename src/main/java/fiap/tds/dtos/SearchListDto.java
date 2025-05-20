package fiap.tds.dtos;

import java.util.List;

public record SearchListDto<T>(
        int page, String direction, int pageSize, int totalItems, List<T> data
) {
    public SearchListDto {
    }
}
