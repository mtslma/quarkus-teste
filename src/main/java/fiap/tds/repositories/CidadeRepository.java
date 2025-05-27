package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.Cidade;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class CidadeRepository {

    public static final Logger logger = LogManager.getLogger(CidadeRepository.class);

    /**
     * Registra uma nova cidade no banco de dados.
     * QT_OCORRENCIAS e QT_ABRIGOS não são inseridos aqui, pois são calculados na leitura.
     */
    public void registrar(Cidade cidade) {
        // Query ajustada para remover QT_OCORRENCIAS e QT_ABRIGOS
        var query = "INSERT INTO T_POSE_CIDADE (NM_CIDADE, DT_CRIACAO, LAT, LON, DELETED) VALUES (?, ?, ?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query, new String[]{"ID_CIDADE"});

            // Definindo os parâmetros - Índices ajustados
            stmt.setString(1, cidade.getNomeCidade());
            stmt.setTimestamp(2, Timestamp.valueOf(cidade.getDataCriacao()));

            // LAT é o 3º parâmetro agora
            if (cidade.getLat() != null) {
                stmt.setBigDecimal(3, cidade.getLat());
            } else {
                // Assumindo que LAT é NOT NULL conforme sua definição de tabela anterior.
                // Se puder ser NULL, este setNull é apropriado.
                // Se for NOT NULL, o objeto 'cidade' deve garantir que lat não seja nulo.
                stmt.setNull(3, Types.NUMERIC);
            }

            // LON é o 4º parâmetro agora
            if (cidade.getLon() != null) {
                stmt.setBigDecimal(4, cidade.getLon());
            } else {
                // Assumindo que LON é NOT NULL.
                stmt.setNull(4, Types.NUMERIC);
            }

            // DELETED é o 5º parâmetro agora
            stmt.setInt(5, cidade.isDeleted() ? 1 : 0);

            var res = stmt.executeUpdate();

            if (res > 0) {
                try (var generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        var idGerado = generatedKeys.getInt(1);
                        cidade.setIdCidade(idGerado); // Atualiza o objeto cidade com o ID gerado
                        logger.info("✅ Cidade registrada com sucesso, ID: {}", idGerado);
                    }
                } catch (SQLException e) {
                    logger.error("❌ Erro ao obter ID gerado para a cidade: {}", e.getMessage(), e);
                }
            }
        } catch (SQLException e) {
            // Log do erro original que inclui o código ORA
            logger.error("❌ Erro ao registrar cidade: {}", e.getMessage(), e);
            // Você pode querer relançar uma exceção customizada aqui se o service precisar tratar
        }
    }

    public SearchResult<Cidade> buscar(String nomeCidade, String direction) {
        List<Cidade> cidadesBuscadas = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder(
                "SELECT c.ID_CIDADE, c.DELETED, c.DT_CRIACAO, c.NM_CIDADE, c.LAT, c.LON, " +
                        "(SELECT COUNT(*) FROM T_POSE_ABRIGO a WHERE a.ID_CIDADE = c.ID_CIDADE) as QT_ABRIGOS_CALC, " + // Assumindo que não há 'deleted' em abrigo
                        "(SELECT COUNT(*) FROM T_POSE_OCORRENCIA o WHERE o.ID_CIDADE = c.ID_CIDADE AND o.DELETED = 0) as QT_OCORRENCIAS_CALC " +
                        "FROM T_POSE_CIDADE c " +
                        "WHERE c.DELETED = 0"
        );

        if (nomeCidade != null && !nomeCidade.isBlank()) {
            query.append(" AND UPPER(c.NM_CIDADE) LIKE UPPER(?)");
        }

        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY c.ID_CIDADE DESC");
        } else {
            query.append(" ORDER BY c.ID_CIDADE ASC");
        }

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {

            int paramIndex = 1;
            if (nomeCidade != null && !nomeCidade.isBlank()) {
                stmt.setString(paramIndex++, "%" + nomeCidade + "%");
            }

            var res = stmt.executeQuery();

            while (res.next()) {
                Cidade cidade = new Cidade();
                cidade.setIdCidade(res.getInt("ID_CIDADE"));
                cidade.setDeleted(res.getInt("DELETED") == 1);
                cidade.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                cidade.setNomeCidade(res.getString("NM_CIDADE"));
                cidade.setLat(res.getBigDecimal("LAT"));
                cidade.setLon(res.getBigDecimal("LON"));
                cidade.setQuantidadeAbrigos(res.getInt("QT_ABRIGOS_CALC"));
                cidade.setQuantidadeOcorrencias(res.getInt("QT_OCORRENCIAS_CALC"));
                cidadesBuscadas.add(cidade);
            }
            totalItems = cidadesBuscadas.size();
            logger.info("✅ Cidades buscadas com sucesso. {} resultados.", totalItems);

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar cidades: {}", e.getMessage(), e);
        }
        return new SearchResult<>(cidadesBuscadas, totalItems);
    }

    public Cidade buscarPorId(int id) {
        var query =
                "SELECT c.ID_CIDADE, c.DELETED, c.DT_CRIACAO, c.NM_CIDADE, c.LAT, c.LON, " +
                        "(SELECT COUNT(*) FROM T_POSE_ABRIGO a WHERE a.ID_CIDADE = c.ID_CIDADE) as QT_ABRIGOS_CALC, " +
                        "(SELECT COUNT(*) FROM T_POSE_OCORRENCIA o WHERE o.ID_CIDADE = c.ID_CIDADE AND o.DELETED = 0) as QT_OCORRENCIAS_CALC " +
                        "FROM T_POSE_CIDADE c " +
                        "WHERE c.ID_CIDADE = ? AND c.DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            var rs = stmt.executeQuery();

            if (rs.next()) {
                Cidade cidade = new Cidade();
                cidade.setIdCidade(rs.getInt("ID_CIDADE"));
                cidade.setDeleted(rs.getInt("DELETED") == 1);
                cidade.setDataCriacao(rs.getTimestamp("DT_CRIACAO").toLocalDateTime());
                cidade.setNomeCidade(rs.getString("NM_CIDADE"));
                cidade.setLat(rs.getBigDecimal("LAT"));
                cidade.setLon(rs.getBigDecimal("LON"));
                cidade.setQuantidadeAbrigos(rs.getInt("QT_ABRIGOS_CALC"));
                cidade.setQuantidadeOcorrencias(rs.getInt("QT_OCORRENCIAS_CALC"));
                logger.info("✅ Cidade com ID {} buscada com sucesso.", id);
                return cidade;
            } else {
                logger.warn("⚠️ Nenhuma cidade ativa encontrada com ID {}.", id);
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar cidade por ID {}: {}", id, e.getMessage(), e);
        }
        return null;
    }

    public void atualizar(int id, Cidade cidade) {

        String query = "UPDATE T_POSE_CIDADE SET NM_CIDADE = ?, LAT = ?, LON = ? " + // DELETED não é atualizado aqui
                "WHERE ID_CIDADE = ? AND DELETED = 0";

        try (var conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setString(1, cidade.getNomeCidade());
            // Índices ajustados
            if (cidade.getLat() != null) {
                stmt.setBigDecimal(2, cidade.getLat());
            } else {
                stmt.setNull(2, Types.NUMERIC);
            }
            if (cidade.getLon() != null) {
                stmt.setBigDecimal(3, cidade.getLon());
            } else {
                stmt.setNull(3, Types.NUMERIC);
            }
            stmt.setInt(4, id);

            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Cidade com ID {} atualizada com sucesso.", id);
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao atualizar cidade com ID {}: {}", id, e.getMessage(), e);
        }
    }

    public void deletar(int id) {
        logger.info("Iniciando deleção lógica da cidade por ID {}", id);
        String query = "UPDATE T_POSE_CIDADE SET DELETED = 1 WHERE ID_CIDADE = ?";

        try (var conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {
            stmt.setInt(1, id);
            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Cidade com ID {} marcada como deletada com sucesso.", id);
            } else {
                logger.warn("⚠️ Nenhuma cidade encontrada com ID {} para deletar.", id);
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao marcar cidade com ID {} como deletada: {}", id, e.getMessage(), e);
        }
    }
}