package fiap.tds.dtos;

import com.google.gson.annotations.SerializedName;

public record ResponseNominatimDto(
        String lat,
        String lon,
        @SerializedName("display_name") String displayName
) {
}
