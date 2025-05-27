package fiap.tds.repositories;

import fiap.tds.entities.AutenticaUsuario;
import fiap.tds.entities.Usuario;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;
import java.sql.Timestamp;

public class UsuarioRepository {

    public static final Logger logger = LogManager.getLogger(UsuarioRepository.class);

    // Chamando o repository de AutenticaUsuario para usar as funcionalidades dele aqui e conseguir separar as tabelas
    AutenticaUsuarioRepository autenticaUsuarioRepository = new AutenticaUsuarioRepository();

    // Função para registrar colaborador
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

                        // Chamar a função do AutenticaColaboradorRepository aqui!
                        AutenticaUsuario autenticaColaborador = usuario.getAutenticaUsuario();
                        autenticaColaborador.setIdUsuario(idGerado);
                        autenticaUsuarioRepository.registrar(autenticaColaborador);
                    }
                } catch (Exception e) {
                    logger.info("❌ Erro ao obter ID gerado pelo banco");
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar usuário: {}", e.getMessage());
        }
    }
}
