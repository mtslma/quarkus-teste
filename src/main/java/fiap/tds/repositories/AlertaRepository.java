package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.Alerta;
import fiap.tds.exceptions.NotFoundException;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class AlertaRepository {

    private static final Logger logger = LogManager.getLogger(AlertaRepository.class);

    public void registrar(Alerta alerta) {
        var query = "INSERT INTO T_AUMO_ALERTA (DELETED, NM_ALERTA, DS_ALERTA, NV_GRAVIDADE, ID_ESTACAO, ID_LINHA, DT_CRIACAO) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?)";

        // Conexão com o banco
        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            // Configurando os parâmetros
            stmt.setInt(1, 0); // Deleted = 0 (ativo)
            stmt.setString(2, alerta.getNomeAlerta());
            stmt.setString(3, alerta.getDescricaoAlerta());
            stmt.setString(4, alerta.getNivelGravidade().toUpperCase());
            stmt.setInt(5, alerta.getIdEstacao());
            stmt.setInt(6, alerta.getIdLinha());
            stmt.setTimestamp(7, Timestamp.valueOf(alerta.getDataCriacao()));

            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Alerta registrado com sucesso!");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar alerta", e);
            throw new RuntimeException("Erro ao registrar alerta.");
        }
    }



    public Alerta buscarPorId(int id) {
        final String query = "SELECT ID_ALERTA, NM_ALERTA, DS_ALERTA, NV_GRAVIDADE, ID_ESTACAO, ID_LINHA, DELETED, DT_CRIACAO, DT_ENCERRAMENTO FROM T_AUMO_ALERTA WHERE ID_ALERTA = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            try (var rs = stmt.executeQuery()) {
                if (rs.next()) {
                    Alerta alerta = new Alerta();
                    alerta.setId(rs.getInt("ID_ALERTA"));
                    alerta.setNomeAlerta(rs.getString("NM_ALERTA"));
                    alerta.setDescricaoAlerta(rs.getString("DS_ALERTA"));
                    alerta.setNivelGravidade(rs.getString("NV_GRAVIDADE").toUpperCase());
                    alerta.setIdEstacao(rs.getInt("ID_ESTACAO"));
                    alerta.setIdLinha(rs.getInt("ID_LINHA"));
                    alerta.setDeleted(rs.getBoolean("DELETED"));
                    alerta.setDataCriacao(rs.getTimestamp("DT_CRIACAO").toLocalDateTime());

                    Timestamp encerramento = rs.getTimestamp("DT_ENCERRAMENTO");
                    if (encerramento != null) {
                        alerta.setDataEncerramento(encerramento.toLocalDateTime());
                    }

                    logger.info("✅ Alerta buscado com sucesso");
                    return alerta;
                }
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar alerta por ID {}: {}", id, e.getMessage(), e);
        }

        return null;
    }

    public SearchResult<Alerta> buscar(String nome, String nivel, String descricao, String direction) {
        List<Alerta> alertasFiltrados = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_AUMO_ALERTA WHERE DELETED = 0");

        // Filtros
        if (nome != null && !nome.isBlank()) {
            query.append(" AND UPPER(NM_ALERTA) LIKE UPPER(?)");
        }
        if (nivel != null && !nivel.isBlank()) {
            query.append(" AND UPPER(NV_GRAVIDADE) LIKE UPPER(?)");
        }
        if (descricao != null && !descricao.isBlank()) {
            query.append(" AND UPPER(DS_ALERTA) LIKE UPPER(?)");
        }

        // Ordenação
        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY DT_CRIACAO DESC");
        } else {
            query.append(" ORDER BY DT_CRIACAO ASC");
        }

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (nome != null && !nome.isBlank()) {
                stmt.setString(paramIndex++, "%" + nome + "%");
            }
            if (nivel != null && !nivel.isBlank()) {
                stmt.setString(paramIndex++, "%" + nivel + "%");
            }
            if (descricao != null && !descricao.isBlank()) {
                stmt.setString(paramIndex++, "%" + descricao + "%");
            }

            var res = stmt.executeQuery();

            while (res.next()) {
                Alerta alerta = new Alerta();
                alerta.setId(res.getInt("ID_ALERTA"));
                alerta.setNomeAlerta(res.getString("NM_ALERTA"));
                alerta.setDescricaoAlerta(res.getString("DS_ALERTA"));
                alerta.setNivelGravidade(res.getString("NV_GRAVIDADE"));
                alerta.setIdEstacao(res.getInt("ID_ESTACAO"));
                alerta.setIdLinha(res.getInt("ID_LINHA"));
                alerta.setDeleted(res.getBoolean("DELETED"));
                alerta.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                alerta.setDataEncerramento(
                        res.getTimestamp("DT_ENCERRAMENTO") != null
                                ? res.getTimestamp("DT_ENCERRAMENTO").toLocalDateTime()
                                : null
                );
                alertasFiltrados.add(alerta);
            }

            logger.info(("✅ Alertas buscados com sucesso"));
            totalItems = alertasFiltrados.size();


        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar alertas \n{}", e.getMessage());
        }

        return new SearchResult<>(alertasFiltrados, totalItems);
    }



    public void atualizar(Alerta alerta) {
        var query = """
        UPDATE T_AUMO_ALERTA
        SET NM_ALERTA = ?, DS_ALERTA = ?, NV_GRAVIDADE = ?, ID_ESTACAO = ?, ID_LINHA = ?
        WHERE ID_ALERTA = ? AND DELETED = 0
    """;

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, alerta.getNomeAlerta());
            stmt.setString(2, alerta.getDescricaoAlerta());
            stmt.setString(3, alerta.getNivelGravidade().toUpperCase());
            stmt.setInt(4, alerta.getIdEstacao());
            stmt.setInt(5, alerta.getIdLinha());
            stmt.setInt(6, alerta.getId());

            int res = stmt.executeUpdate();
            if (res == 0) {
                throw new NotFoundException("Alerta não encontrado para atualização.");
            } else if (res > 0) {
                logger.info("✅ Alerta atualizado com sucesso!");
            }


        } catch (SQLException e) {
            logger.error("❌ Erro ao atualizar alerta", e);
            throw new RuntimeException("Erro no banco ao atualizar alerta.");
        }
    }

    // Encerrar alerta
    public void deletar(int id) {
        var query = "UPDATE T_AUMO_ALERTA SET DELETED = ?, DT_ENCERRAMENTO = ? WHERE ID_ALERTA = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, 1); // Definindo como deleted
            stmt.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
            stmt.setInt(3, id);
            var res = stmt.executeUpdate();

            if (res > 0){
                logger.info("✅ Alerta deletado com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao encerrar alerta: {}", e.getMessage());
        }
    }

}
