package fiap.tds.controllers;

import fiap.tds.dtos.usuarioDto.CreateUsuarioDto;
import fiap.tds.services.UsuarioService;
import jakarta.ws.rs.*;
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

    @GET
    @Path("/search")
    @Produces(MediaType.APPLICATION_JSON)
    public Response buscar(
            @QueryParam("page") @DefaultValue("1") int page,
            @QueryParam("nome") String nome,
            @QueryParam("tipo") String tipo,
            @QueryParam("direction") @DefaultValue("asc") String direction
    ) {
        logger.info("🔍 Buscando usuários: page={}, nome={},, tipo={}, direction={}", page, nome, tipo, direction);

        try {
            var resultado = usuarioService.buscar(page, nome, tipo, direction);
            return Response.ok(resultado).build();

        } catch (fiap.tds.exceptions.BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST).entity(e.getMessage()).build();

        } catch (Exception e) {
            logger.error("❌ Erro ao buscar usuários", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao buscar usuários.")
                    .build();
        }
    }

    @GET
    @Path("{id}")
    public Response buscarColaboradorPorId(@PathParam("id") int id) {
        logger.info("Iniciando busca de colaborador com ID {}", id);

        try{

            var colaborador = usuarioService.buscarPorId(id);
            return Response.ok(colaborador).build();
        } catch (fiap.tds.exceptions.NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();

        }
    }

    @PUT
    @Path("{id}")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    public Response atualizar(@PathParam("id") int id, CreateUsuarioDto createUsuarioDto) {
        logger.info("Iniciando atualização de usuário...");

        try {
            usuarioService.atualizar(id, createUsuarioDto);
            return Response.ok("Usuário atualizado com sucesso.").build();

        } catch (NotFoundException e) {
            return Response.status(Response.Status.NOT_FOUND)
                    .entity(e.getMessage())
                    .build();

        } catch (BadRequestException e) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity(e.getMessage())
                    .build();

        } catch (Exception e) {
            logger.error("❌ Erro ao atualizar usuário", e);
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro interno ao atualizar usuário.")
                    .build();
        }
    }

    @DELETE
    @Path("{id}")
    public Response deletar(@PathParam("id") int id) {

        try {
            var existente = usuarioService.buscarPorId(id);
            if (existente == null) {
                return Response.status(Response.Status.NOT_FOUND)
                        .entity("Usuário não encontrado.")
                        .build();
            }

            usuarioService.deletar(id);
            return Response.ok("Usuário deletado com sucesso.").build();

        } catch (RuntimeException e) {
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                    .entity("Erro ao excluir usuário.")
                    .build();
        }
    }
}
