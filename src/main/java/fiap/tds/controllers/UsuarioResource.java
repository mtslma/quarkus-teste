package fiap.tds.controllers;

import fiap.tds.dtos.usuarioDto.CreateUsuarioDto;
import fiap.tds.services.UsuarioService;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/usuario")
public class UsuarioResource {

    // Logger e service
    private static final Logger logger = LogManager.getLogger(UsuarioResource.class);
    private final UsuarioService usuarioService = new UsuarioService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar(CreateUsuarioDto createUsuarioDto) {
        logger.info("Iniciando registro de colaborador");

        try {
            usuarioService.registrar(createUsuarioDto);
            logger.info("✅ Usuário registrado com sucesso");

            return Response.status(Response.Status.CREATED)
                    .entity("Usuário registrado com sucesso.")
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.warn("❌ Falha de validação: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (RuntimeException e) {
            logger.error("❌ Erro ao registrar usuário: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao registrar usuário.")
                    .build();
        }
    }
}
