package fiap.tds.repositories;

import fiap.tds.dtos.SearchResult;
import fiap.tds.entities.MensagemContato;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class MensagemContatoRepository {

    private static final Logger logger = LogManager.getLogger(MensagemContatoRepository.class);

    // Função para registrar mensagens de contato
    public void registrar(MensagemContato mensagemContato) {
        // Preparando a query
        String query = "INSERT INTO T_AUMO_MS_CONTATO (DELETED, DT_CRIACAO, NM_CONTATO, EMAIL_CONTATO, MS_CONTATO) " +
                "VALUES (?, ?, ?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection();

             // Preparando o statement para executar a query, ela vai retornar o ID
             var stmt = conn.prepareStatement(query, new String[]{"ID_CONTATO"})) {

            // Definindo os valores que serão inseridos
            stmt.setInt(1, 0); // deleted = 0
            stmt.setTimestamp(2, Timestamp.valueOf(mensagemContato.getDataCriacao()));
            stmt.setString(3, mensagemContato.getNome());
            stmt.setString(4, mensagemContato.getEmail());
            stmt.setString(5, mensagemContato.getMensagem());

            // Executando inserção
            int res = stmt.executeUpdate();

            // Validando sucesso da operação
            if (res > 0) {
                logger.info("✅ Mensagem registrada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar mensagem de contato: {}", e.getMessage());
        }
    }

    // Função para registrar mensagens de contato com filtros
    public SearchResult<MensagemContato> buscar(String name, String text, String email, String direction) {

        List<MensagemContato> mensagensFiltradas = new ArrayList<>();
        int totalItems = 0;

        var query = new StringBuilder("SELECT * FROM T_AUMO_MS_CONTATO WHERE DELETED = 0");

        // Filtros
        if (name != null && !name.isBlank()) {
            query.append(" AND UPPER(NM_CONTATO) LIKE UPPER(?)");
        }
        if (text != null && !text.isBlank()) {
            query.append(" AND UPPER(MS_CONTATO) LIKE UPPER(?)");
        }
        if (email != null && !email.isBlank()) {
            query.append(" AND UPPER(EMAIL_CONTATO) LIKE UPPER(?)");
        }

        // Ordenação
        if ("desc".equalsIgnoreCase(direction)) {
            query.append(" ORDER BY NM_CONTATO DESC");
        } else {
            query.append(" ORDER BY NM_CONTATO ASC");
        }

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query.toString())) {

            // Índice dos parâmetros dinâmicos
            int paramIndex = 1;
            if (name != null && !name.isBlank()) {
                stmt.setString(paramIndex++, "%" + name + "%");
            }
            if (text != null && !text.isBlank()) {
                stmt.setString(paramIndex++, "%" + text + "%");
            }
            if (email != null && !email.isBlank()) {
                stmt.setString(paramIndex++, "%" + email + "%");
            }

            var res = stmt.executeQuery();

            // Preenche lista com resultados
            while (res.next()) {
                var contato = new MensagemContato();
                contato.setId(res.getInt("ID_CONTATO"));
                contato.setNome(res.getString("NM_CONTATO"));
                contato.setEmail(res.getString("EMAIL_CONTATO"));
                contato.setMensagem(res.getString("MS_CONTATO"));
                contato.setDeleted(res.getBoolean("DELETED"));
                contato.setDataCriacao(res.getTimestamp("DT_CRIACAO").toLocalDateTime());
                mensagensFiltradas.add(contato);
            }

            logger.info("✅ Mensagens de contato filtradas com sucesso");
            totalItems = mensagensFiltradas.size();

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar por mensagens de contato: \n{}", e.getMessage());
       }

        return new SearchResult<>(mensagensFiltradas, totalItems);
    }

    // Função para deletar mensagens de contato logicamente
    public void deletarMensagemContato(int id) {
        var query = "UPDATE T_AUMO_MS_CONTATO SET DELETED = 1 WHERE ID_CONTATO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, id);
            var res = stmt.executeUpdate();
            if (res > 0){
                logger.info("✅ Mensagem deletada com sucesso");
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao executar DELETE lógico no banco de dados: {}", e.getMessage());

        }
    }

    // As mensagens de contato não devem ser atualizadas após serem enviadas, pois ficam disponíveis apenas para
    // colaboradores, com o único intuito de serem analisadas/ respondidas por algum canal posteriormente
}
