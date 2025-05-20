package fiap.tds.controllers;

import fiap.tds.dtos.alerta.AlertaCreateDto;
import fiap.tds.services.AlertaService;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.jboss.logging.Logger;

@Path("alerta")
public class AlertaResource {

    // Logger
    private static final Logger logger = Logger.getLogger(AlertaResource.class);

    // Service
    private final AlertaService alertaService = new AlertaService();

    @POST
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response registrar(AlertaCreateDto alertaCreateDto) {
        logger.info("Iniciando registro de alerta...");
        try {
            alertaService.registrar(alertaCreateDto);
            return Response.status(Response.Status.CREATED)
                    .entity("Alerta registrado com sucesso.")
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            logger.error("❌ Erro ao registrar alerta: " + e.getMessage());
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (RuntimeException e) {
            logger.error("❌ Erro ao registrar alerta: " + e.getMessage());
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro inesperado ao registrar linha. Tente novamente mais tarde.")
                    .build();
        }
    }

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("nome") String nome,
            @QueryParam("nivel") String nivel,
            @QueryParam("descricao") String descricao,
            @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        logger.info("Iniciando busca de alertas...");
        try {
            var resultado = alertaService.buscar(page, nome, nivel, descricao, direction);
            return Response.ok(resultado)
                    .build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar alertas.")
                    .build();
        }
    }

    @GET
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscarPorId(@PathParam("id") int id) {
        logger.info("Iniciando busca de alerta por ID...");
        try {
            var alerta = alertaService.buscarPorId(id);

            return Response.ok(alerta).build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity("Alerta não encontrado.").build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar alerta.").build();
        }
    }

    @PUT
    @Path("/{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") int id, AlertaCreateDto alertaCreateDto) {
        logger.info("Iniciando atualização de alerta...");
        try {
            alertaService.atualizar(id, alertaCreateDto);
            return Response.ok("Alerta atualizado com sucesso.").build();

        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao atualizar alerta.")
                    .build();
        }
    }


    @DELETE
    @Path("/{id}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response deletar(@PathParam("id") int id) {
        logger.info("Iniciando deleção de alerta...");
        try {
            alertaService.deletar(id);
            return Response.ok()
                    .entity("Alerta deletado com sucesso")
                    .build();
        }catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();
        } catch (Exception e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao deletar alerta")
                    .build();
        }
    }
}
