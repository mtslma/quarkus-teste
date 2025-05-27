package fiap.tds.services;

import fiap.tds.dtos.sessaoUsuarioDto.ResponseSessaoUsuarioDto;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.SessaoUsuarioRepository;
import fiap.tds.repositories.UsuarioRepository;


public class SessaoUsuarioService {

    private final SessaoUsuarioRepository sessaoRepository = new SessaoUsuarioRepository();
    private final UsuarioRepository usuarioRepository = new UsuarioRepository();
    private final UsuarioService usuarioService = new UsuarioService();

    // Função para buscar informações de um colaborador por meio do token
    public ResponseSessaoUsuarioDto buscarColaboradorPorToken(String token) {
        var sessaoExistente = sessaoRepository.buscarSessao(token);
        if (sessaoExistente == null){
            throw new NotFoundException("Sessão não encontrada");
        }
        var idColaborador = usuarioRepository.buscarIdColaboradorPorToken(token);

        var usuarioDto = usuarioService.buscarPorId(idColaborador);

        // Retornando o DTO de sessão bem bonito
        return new ResponseSessaoUsuarioDto(
                sessaoExistente.getTokenSessao(),
                sessaoExistente.getStatusSessao(),
                sessaoExistente.getDataLogin(),
                sessaoExistente.getDataLogout(),
                usuarioDto
        );
    }

    public void logoutSessao(ResponseSessaoUsuarioDto dto) {

        if (dto.tokenSessao() == null){
            throw new BadRequestException("Informe o token da sessão a ser encerrada");
        }

        var sessaoExistente = sessaoRepository.buscarSessao(dto.tokenSessao());

        if (sessaoExistente == null){
            throw new NotFoundException("Sessão não encontrada");
        }

        if (dto.tokenSessao().length() != 36){
            throw new BadRequestException("Token de sessão inválido");
        }

        if (sessaoExistente.getStatusSessao().equalsIgnoreCase("INATIVA")) {
            throw new BadRequestException("A sessão já foi encerrada anteriormente.");
        }

        sessaoRepository.deletarSessao(dto.tokenSessao());
    }
}
