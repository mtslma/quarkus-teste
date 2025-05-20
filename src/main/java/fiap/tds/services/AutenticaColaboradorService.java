package fiap.tds.services;

import fiap.tds.dtos.autenticaColaborador.AutenticaColaboradorDto;
import fiap.tds.entities.AutenticaColaborador;
import fiap.tds.entities.SessaoColaborador;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.repositories.AutenticaColaboradorRepository;
import fiap.tds.repositories.ColaboradorRepository;
import fiap.tds.repositories.SessaoColaboradorRepository;

import java.util.Optional;
import java.util.UUID;

public class AutenticaColaboradorService {

    private final ColaboradorRepository colaboradorRepository = new ColaboradorRepository();
    private final AutenticaColaboradorRepository autenticaColaboradorRepository = new AutenticaColaboradorRepository();
    private final SessaoColaboradorRepository sessaoRepository = new SessaoColaboradorRepository();

    // Função para autenticar login de um usuário
    public Optional<SessaoColaborador> autenticar(AutenticaColaboradorDto dto) {
        AutenticaColaborador colaboradorExistente = autenticaColaboradorRepository.autenticarColaborador(dto);

        if (dto.email() == null || dto.senha() == null){
            throw new BadRequestException("É necessário informar um email e senha");
        }

        if (colaboradorExistente != null) {
            SessaoColaborador novaSessao = new SessaoColaborador();
            novaSessao.setIdColaborador(colaboradorExistente.getId_colaborador());
            novaSessao.setTokenSessao(UUID.randomUUID().toString());
            novaSessao.setStatusSessao("ATIVA");

            sessaoRepository.registrarSessao(novaSessao);

            return Optional.of(novaSessao);
        }

        return Optional.empty();
    }
}
