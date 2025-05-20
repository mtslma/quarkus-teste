package fiap.tds.controllers;

import fiap.tds.dtos.estacao.EstacaoCreateDto;
import fiap.tds.services.EstacaoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("estacao")
public class EstacaoResource {

    // Logger
    private static final Logger logger = Logger.getLogger(EstacaoResource.class);

    // Service
    private final EstacaoService estacaoService = new EstacaoService();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrar(EstacaoCreateDto estacaoCreateDto) {
        logger.info("Iniciando registro de estação...");
        try {
            estacaoService.registrar(estacaoCreateDto);
            return Response.status(Response.Status.CREATED)
                    .entity("Estação registrada com sucesso")
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.error("❌ Erro ao registrar estação: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Dados inválidos: " + e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.error("❌ Erro ao registrar estação: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado ao registrar estação. Tente novamente mais tarde.")
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
        logger.info("Iniciando busca de estações...");
        try {
            var resultado = estacaoService.buscar(page, nome, status, direction);
            return Response.ok(resultado).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar estações", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar estações.")
                    .build();
        }
    }


    // GET - Buscar estação por ID
    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorId(@PathParam("id") int idEstacao) {

        try {
            var estacao = estacaoService.buscarPorId(idEstacao); // Chama o serviço para buscar a estação
            return Response.ok(estacao).build(); // Retorna 200 OK com o objeto da estação

        } catch (fiap.tds.exceptions.NotFoundException e) {
            // Se a estação não for encontrada
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build(); // Retorna 404 NOT FOUND com a mensagem de erro

        } catch (Exception e) {
            // Se ocorrer algum outro erro
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao buscar estação").build(); // Retorna 500 INTERNAL SERVER ERROR com a mensagem de erro
        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") int id, EstacaoCreateDto estacaoCreateDto) {
        logger.info("🔄 Atualizando status da estação");

        try {
            estacaoService.atualizar(id, estacaoCreateDto);
            return Response.ok("Status da estação atualizada com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar status da estação.")
                    .build();
        }
    }

    @PUT
    @Path("/status/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizarStatus(@PathParam("id") int id, EstacaoCreateDto estacaoCreateDto) {
        logger.info("🔄 Atualizando estação");

        try {
            estacaoService.atualizarStatus(id, estacaoCreateDto);
            return Response.ok("Estação atualizada com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar estação.")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletar(@PathParam("id") int id) {
        try {
            estacaoService.deletar(id);
            logger.info("✅ Estação deletada com sucesso!");
            return Response.ok()
                    .entity("Estação deletada com sucesso")
                    .build();
        } catch (fiap.tds.exceptions.NotFoundException e) {
            logger.error("❌ Erro ao deletar estação com ID: {}", id, e);
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("❌ Erro ao deletar estação com ID: {}", id, e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao deletar estação")
                    .build();
        }
    }
}
