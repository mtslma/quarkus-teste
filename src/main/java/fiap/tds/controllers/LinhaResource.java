package fiap.tds.controllers;

import fiap.tds.dtos.linha.LinhaCreateDto;
import fiap.tds.services.LinhaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("/linha")
public class LinhaResource {

    // Logger
    private static final Logger logger = LogManager.getLogger(LinhaResource.class);

    // Service
    private final LinhaService linhaService = new LinhaService();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrar(LinhaCreateDto createDto) {
        logger.info("Iniciando registro de linha...");
        try {

            linhaService.registrar(createDto);
            return Response.status(Response.Status.CREATED)
                    .entity("Linha registrada com sucesso")
                    .build();
        } catch (fiap.tds.exceptions.BadRequestException e) {

            logger.error("❌ Erro ao registrar linha: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {

            logger.error("❌ Erro ao registrar linha: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao registrar a linha.")
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("nome") String nome,
            @QueryParam("status") String status,
            @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        logger.info("Iniciando busca por alertas...");
        try {
            var resultado = linhaService.buscar(page, nome, status, direction);
            return Response.ok(resultado).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar linhas", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar linhas.")
                    .build();
        }
    }



    @GET
    @Path("{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        logger.info("Iniciando busca de linha por ID");

        try{
            var linha = linhaService.buscarPorId(id);
            return Response.ok(linha).build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();

        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar linha").build();
        }
    }

    @PUT
    @Path("status/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizarStatusLinha(@PathParam("id") int id, LinhaCreateDto linhaCreateDto) {
        logger.info("Atualizando status da linha...");

        try {
            linhaService.atualizarStatusLinha(id, linhaCreateDto);
            return Response.ok("Status da linha atualizada com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {

            logger.info("❌ Erro ao atualizar status da linha: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {

            logger.info("❌ Erro ao atualizar status da linha: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {

            logger.info("❌ Erro ao atualizar status da linha: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar status da linha.")
                    .build();
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") int id, LinhaCreateDto linhaCreateDto) {
        logger.info("Atualizando linha...");

        try {
            linhaService.atualizar(id, linhaCreateDto);
            return Response.ok("Linha atualizada com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {

            logger.info("❌ Erro ao atualizar linha: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.info("❌ Erro ao atualizar linha: {}", e.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.info("❌ Erro ao atualizar linha: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar linha.")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletarLinha(@PathParam("id") int id) {
        logger.info("Iniciando deleção lógica da linha");
        try {
            linhaService.deletar(id);
            logger.info("✅ Linha deletada com sucesso!");
            return Response.ok()
                    .entity("Linha deletada com sucesso")
                    .build();
        }catch (fiap.tds.exceptions.NotFoundException e) {
            logger.error("❌ Erro ao deletar linha com ID: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("❌ Erro ao deletar linha com ID: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao deletar linha")
                    .build();
        }
    }
}
