package fiap.tds.dtos;

import com.google.gson.annotations.SerializedName;

public record ResponseViaCepDto(
        String cep,
        String logradouro,
        String complemento,
        String bairro,
        @SerializedName("localidade") String localidade, // A anotação vai aqui para o componente do record
        String uf,
        String ibge,
        String gia,
        String ddd,
        String siafi,
        boolean erro // Gson tratará como false se o campo "erro" não estiver no JSON
) {
}
