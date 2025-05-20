package fiap.tds.controllers;

import fiap.tds.dtos.colaborador.ColaboradorCreateDto;
import fiap.tds.services.ColaboradorService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Path("colaborador")
public class ColaboradorResource {

    private static final Logger logger = LogManager.getLogger(ColaboradorResource.class);
    private final ColaboradorService colaboradorService = new ColaboradorService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar(ColaboradorCreateDto colaboradorCreateDto) {
        logger.info("Iniciando registro de colaborador");

        try {
            colaboradorService.registrar(colaboradorCreateDto);
            logger.info("✅ Colaborador registrado com sucesso");

            return Response.status(Response.Status.CREATED)
                    .entity("Colaborador registrado com sucesso.")
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.warn("❌ Falha de validação: {}", e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (RuntimeException e) {
            logger.error("❌ Erro ao registrar colaborador: {}", e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao registrar colaborador.")
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("nome") String nome,
            @QueryParam("tipo") String tipo,
            @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        logger.info("🔍 Buscando colaboradores: page={}, nome={},, tipo={}, direction={}", page, nome, tipo, direction);

        try {
            var resultado = colaboradorService.buscar(page, nome, tipo, direction);
            return Response.ok(resultado).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar colaboradores", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar colaboradores.")
                    .build();
        }
    }

    @GET
    @Path("{id}")
    public Response buscarColaboradorPorId(@PathParam("id") int id) {
        logger.info("Iniciando busca de colaborador com ID {}", id);

        try{

        var colaborador = colaboradorService.buscarPorId(id);
        return Response.ok(colaborador).build();
        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") int id, ColaboradorCreateDto colaboradorCreateDto) {
        logger.info("Iniciando atualização de colaborador...");

        try {
            colaboradorService.atualizar(id, colaboradorCreateDto);
            return Response.ok("Colaborador atualizado com sucesso.").build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.error("❌ Erro ao atualizar colaborador", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar colaborador.")
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deletar(@PathParam("id") int id) {

        try {
            var existente = colaboradorService.buscarPorId(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Colaborador não encontrado.")
                        .build();
            }

            colaboradorService.deletar(id);
            return Response.ok("Colaborador deletado com sucesso.").build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir colaborador.")
                    .build();
        }
    }
}
