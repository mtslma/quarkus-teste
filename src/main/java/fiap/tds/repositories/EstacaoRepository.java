package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.Estacao;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class EstacaoRepository {

    private static final Logger logger = LogManager.getLogger(EstacaoRepository.class);

    public void registrar(Estacao estacao) {
        var query = "INSERT INTO T_AUMO_ESTACAO (ID_LINHA, DT_CRIACAO, NM_ESTACAO, ST_ESTACAO, HR_FUNCIONAMENTO_INICIO, HR_FUNCIONAMENTO_FIM) VALUES (?, ?, ?, ?, ?, ?)";

        // Conexão com o banco de dados
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            // Define uma data base fixa: 1970-01-01 HH:mm
            LocalDate dataBase = LocalDate.of(1970, 1, 1);
            LocalDateTime inicio = LocalDateTime.of(dataBase, estacao.getInicioOperacao());
            LocalDateTime fim = LocalDateTime.of(dataBase, estacao.getFimOperacao());

            // Converte para java.sql.Timestamp
            Timestamp timestampInicio = Timestamp.valueOf(inicio);
            Timestamp timestampFim = Timestamp.valueOf(fim);

            // Definindo os campos
            stmt.setInt(1, estacao.getIdLinha() );
            stmt.setTimestamp(2, Timestamp.valueOf(estacao.getDataCriacao()));
            stmt.setString(3, estacao.getNomeEstacao());
            stmt.setString(4, estacao.getStatusEstacao());
            stmt.setTimestamp(5, timestampInicio);
            stmt.setTimestamp(6, timestampFim);

            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Estação registrada com sucesso!");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar estação", e);
        }
    }


    public SearchResult<Estacao> buscar(String nome, String status, String direction) {
        List<Estacao> estacoesFiltradas = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_AUMO_ESTACAO WHERE DELETED = 0");

        // Filtros
        if (nome != null && !nome.isBlank()) {
            query.append(" AND UPPER(NM_ESTACAO) LIKE UPPER(?)");
        }
        if (status != null && !status.isBlank()) {
            query.append(" AND UPPER(ST_ESTACAO) LIKE UPPER(?)");
        }

        // Ordenação
        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY ID_ESTACAO DESC");
        } else {
            query.append(" ORDER BY ID_ESTACAO ASC");
        }

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (nome != null && !nome.isBlank()) {
                stmt.setString(paramIndex++, "%" + nome + "%");
            }
            if (status != null && !status.isBlank()) {
                stmt.setString(paramIndex++, "%" + status + "%");
            }

            var res = stmt.executeQuery();

            while (res.next()) {
                var estacao = new Estacao();
                estacao.setId(res.getInt("ID_ESTACAO"));
                estacao.setIdLinha(res.getInt("ID_LINHA"));
                estacao.setNomeEstacao(res.getString("NM_ESTACAO"));
                estacao.setStatusEstacao(res.getString("ST_ESTACAO"));
                estacao.setDeleted(res.getBoolean("DELETED"));
                estacao.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                estacao.setInicioOperacao(res.getTimestamp("HR_FUNCIONAMENTO_INICIO").toLocalDateTime().toLocalTime());
                estacao.setFimOperacao(res.getTimestamp("HR_FUNCIONAMENTO_FIM").toLocalDateTime().toLocalTime());

                estacoesFiltradas.add(estacao);
            }

            logger.info("✅ Estacoes buscadas com sucesso");
            totalItems = estacoesFiltradas.size();

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar estações \n{}", e.getMessage());
        }

        return new SearchResult<>(estacoesFiltradas, totalItems);
    }


    public Estacao buscarPorId(int id) {
        var query = "SELECT * FROM T_AUMO_ESTACAO WHERE ID_ESTACAO = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection()){
            var stmt = conn.prepareStatement(query);

            stmt.setInt(1, id);  // Configura o ID na query
            var rs = stmt.executeQuery();

            if (rs.next()) {
                Estacao estacao = new Estacao();
                estacao.setId(rs.getInt("ID_ESTACAO"));
                estacao.setDeleted(rs.getBoolean("DELETED"));
                estacao.setDataCriacao(rs.getTimestamp("DT_CRIACAO").toLocalDateTime());
                estacao.setNomeEstacao(rs.getString("NM_ESTACAO"));
                estacao.setStatusEstacao(rs.getString("ST_ESTACAO"));
                estacao.setInicioOperacao(rs.getTimestamp("HR_FUNCIONAMENTO_INICIO").toLocalDateTime().toLocalTime());
                estacao.setFimOperacao(rs.getTimestamp("HR_FUNCIONAMENTO_FIM").toLocalDateTime().toLocalTime());
                estacao.setIdLinha(rs.getInt("ID_LINHA"));

                logger.info("✅ Estacao buscada com sucesso");
                return estacao;
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar estação por ID {}: {}", id, e.getMessage());
        }
        return null;
    }

    public void atualizarStatus(int id, String status){
        var query = "UPDATE T_AUMO_ESTACAO SET ST_ESTACAO = ? WHERE ID_ESTACAO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            var res = stmt.executeUpdate();

            // Verifica se alguma linha foi atualizada
            if (res > 0) {
                logger.info("✅ Status da linha com ID {} atualizado com sucesso");
            }

        } catch (SQLException e) {
            logger.error("Erro ao atualizar linha no banco de dados", e);
            throw new RuntimeException("Erro ao atualizar linha", e);
        }
    }

    public void atualizar(int id, Estacao estacao) {
        String query = "UPDATE T_AUMO_ESTACAO SET NM_ESTACAO = ?, ST_ESTACAO = ?, HR_FUNCIONAMENTO_INICIO = ?, HR_FUNCIONAMENTO_FIM = ?, ID_LINHA = ? WHERE ID_ESTACAO = ? AND DELETED = 0"; // Garante que a estação não esteja deletada

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            // Define data base fixa para Timestamp
            LocalDate dataBase = LocalDate.of(1970, 1, 1);
            LocalDateTime inicio = LocalDateTime.of(dataBase, estacao.getInicioOperacao());
            LocalDateTime fim = LocalDateTime.of(dataBase, estacao.getFimOperacao());

            // Preenche os parâmetros
            stmt.setString(1, estacao.getNomeEstacao());
            stmt.setString(2, estacao.getStatusEstacao());
            stmt.setTimestamp(3, Timestamp.valueOf(inicio));
            stmt.setTimestamp(4, Timestamp.valueOf(fim));
            stmt.setInt(5, estacao.getIdLinha());
            stmt.setInt(6, id);

            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Estação com ID {} atualizada com sucesso", estacao.getId());
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao atualizar estação no banco de dados", e);
            throw new RuntimeException("Erro ao atualizar estação", e);
        }
    }

    public void deletarEstacao(int idEstacao) {
        var query = "UPDATE T_AUMO_ESTACAO SET DELETED = 1 WHERE ID_ESTACAO = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, idEstacao);

            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Estação marcada como deletada com sucesso!");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao marcar a estação como deletada", e);
        }
    }
}