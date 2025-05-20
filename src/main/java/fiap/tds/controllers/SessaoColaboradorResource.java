package fiap.tds.controllers;

import fiap.tds.dtos.sessaoColaboradorDto.SessaoColaboradorResponseDto;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.services.SessaoColaboradorService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/sessao")
public class SessaoColaboradorResource {

    private static final Logger logger = LogManager.getLogger(SessaoColaboradorResource.class);
    private final SessaoColaboradorService service = new SessaoColaboradorService();

    @GET
    @Path("/{token}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarSessaoPorToken(@PathParam("token") String token) {
        logger.info("🔍 Buscando colaborador pelo token {}", token);

        try{
            var sessao = service.buscarColaboradorPorToken(token);
            return Response.status(Response.Status.OK).entity(sessao).build();
        } catch (NotFoundException e){
            logger.info("❌ Erro ao buscar por colaborador da sessão: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        } catch (Exception e){
            logger.info("❌ Erro ao buscar por colaborador da sessão: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity(e.getMessage()).build();
        }

    }

    @PUT
    @Path("/logout")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response logoutSessao(SessaoColaboradorResponseDto dto) {
        logger.info("Finalizando sessão para o token {}", dto.tokenSessao());
        try {
            service.logoutSessao(dto);
            return Response.status(Response.Status.OK).entity("Sessão finalizada com sucesso").build();
        }catch (fiap.tds.exceptions.NotFoundException e) {
            logger.error("❌ Erro ao finalizar sessão", e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();
        } catch (fiap.tds.exceptions.BadRequestException e){
            logger.error("❌ Erro ao finalizar sessão", e);
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("❌ Erro ao finalizar sessão", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao finalizar sessão").build();
        }
    }
}
