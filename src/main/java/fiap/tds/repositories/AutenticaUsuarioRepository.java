package fiap.tds.repositories;

import fiap.tds.dtos.autenticaUsuarioDto.AutenticaUsuarioDto;
import fiap.tds.entities.AutenticaUsuario;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class AutenticaUsuarioRepository {

    // Logger
    private static final Logger logger = LogManager.getLogger(AutenticaUsuarioRepository.class);


    // Função para registrar a autenticação de usuário
    public void registrar(AutenticaUsuario autenticaUsuario){

        var query = "INSERT INTO T_POSE_AUTENTICA_USUARIO (ID_USUARIO, EMAIL_USUARIO, SENHA_USUARIO) VALUES (?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection()){
            var stmt = conn.prepareStatement(query);

            // Definindo os parâmetros
            stmt.setInt(1, autenticaUsuario.getIdUsuario());
            stmt.setString(2, autenticaUsuario.getEmailUsuario());
            stmt.setString(3, autenticaUsuario.getSenhaUsuario());

            var res = stmt.executeUpdate();

            if (res > 0){
                logger.info("✅ Autenticação de usuário registrada com sucesso");
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar autenticação de usuário: {}", e.getMessage());
        }
    }

    // Função para autenticar usuários
    public AutenticaUsuario autenticar(AutenticaUsuarioDto dto){

        var query = "SELECT A.ID_USUARIO, A.EMAIL_USUARIO, A.SENHA_USUARIO, C.NM_USUARIO" +
                "FROM T_POSE_AUTENTICA_USUARIO A " +
                "JOIN T_POSE_USUARIO C ON A.ID_USUARIO = C.ID_USUARIO" +
                "WHERE A.EMAIL_USUARIO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dto.emailUsuario());

            var res = stmt.executeQuery();

            if (res.next()) {

                var senhaArmazenada = res.getString("SENHA_USUARIO");

                // Compara a senha fornecida com a senha armazenada no banco
                if (senhaArmazenada.equals(dto.senhaUsuario())) {

                    // Cria e retorna o objeto de autenticação com os dados do colaborador
                    AutenticaUsuario autenticaUsuario = new AutenticaUsuario();
                    autenticaUsuario.setIdUsuario(res.getInt("ID_USUARIO"));
                    autenticaUsuario.setEmailUsuario(res.getString("EMAIL"));
                    autenticaUsuario.setSenhaUsuario(res.getString("SENHA"));

                    logger.info("✅ Autenticação do usuário {} realizada com sucesso!", dto.emailUsuario());
                    return autenticaUsuario; // Retorna o objeto de autenticação do colaborador
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao autenticar usuário: {}", e.getMessage());
        }

        // Retorna null se a autenticação falhar
        return null;
    }

    // Função para validar se um email já está em uso
    public boolean validarEmailExistente(String email){
        var query = "SELECT COUNT(*) FROM T_POSE_AUTENTICA_USUARIO WHERE EMAIL_USUARIO = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);

            var res = stmt.executeQuery();

            if (res.next()) {
                logger.info("✅ O e-mail fornecido é válido");
                return res.getInt(1) > 0;
            }

        } catch (SQLException e) {
            logger.error("❌ Erro ao verificar e-mail: {}", e.getMessage());
        }

        return false;
    }
}
