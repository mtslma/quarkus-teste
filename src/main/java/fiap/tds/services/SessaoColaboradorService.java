package fiap.tds.services;

import fiap.tds.dtos.sessaoColaboradorDto.SessaoColaboradorResponseDto;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.ColaboradorRepository;
import fiap.tds.repositories.SessaoColaboradorRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SessaoColaboradorService {

    private static final Logger log = LoggerFactory.getLogger(SessaoColaboradorService.class);
    private final SessaoColaboradorRepository sessaoRepository = new SessaoColaboradorRepository();
    private final ColaboradorRepository colaboradorRepository = new ColaboradorRepository();
    private final ColaboradorService colaboradorService = new ColaboradorService();

    // Função para buscar informações de um colaborador por meio do token
    public SessaoColaboradorResponseDto buscarColaboradorPorToken(String token) {
        var sessaoExistente = sessaoRepository.buscarSessao(token);
        if (sessaoExistente == null){
            throw new NotFoundException("Sessão não encontrada");
        }
        var idColaborador = colaboradorRepository.buscarIdColaboradorPorToken(token);
        var colaboradorDto = colaboradorService.buscarPorId(idColaborador);
        // Retornando o DTO de sessão bem bonito
        return new SessaoColaboradorResponseDto(
                sessaoExistente.getTokenSessao(),
                sessaoExistente.getStatusSessao(),
                sessaoExistente.getDataLogin(),
                sessaoExistente.getDataLogout(),
                colaboradorDto
        );
    }

    public void logoutSessao(SessaoColaboradorResponseDto dto) {

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
