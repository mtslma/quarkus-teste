package fiap.tds.dtos.usuarioDto;

import fiap.tds.entities.AutenticaUsuario;

public record CreateUsuarioDto(
        String nomeUsuario,
        String tipoUsuario,
        String telefoneContato,
        int idCidade,
        AutenticaUsuario autenticaUsuario
) {
    public CreateUsuarioDto {
    }
}
