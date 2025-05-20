package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.Linha;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class LinhaRepository {

    private static final Logger logger = LogManager.getLogger(LinhaRepository.class);

    // Função para registrar uma linha
    public void registrar(Linha linha) {
        var query = "INSERT INTO T_AUMO_LINHA (DT_CRIACAO, NM_LINHA, NR_LINHA, ST_LINHA) VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            // Configura os parâmetros do insert
            stmt.setTimestamp(1, Timestamp.valueOf(linha.getDataCriacao()));
            stmt.setString(2, linha.getNomeLinha());
            stmt.setInt(3, linha.getNumeroLinha());
            stmt.setString(4, linha.getStatusLinha());

            // Executa o insert
            var res = stmt.executeUpdate();
            logger.info(linha.getStatusLinha());

            if (res > 0){
                logger.info("✅ Linha registrada com sucesso!");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar linha", e);
        }
    }

    public SearchResult<Linha> buscar(String nome, String status, String direction) {
        List<Linha> linhasFiltradas = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_AUMO_LINHA WHERE DELETED = 0");

        // Filtros
        if (nome != null && !nome.isBlank()) {
            query.append(" AND UPPER(NM_LINHA) LIKE UPPER(?)");
        }
        if (status != null && !status.isBlank()) {
            query.append(" AND UPPER(ST_LINHA) LIKE UPPER(?)");
        }

        // Ordenação por número da linha
        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY NR_LINHA DESC");
        } else {
            query.append(" ORDER BY NR_LINHA ASC");
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
                var linha = new Linha();
                linha.setId(res.getInt("ID_LINHA"));
                linha.setNomeLinha(res.getString("NM_LINHA"));
                linha.setNumeroLinha(res.getInt("NR_LINHA"));
                linha.setStatusLinha(res.getString("ST_LINHA"));
                linha.setDeleted(res.getBoolean("DELETED"));
                linha.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());

                linhasFiltradas.add(linha);
            }

            logger.info("✅ Estacoes buscadas com sucesso");
            totalItems = linhasFiltradas.size();

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar estações \n{}", e.getMessage());
        }

        return new SearchResult<>(linhasFiltradas, totalItems);
    }

    // Função para buscar linha por ID
    public Linha buscarPorId(int id) {
        // Query da busca
        var query = "SELECT * FROM T_AUMO_LINHA WHERE ID_LINHA = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection()){
            var stmt = conn.prepareStatement(query);

            stmt.setInt(1, id); // Configura o ID na query

            var res = stmt.executeQuery();

            if (res.next()) {
                Linha linha = new Linha();
                linha.setId(res.getInt("ID_LINHA"));
                linha.setDeleted(res.getBoolean("DELETED"));
                linha.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                linha.setNomeLinha(res.getString("NM_LINHA"));
                linha.setNumeroLinha(res.getInt("NR_LINHA"));
                linha.setStatusLinha(res.getString("ST_LINHA"));

                logger.info("✅ Linha buscada com sucesso");
                return linha;
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar por linha com ID {}", id);
        }
        return null;
    }

    public void atualizarStatus(int id, String status){
        var query = "UPDATE T_AUMO_LINHA SET ST_LINHA = ? WHERE ID_LINHA = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, status);
            stmt.setInt(2, id);
            var res = stmt.executeUpdate();

            // Verifica se alguma linha foi atualizada
            if (res > 0) {
                logger.info("✅ Status da linha atualizado com sucesso");
            }

        } catch (SQLException e) {
            logger.error("Erro ao atualizar linha no banco de dados", e);
            throw new RuntimeException("Erro ao atualizar linha", e);
        }
    }

    public void atualizar(int id, Linha linha) {
        String query = "UPDATE T_AUMO_LINHA SET NM_LINHA = ?, NR_LINHA = ?, ST_LINHA = ? WHERE ID_LINHA = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {
            stmt.setString(1, linha.getNomeLinha());
            stmt.setInt(2, linha.getNumeroLinha());
            stmt.setString(3, linha.getStatusLinha());
            stmt.setInt(4, id);
            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Linha com ID {} atualizada com sucesso", linha.getId());
            }

        } catch (SQLException e) {
            logger.error("Erro ao atualizar linha no banco de dados", e);
            throw new RuntimeException("Erro ao atualizar linha", e);
        }
    }

    public void deletar(int idLinha) {
        String updateQuery = "UPDATE T_AUMO_LINHA SET DELETED = 1 WHERE ID_LINHA = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(updateQuery)) {

            stmt.setInt(1, idLinha);

            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Linha deletada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao deletar a linha {}", e.getMessage());
        }
    }
}





