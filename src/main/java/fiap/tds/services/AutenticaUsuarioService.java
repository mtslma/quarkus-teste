package fiap.tds.services;

import fiap.tds.dtos.autenticaUsuarioDto.AutenticaUsuarioDto;
import fiap.tds.entities.SessaoUsuario;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.repositories.AutenticaUsuarioRepository;
import fiap.tds.repositories.SessaoUsuarioRepository;

import java.util.Optional;
import java.util.UUID;

public class AutenticaUsuarioService {

    private final AutenticaUsuarioRepository autenticaUsuarioRepository = new AutenticaUsuarioRepository();
    private final SessaoUsuarioRepository sessaoRepository = new SessaoUsuarioRepository();

    // Função para autenticar login de um usuário
    public Optional<SessaoUsuario> autenticar(AutenticaUsuarioDto dto) {

        var usuarioExistente = autenticaUsuarioRepository.autenticar(dto);

        if (dto.emailUsuario() == null || dto.senhaUsuario() == null){
            throw new BadRequestException("É necessário informar um email e senha");
        }

        if (usuarioExistente != null) {
            SessaoUsuario novaSessao = new SessaoUsuario();
            novaSessao.setIdUsuario(usuarioExistente.getIdUsuario());
            novaSessao.setTokenSessao(UUID.randomUUID().toString());
            novaSessao.setStatusSessao("ATIVA");

            sessaoRepository.registrarSessao(novaSessao);

            return Optional.of(novaSessao);
        }

        return Optional.empty();
    }
}
