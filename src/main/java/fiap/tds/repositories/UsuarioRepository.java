package fiap.tds.repositories;

import fiap.tds.dtos.SearchListDto;
import fiap.tds.dtos.SearchResult;
import fiap.tds.dtos.usuarioDto.ResponseUsuarioDto;
import fiap.tds.entities.AutenticaUsuario;
import fiap.tds.entities.Usuario;
import fiap.tds.exceptions.BadRequestException;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class UsuarioRepository {

    public static final Logger logger = LogManager.getLogger(UsuarioRepository.class);

    // Chamando o repository de AutenticaUsuario para usar as funcionalidades dele aqui e conseguir separar as tabelas
    AutenticaUsuarioRepository autenticaUsuarioRepository = new AutenticaUsuarioRepository();

    // Função para registrar usuário
    public void registrar(Usuario usuario){
        var query = "INSERT INTO T_POSE_USUARIO (DT_CRIACAO, NM_USUARIO, TP_USUARIO, ID_CIDADE, TELEFONE_CONTATO) VALUES (?, ?, ?, ?, ?) ";

        try (var conn = DatabaseConfig.getConnection()) {

            var stmt = conn.prepareStatement(query, new String[] { "ID_USUARIO"}); // Retorna o ID gerado pelo banco

            // Definindo os parâmetros
            stmt.setTimestamp(1, Timestamp.valueOf(usuario.getDataCriacao()));
            stmt.setString(2, usuario.getNomeUsuario());
            stmt.setString(3, usuario.getTipoUsuario());
            stmt.setInt(4, usuario.getIdCidade());
            stmt.setString(5, usuario.getTelefoneContato());

            var res = stmt.executeUpdate();

            if (res > 0){

                // Recuperando o ID gerado pelo banco de dados
                try (var generatedKeys = stmt.getGeneratedKeys()){
                    if (generatedKeys.next()){
                        var idGerado = generatedKeys.getInt(1); // Isso busca o ID gerado pelo banco
                        logger.info("✅ Usuário registrado com sucesso, ID: {}", idGerado);

                        // Chamar a função do AutenticaUsuario aqui!
                        AutenticaUsuario autenticaUsuario = usuario.getAutenticaUsuario();
                        autenticaUsuario.setIdUsuario(idGerado);
                        autenticaUsuarioRepository.registrar(autenticaUsuario);
                    }
                } catch (Exception e) {
                    logger.info("❌ Erro ao obter ID gerado pelo banco");
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar usuário: {}", e.getMessage());
        }
    }

    // Função para buscar usuários
    public SearchResult<Usuario> buscar(String nome, String tipo, String direction) {
        List<Usuario> usuariosBuscados = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_POSE_USUARIO WHERE DELETED = 0");

        // Filtros
        if (nome != null && !nome.isBlank()) {
            query.append(" AND UPPER(NM_USUARIO) LIKE UPPER(?)");
        }
        if (tipo != null && !tipo.isBlank()) {
            query.append(" AND UPPER(TP_USUARIO) LIKE UPPER(?)");
        }

        // Ordenação
        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY ID_USUARIO DESC");
        } else {
            query.append(" ORDER BY ID_USUARIO ASC");
        }

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (nome != null && !nome.isBlank()) {
                stmt.setString(paramIndex++, "%" + nome + "%");
            }
            if (tipo != null && !tipo.isBlank()) {
                stmt.setString(paramIndex++, "%" + tipo + "%");
            }

            var res = stmt.executeQuery();

            while (res.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(res.getInt("ID_USUARIO"));
                usuario.setDeleted(res.getBoolean("DELETED"));
                usuario.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime()); // tomar cuidado pra não esquecer isso ;)
                usuario.setNomeUsuario(res.getString("NM_USUARIO"));
                usuario.setTipoUsuario(res.getString("TP_USUARIO"));
                usuario.setTelefoneContato(res.getString("TELEFONE_CONTATO"));
                usuario.setIdCidade(res.getInt("ID_CIDADE"));
                usuariosBuscados.add(usuario);
            }

            logger.info("✅ Usuários buscados com sucesso");
            totalItems = usuariosBuscados.size();

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar usuários: {}", e.getMessage());
        }
        return new SearchResult<>(usuariosBuscados, totalItems);
    }

    // Função para pegar o ID de um usuário por token de sessão
    public Integer buscarIdColaboradorPorToken(String token) {
        String query = "SELECT ID_USUARIO FROM T_POSE_SESSAO_USUARIO WHERE TOKEN_SESSAO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, token);

            var res = stmt.executeQuery();

            if (res.next()) {
                return res.getInt("ID_USUARIO");
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar colaborador pelo token \n{}", e.getMessage());
        }
        return null;
    }

    public Usuario buscarPorId(int id) {
        var query = "SELECT * FROM T_POSE_USUARIO WHERE ID_USUARIO = ? AND DELETED = 0";

        // Conexão com o banco de dados
        try (var conn = DatabaseConfig.getConnection()){
            var stmt = conn.prepareStatement(query);

            stmt.setInt(1, id); // Configura o ID na query

            var rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setIdUsuario(rs.getInt("ID_USUARIO"));
                usuario.setDeleted(rs.getBoolean("DELETED"));
                usuario.setDataCriacao(rs.getTimestamp("DT_CRIACAO").toLocalDateTime()); // importante não esquecer isso
                usuario.setNomeUsuario(rs.getString("NM_USUARIO"));
                usuario.setTipoUsuario(rs.getString("TP_USUARIO"));
                usuario.setTelefoneContato(rs.getString("TELEFONE_CONTATO"));
                usuario.setIdCidade(rs.getInt("ID_CIDADE"));

                logger.info("✅ Colaborador buscado com sucesso");
                return usuario;
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar por colaborador com ID {}", id);
        }
        return null;
    }

    public void atualizar(int id, Usuario usuario) {

        String query = "UPDATE T_POSE_USUARIO SET NM_USUARIO = ?, TELEFONE_CONTATO = ?, ID_CIDADE = ? WHERE ID_USUARIO = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, usuario.getNomeUsuario());
            stmt.setString(2, usuario.getTelefoneContato());
            stmt.setInt(3, usuario.getIdCidade());
            stmt.setInt(4, id);

            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Colaborador atualizado com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao atualizar usuario \n{}", e.getMessage());
        }
    }

    public void deletar(int id) {
        logger.info("Iniciando deleção do usuário por ID {}", id);

        String query = "UPDATE T_POSE_USUARIO SET DELETED = 1 WHERE ID_USUARIO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Usuário deletado com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao excluir usuário \n{}", e.getMessage());
        }
    }

}
