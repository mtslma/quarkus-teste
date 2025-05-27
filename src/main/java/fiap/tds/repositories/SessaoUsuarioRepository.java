package fiap.tds.repositories;

import fiap.tds.entities.SessaoUsuario;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;

public class SessaoUsuarioRepository {

    // Logger
    private static final Logger logger = LogManager.getLogger(SessaoUsuarioRepository.class);

    public SessaoUsuario buscarSessao(String token) {
        String query = "SELECT * FROM T_POSE_SESSAO_USUARIO WHERE TOKEN_SESSAO = ?";

        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setString(1, token);

            var res = stmt.executeQuery();

            if (res.next()) {
                SessaoUsuario sessao = new SessaoUsuario();
                sessao.setIdUsuario(res.getInt("ID_USUARIO"));
                sessao.setTokenSessao(res.getString("TOKEN_SESSAO"));
                sessao.setStatusSessao(res.getString("ST_SESSAO"));
                sessao.setDataLogin(res.getTimestamp("DT_LOGIN").toLocalDateTime());
                Timestamp logoutTimestamp = res.getTimestamp("DT_LOGOUT");
                sessao.setDataLogout(logoutTimestamp != null ? logoutTimestamp.toLocalDateTime() : null);

                return sessao;
            } else {
                logger.warn("⚠️ Nenhuma sessão encontrada");
                return null;
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao buscar por sessão do usuário", e);
            return null;
        }
    }

    // Essa função roda quando o usuário realiza login
    public void registrarSessao(SessaoUsuario sessaoUsuario) {
        String query = "INSERT INTO T_POSE_SESSAO_USUARIO (ID_USUARIO, TOKEN_SESSAO, DT_LOGIN, ST_SESSAO) " +
                "VALUES (?, ?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, sessaoUsuario.getIdUsuario());
            stmt.setString(2, sessaoUsuario.getTokenSessao()); // Token já vem pronto
            stmt.setTimestamp(3, Timestamp.valueOf(sessaoUsuario.getDataLogin()));
            stmt.setString(4, sessaoUsuario.getStatusSessao());

            int res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Sessão de usuário registrada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar sessão do usuário: {}", e.getMessage());
        }
    }

    public void deletarSessao(String tokenSessao) {
        String query = "UPDATE T_POSE_SESSAO_USUARIO SET DT_LOGOUT = ?, ST_SESSAO = ? WHERE TOKEN_SESSAO = ?";

        try (var conn = DatabaseConfig.getConnection()) {
            var stmt = conn.prepareStatement(query);
            stmt.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now())); // Define o horário do logout
            stmt.setString(2, "INATIVA"); // Atualiza o status da sessão para "inativo"
            stmt.setString(3, tokenSessao); // Identifica a sessão pelo token

            var res = stmt.executeUpdate();

            if (res > 0) {
                logger.info("✅ Sessão do usuário com token {} foi finalizada", tokenSessao);
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao finalizar sessão do usuário \n{}", e.getMessage());
        }
    }
}
