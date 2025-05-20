package fiap.tds.dtos;

import java.util.List;

public record SearchResult<T>(
        List<T> data,
        int totalItems
) {

}
