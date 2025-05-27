package fiap.tds.controllers;

import fiap.tds.dtos.autenticaUsuarioDto.AutenticaUsuarioDto;
import fiap.tds.repositories.AutenticaUsuarioRepository;
import fiap.tds.services.AutenticaUsuarioService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;


@Path("/autenticacao")
public class AutenticaUsuarioResource {

    // Logger
    private static final Logger logger = Logger.getLogger(AutenticaUsuarioResource.class);
    private final AutenticaUsuarioService autenticaService = new AutenticaUsuarioService();


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response autenticar(AutenticaUsuarioDto autenticaColaboradorDto) {
        logger.info("Iniciando tentativa de autenticação para o email: " + autenticaColaboradorDto.emailUsuario());

        try {
            return autenticaService.autenticar(autenticaColaboradorDto)
                    .map(sessao -> {
                        logger.info("✅ Autenticação bem-sucedida para o email: " + autenticaColaboradorDto.emailUsuario());

                        Map<String, String> resposta = new HashMap<>();
                        resposta.put("session-token", sessao.getTokenSessao());

                        return Response.ok(resposta).build();
                    })
                    .orElseGet(() -> {
                        logger.warn("❌ Falha na autenticação para o email: " + autenticaColaboradorDto.emailUsuario());
                        return Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Email ou senha inválidos").build();
                    });
        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.error("❌ Erro de requisição incorreta para o email: " + autenticaColaboradorDto.emailUsuario(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()) // Mensagem personalizada de erro
                    .build();
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao autenticar para o email: " + autenticaColaboradorDto.emailUsuario(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao processar a autenticação")
                    .build();
        }
    }

}
