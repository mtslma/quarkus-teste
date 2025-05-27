package fiap.tds.services;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.usuarioDto.CreateUsuarioDto;
import fiap.tds.dtos.usuarioDto.ResponseUsuarioDto;
import fiap.tds.entities.Usuario;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.AutenticaUsuarioRepository;
import fiap.tds.repositories.UsuarioRepository;

import java.time.LocalDateTime;
import java.util.List;

public class UsuarioService {


    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final AutenticaUsuarioRepository autenticaUsuarioRepository = new AutenticaUsuarioRepository();

    // Registrar novo colaborador
        public void registrar(CreateUsuarioDto dto) {

        // Verificando se o objeto possui todos os campos necessários
        if (dto.nomeUsuario() == null || dto.telefoneContato() == null || dto.autenticaUsuario() == null) {
            throw new BadRequestException("Todos os campos obrigatórios devem ser preenchidos.");
        }

        // Verificando se a senha tem no mínimo 8 caracteres
        if (dto.autenticaUsuario().getSenhaUsuario().length() < 8 || dto.autenticaUsuario().getSenhaUsuario().length() > 30) {
            throw new BadRequestException("A senha deve ter no mínimo 8 e no máximo 30 caracteres");
        }

        // Verificando se o email já está em uso
        if (autenticaUsuarioRepository.validarEmailExistente(dto.autenticaUsuario().getEmailUsuario())) {
            throw new BadRequestException("O email informado já está cadastrado");
        }

        // Valiação do tipo
        if (!dto.tipoUsuario().equalsIgnoreCase("CLIENTE") && !dto.tipoUsuario().equalsIgnoreCase("COLABORADOR")) {
            throw new BadRequestException("O tipo de colaborador deve ser CLIENTE ou ADMIN");
        }

        // Convertendo DTO para objeto
        var usuario = new Usuario();
        usuario.setNomeUsuario(dto.nomeUsuario());
        usuario.setTipoUsuario(dto.tipoUsuario());
        usuario.setTelefoneContato(dto.telefoneContato());
        usuario.setIdCidade(dto.idCidade());
        usuario.setAutenticaUsuario(dto.autenticaUsuario());
        usuario.setDataCriacao(LocalDateTime.now());
        usuario.setDeleted(false);

        usuarioRepository.registrar(usuario);
    }

    // Função que converte Usuario em ResponseUsuarioDto
    private ResponseUsuarioDto converterColaboradorParaDto(Usuario a) {
        return new ResponseUsuarioDto(
                a.getIdUsuario(),
                a.isDeleted(),
                a.getDataCriacao(),
                a.getNomeUsuario(),
                a.getTipoUsuario(),
                a.getIdCidade(),
                a.getTelefoneContato()
        );
    }
}
