package fiap.tds.controllers;

import fiap.tds.dtos.autenticaColaborador.AutenticaColaboradorDto;
import fiap.tds.services.AutenticaColaboradorService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

import java.util.HashMap;
import java.util.Map;

@Path("/autenticacao")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
public class AutenticaColaboradorResource {

    private static final Logger logger = Logger.getLogger(AutenticaColaboradorResource.class);
    private final AutenticaColaboradorService autenticaService = new AutenticaColaboradorService();

    public int responderNao(){
        System.out.println("nao");
        return 1;
    }


    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    public Response autenticar(AutenticaColaboradorDto autenticaColaboradorDto) {
        logger.info("Iniciando tentativa de autenticação para o email: " + autenticaColaboradorDto.email());

        try {
            return autenticaService.autenticar(autenticaColaboradorDto)
                    .map(sessao -> {
                        logger.info("✅ Autenticação bem-sucedida para o email: " + autenticaColaboradorDto.email());

                        Map<String, String> resposta = new HashMap<>();
                        resposta.put("session-token", sessao.getTokenSessao());

                        return Response.ok(resposta).build();
                    })
                    .orElseGet(() -> {
                        logger.warn("❌ Falha na autenticação para o email: " + autenticaColaboradorDto.email());
                        return Response.status(Response.Status.UNAUTHORIZED)
                                .entity("Email ou senha inválidos").build();
                    });
        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.error("❌ Erro de requisição incorreta para o email: " + autenticaColaboradorDto.email(), e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()) // Mensagem personalizada de erro
                    .build();
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao autenticar para o email: " + autenticaColaboradorDto.email(), e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao processar a autenticação")
                    .build();
        }
    }
}
