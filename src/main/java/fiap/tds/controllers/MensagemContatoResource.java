package fiap.tds.controllers;

import fiap.tds.dtos.mensagemContato.MensagemContatoCreateDto;
import fiap.tds.services.MensagemContatoService;

import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("mensagem")
public class MensagemContatoResource {

    private static final Logger logger = LogManager.getLogger(MensagemContatoResource.class);
    private final MensagemContatoService service = new MensagemContatoService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar(MensagemContatoCreateDto mensagemContatoCreateDto) {
        logger.info("Iniciando registro de nova mensagem de contato");
        try {
            service.registrar(mensagemContatoCreateDto);
            return Response.status(Response.Status.CREATED).entity("Mensagem enviada com sucesso").build();
        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.error("❌ Dados inválidos: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao registrar a mensagem", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao registrar a mensagem.").build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("name") String name,
            @QueryParam("text") String text,
            @QueryParam("email") String email,
            @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        logger.info("Iniciando busca de mensagens de contato");

        try {
            var resultado = service.buscar(page, name, text, email, direction);
            return Response.ok(resultado).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar mensagens", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar mensagens de contato.")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletar(@PathParam("id") int id) {
        logger.info("Deletando mensagem de contato por ID: {}", id);

        try {
            service.deletar(id);
            return Response.ok("Mensagem deletada com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao deletar mensagem de contato", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao tentar excluir a mensagem.").build();
        }
    }
}
