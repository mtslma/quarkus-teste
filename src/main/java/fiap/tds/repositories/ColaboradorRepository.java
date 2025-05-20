package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.AutenticaColaborador;
import fiap.tds.entities.Colaborador;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ColaboradorRepository {

    private static final Logger logger = LogManager.getLogger(ColaboradorRepository.class);

    // Chamando o repository de AutenticaColaborador para usar as funcionalidades dele aqui e conseguir separar as tabelas
    AutenticaColaboradorRepository autenticaColaboradorRepository = new AutenticaColaboradorRepository();

    // Função para registrar novos colaboradores
    public void registrar(Colaborador object){

        var query = "INSERT INTO T_AUMO_COLABORADOR (DELETED, DT_CRIACAO, NM_COLABORADOR, TP_COLABORADOR)" +
                " VALUES (?, ?, ?, ?)";


        // Conexão com o banco de dados
        try (var conn = DatabaseConfig.getConnection()) {

            var stmt = conn.prepareStatement(query, new String[] { "ID_COLABORADOR"}); // Isso faz retornar o ID que eu preciso pra relacionar na tabela de autentica

            // Setando os parâmetros para inserção do colaborador
            stmt.setInt(1, 0);
            stmt.setTimestamp(2, Timestamp.valueOf(object.getDataCriacao()));
            stmt.setString(3, object.getNomeColaborador());
            stmt.setString(4, object.getTipoColaborador());

            // Executando a inserção
            var res = stmt.executeUpdate();

            if (res > 0){
                // Pegando o ID gerado pelo banco e executando a função do AutenticaColaborador para registrar a autenticação
                try (var generatedKeys = stmt.getGeneratedKeys()){
                    if (generatedKeys.next()){
                        var idGerado = generatedKeys.getInt(1); // Isso busca o ID gerado pelo banco
                        logger.info("✅ Colaborador registrado com sucesso, ID: {}", idGerado);

                        // Chamar a função do AutenticaColaboradorRepository aqui!
                        AutenticaColaborador autenticaColaborador = object.getAutenticaColaborador();
                        autenticaColaborador.setId_colaborador(idGerado);
                        autenticaColaboradorRepository.registrarAutenticaColaborador(autenticaColaborador);
                    }
                } catch (Exception e) {
                    logger.info("❌ Erro ao obter ID gerado pelo banco");
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar colaborador \n{}", e.getMessage());
        }
    }

    // Função para buscar colaboradores
    public SearchResult<Colaborador> buscar(String nome, String tipo, String direction) {
        List<Colaborador> colaboradoresFiltrados = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_AUMO_COLABORADOR WHERE DELETED = 0");

        // Filtros
        if (nome != null && !nome.isBlank()) {
            query.append(" AND UPPER(NM_COLABORADOR) LIKE UPPER(?)");
        }
        if (tipo != null && !tipo.isBlank()) {
            query.append(" AND UPPER(TP_COLABORADOR) LIKE UPPER(?)");
        }

        // Ordenação
        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY ID_COLABORADOR DESC");
        } else {
            query.append(" ORDER BY ID_COLABORADOR ASC");
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
                Colaborador colaborador = new Colaborador();
                colaborador.setId(res.getInt("ID_COLABORADOR"));
                colaborador.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime()); // tomar cuidado pra não esquecer isso ;)
                colaborador.setNomeColaborador(res.getString("NM_COLABORADOR"));
                colaborador.setTipoColaborador(res.getString("TP_COLABORADOR"));
                colaborador.setDeleted(res.getBoolean("DELETED"));
                colaboradoresFiltrados.add(colaborador);
            }

            logger.info("✅ Colaboradores buscados com sucesso");
            totalItems = colaboradoresFiltrados.size();

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar colaboradores \n{}", e.getMessage());
        }

        return new SearchResult<>(colaboradoresFiltrados, totalItems);

    }

    // Função para pegar o ID de um usuário por token de sessão
    public Integer buscarIdColaboradorPorToken(String token) {
        String query = "SELECT ID_COLABORADOR FROM T_AUMO_SESSAO_COLABORADOR WHERE TOKEN_SESSAO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, token);

            var res = stmt.executeQuery();

            if (res.next()) {
                return res.getInt("ID_COLABORADOR");
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar colaborador pelo token \n{}", e.getMessage());
        }
        return null;
    }

    public Colaborador buscarPorId(int id) {
        var query = "SELECT * FROM T_AUMO_COLABORADOR WHERE ID_COLABORADOR = ? AND DELETED = 0";

        // Conexão com o banco de dados
        try (var conn = DatabaseConfig.getConnection()){
             var stmt = conn.prepareStatement(query);

            stmt.setInt(1, id); // Configura o ID na query

            var rs = stmt.executeQuery();

            if (rs.next()) {
                Colaborador colaborador = new Colaborador();
                colaborador.setId(rs.getInt("ID_COLABORADOR"));
                colaborador.setDeleted(rs.getBoolean("DELETED"));
                colaborador.setDataCriacao(rs.getTimestamp("DT_CRIACAO").toLocalDateTime()); // importante não esquecer isso
                colaborador.setNomeColaborador(rs.getString("NM_COLABORADOR"));
                colaborador.setTipoColaborador(rs.getString("TP_COLABORADOR"));

                logger.info("✅ Colaborador buscado com sucesso");
                return colaborador;
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar por colaborador com ID {}", id);
        }
        return null;
    }

    public void atualizar(int id, Colaborador colaborador) {

        String query = "UPDATE T_AUMO_COLABORADOR SET NM_COLABORADOR = ?, TP_COLABORADOR = ? WHERE ID_COLABORADOR = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, colaborador.getNomeColaborador());
            stmt.setString(2, colaborador.getTipoColaborador());
            stmt.setInt(3, id);

            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Colaborador atualizado com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao atualizar colaborador \n{}", e.getMessage());
        }
    }

    public void deletar(int id) {
        logger.info("Iniciando deleção do colaborador por ID {}", id);

        String query = "UPDATE T_AUMO_COLABORADOR SET DELETED = 1 WHERE ID_COLABORADOR = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);

            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Colaborador marcado como deletado com sucesso");
            } else {
                logger.warn("⚠️ Nenhum colaborador encontrado com ID {}", id);
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao excluir colaborador \n{}", e.getMessage());
        }
    }
}
