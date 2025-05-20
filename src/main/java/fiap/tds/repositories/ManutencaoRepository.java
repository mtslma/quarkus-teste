package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.Manutencao;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class ManutencaoRepository {

    private static final Logger logger = LogManager.getLogger(ManutencaoRepository.class);

    public void registrar(Manutencao manutencao) {
        var query = "INSERT INTO T_AUMO_MANUTENCAO (DT_CRIACAO, NM_MANUTENCAO, DS_MANUTENCAO, NV_PRIORIDADE, ID_ESTACAO, ID_LINHA) VALUES (?, ?, ?, ?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setTimestamp(1, Timestamp.valueOf(manutencao.getDataCriacao()));
            stmt.setString(2, manutencao.getNomeManutencao());
            stmt.setString(3, manutencao.getDescricaoManutencao());
            stmt.setString(4, manutencao.getNivelPrioridade().toUpperCase());
            stmt.setInt(5, manutencao.getIdEstacao());
            stmt.setInt(6, manutencao.getIdLinha());

            var res = stmt.executeQuery();

            if (res.next()){
                logger.info("✅ Manutencao registrada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar manutencao", e);
        }
    }

    public SearchResult<Manutencao> buscar(String nome, String descricao, String prioridade, String direction) {

        List<Manutencao> manutencoesFiltradas = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_AUMO_MANUTENCAO WHERE DELETED = 0");

        // Filtros
        if (nome != null && !nome.isBlank()) {
            query.append(" AND UPPER(NM_MANUTENCAO) LIKE UPPER(?)");
        }
        if (descricao != null && !descricao.isBlank()) {
            query.append(" AND UPPER(DS_MANUTENCAO) LIKE UPPER(?)");
        }
        if (prioridade != null && !prioridade.isBlank()) {
            query.append(" AND UPPER(NV_PRIORIDADE) = UPPER(?)");
        }

        // Ordenação de busca por ID
        if ("desc".equalsIgnoreCase(direction)) {
            query.append("ORDER BY ID_MANUTENCAO DESC");
        } else {
            query.append("ORDER BY ID_MANUTENCAO ASC");
        }

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (nome != null && !nome.isBlank()) {
                stmt.setString(paramIndex++, "%" + nome + "%");
            }
            if (prioridade != null && !prioridade.isBlank()) {
                stmt.setString(paramIndex++, prioridade);
            }

            var res = stmt.executeQuery();

            while (res.next()) {
                var manutencao = new Manutencao();

                manutencao.setId(res.getInt("ID_MANUTENCAO"));
                manutencao.setDeleted(res.getBoolean("DELETED"));
                manutencao.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                manutencao.setNomeManutencao(res.getString("NM_MANUTENCAO"));
                manutencao.setDescricaoManutencao(res.getString("DS_MANUTENCAO"));
                manutencao.setNivelPrioridade(res.getString("NV_PRIORIDADE"));
                manutencao.setIdLinha(res.getInt("ID_LINHA"));
                manutencao.setIdEstacao(res.getInt("ID_ESTACAO"));

                manutencoesFiltradas.add(manutencao);
            }

            logger.info("✅ Manutencoes buscadas com sucesso");
            totalItems = manutencoesFiltradas.size();

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar manutenções", e);
        }
        return new SearchResult<>(manutencoesFiltradas, totalItems);
    }

    public Manutencao buscarPorId(int id) {
        var query = "SELECT * FROM T_AUMO_MANUTENCAO WHERE ID_MANUTENCAO = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            var res = stmt.executeQuery();

            if (res.next()) {
                Manutencao manutencao = new Manutencao();
                manutencao.setId(res.getInt("ID_MANUTENCAO"));
                manutencao.setDeleted(res.getBoolean("DELETED"));
                manutencao.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                manutencao.setNomeManutencao(res.getString("NM_MANUTENCAO"));
                manutencao.setDescricaoManutencao(res.getString("DS_MANUTENCAO"));
                manutencao.setNivelPrioridade(res.getString("NV_PRIORIDADE"));
                manutencao.setIdLinha(res.getInt("ID_LINHA"));
                manutencao.setIdEstacao(res.getInt("ID_ESTACAO"));

                logger.info("✅ Manutenção encontrada com sucesso");
                return manutencao;
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar manutenção com ID {}", id, e);
        }

        return null;
    }

    public void atualizar(int id, Manutencao manutencao) {
        var query = "UPDATE T_AUMO_MANUTENCAO " +
                "SET NM_MANUTENCAO = ?, DS_MANUTENCAO = ?, NV_PRIORIDADE = ?, " +
                "ID_ESTACAO = ?, ID_LINHA = ? " +
                "WHERE ID_MANUTENCAO = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, manutencao.getNomeManutencao());
            stmt.setString(2, manutencao.getDescricaoManutencao());
            stmt.setString(3, manutencao.getNivelPrioridade().toUpperCase());
            stmt.setInt(4, manutencao.getIdEstacao());
            stmt.setInt(5, manutencao.getIdLinha());
            stmt.setInt(6, id);

            int res = stmt.executeUpdate();
            if (res > 0) {
                logger.info("✅ Manutenção atualizada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao atualizar manutenção", e);
        }
    }

    public void deletar(int idManutencao) {
        var query = "UPDATE T_AUMO_MANUTENCAO SET DELETED = 1 WHERE ID_MANUTENCAO = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idManutencao);

            int res = stmt.executeUpdate();
            if (res > 0) {
                logger.info("✅ Manutenção marcada como deletada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao deletar manutenção", e);
        }
    }
}
