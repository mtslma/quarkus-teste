package fiap.tds.controllers;

import fiap.tds.dtos.manutencao.ManutencaoCreateDto;
import fiap.tds.services.ManutencaoService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("manutencao")
public class ManutencaoResource {

    private static final Logger logger = LogManager.getLogger(ManutencaoResource.class);
    private final ManutencaoService manutencaoService = new ManutencaoService();

    @POST
    @Produces(MediaType.APPLICATION_JSON)
    @Consumes(MediaType.APPLICATION_JSON)
    public Response registrar(ManutencaoCreateDto manutencaoCreateDto) {
        logger.info("Iniciando registro manutencao... ");
        try {
            manutencaoService.registrar(manutencaoCreateDto);
            return Response.status(Response.Status.CREATED)
                    .entity("Manutenção registrada com sucesso")
                    .build();
        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.error("❌ Dados inválidos: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("❌ Erro inesperado ao registrar a manutenção", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao registrar a manutenção.")
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("nome") String nome,
            @QueryParam("descricao") String descricao,
            @QueryParam("prioridade") String prioridade,
            @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        logger.info("Iniciando busca por manutenções...");
        try {
            var resultado = manutencaoService.buscar(page, nome, descricao, prioridade, direction);
            return Response.ok(resultado).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar manutenções", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar manutenções.")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    public Response buscarPorId(@PathParam("id") int id) {
        logger.info("Iniciando busca de manutenção por ID {}", id);

        try{
            var manutencao = manutencaoService.buscarPorId(id);
            return Response.ok(manutencao).build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage()).build();

        } catch (Exception e){
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Erro ao buscar manutenção").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") int id, ManutencaoCreateDto manutencaoCreateDto) {

        logger.info("Iniciando atualizacao de manutencao");
        try {

            manutencaoService.atualizar(id, manutencaoCreateDto);
            return Response.ok("Manutenção atualizada com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            logger.info("❌ Erro ao atualizar manutenção: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.info("❌ Erro ao atualizar manutenção: {}", e.getMessage());

            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.info("❌ Erro ao atualizar manutenção: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar manutenção.")
                    .build();
        }
    }

    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletar(@PathParam("id") int id) {

        logger.info("Iniciando deleção de manutenção por ID {}", id);
        try {
            manutencaoService.deletar(id);
            logger.info("✅ Manutenção deletada com sucesso!");
            return Response.ok()
                    .entity("Manutenção deletada com sucesso")
                    .build();
        }catch (fiap.tds.exceptions.NotFoundException e) {
            logger.error("❌ Erro ao deletar manutenção com ID: {}", e.getMessage());
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            logger.error("❌ Erro ao deletar manutenção com ID: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao deletar manutenção")
                    .build();
        }
    }
}
