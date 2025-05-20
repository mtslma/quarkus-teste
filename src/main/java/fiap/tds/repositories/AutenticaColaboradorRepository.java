package fiap.tds.repositories;

import fiap.tds.dtos.autenticaColaborador.AutenticaColaboradorDto;
import fiap.tds.entities.AutenticaColaborador;
import fiap.tds.infrastructure.DatabaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.SQLException;

public class AutenticaColaboradorRepository {

    // Criando um logger para a classe
    private static final Logger logger = LogManager.getLogger(AutenticaColaboradorRepository.class);

    // Função para registrar a autenticação de um colaborador, é chamada quando tem um POST para colaborador
    public void registrarAutenticaColaborador(AutenticaColaborador autenticaColaborador) {
        logger.info("Iniciando registro de autenticação do colaborador");

        // Query para inserir autenticação vinculada ao colaborador
        String query = "INSERT INTO T_AUMO_AUTENTICA_COLABORADOR (ID_COLABORADOR, EMAIL, SENHA) VALUES (?, ?, ?)";

        try (var conn = DatabaseConfig.getConnection();  // Estabelecendo conexão com o banco
             var stmt = conn.prepareStatement(query)) { // Preparando o statement SQL

            // Setando os parâmetros para a inserção
            stmt.setInt(1, autenticaColaborador.getId_colaborador()); // Vinculando o ID do colaborador
            stmt.setString(2, autenticaColaborador.getEmail());  // Usando o email do objeto
            stmt.setString(3, autenticaColaborador.getSenha());  // Usando a senha do objeto

            int rowsAffected = stmt.executeUpdate(); // Executando a inserção no banco
            if (rowsAffected > 0) {
                logger.info("✅ Autentica colaborador registrado com sucesso");
            } else {
                logger.error("❌ Não foi possível registrar autenticação do colaborador");
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao registrar Autentica Colaborador\n{}", e.getMessage());
        }
    }

    // Função para autenticar o login de um colaborador
    public AutenticaColaborador autenticarColaborador(AutenticaColaboradorDto dto) {
        String query = "SELECT A.ID_COLABORADOR, A.EMAIL, A.SENHA, C.NM_COLABORADOR, C.TP_COLABORADOR " +
                "FROM T_AUMO_AUTENTICA_COLABORADOR A " +
                "JOIN T_AUMO_COLABORADOR C ON A.ID_COLABORADOR = C.ID_COLABORADOR " +
                "WHERE A.EMAIL = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, dto.email());
            var rs = stmt.executeQuery();

            // Verifica se o resultado foi encontrado
            if (rs.next()) {
                String senhaArmazenada = rs.getString("SENHA");

                // Compara a senha fornecida com a senha armazenada no banco
                if (senhaArmazenada.equals(dto.senha())) {
                    // Cria e retorna o objeto de autenticação com os dados do colaborador
                    AutenticaColaborador autenticaColaborador = new AutenticaColaborador();
                    autenticaColaborador.setId_colaborador(rs.getInt("ID_COLABORADOR"));
                    autenticaColaborador.setEmail(rs.getString("EMAIL"));
                    autenticaColaborador.setSenha(rs.getString("SENHA"));

                    logger.info(dto.senha());
                    logger.info(rs.getString(("SENHA")));
                    return autenticaColaborador; // Retorna o objeto de autenticação do colaborador
                }
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao autenticar colaborador: {}", e.getMessage());
        }

        // Retorna null se a autenticação falhar
        return null;
    }

    // Função para validar se um email já está em uso no banco, evita aquele erro de restrição de sei lá o que chave restrita pipipipopopo
    public boolean validarEmailExistente(String email) {
        String query = "SELECT COUNT(*) FROM T_AUMO_AUTENTICA_COLABORADOR WHERE EMAIL = ?";

        try (var conn = DatabaseConfig.getConnection();
             var stmt = conn.prepareStatement(query)) {

            stmt.setString(1, email);
            var res = stmt.executeQuery();

            if (res.next()) {
                return res.getInt(1) > 0;
            }
        } catch (SQLException e) {
            logger.error("❌ Erro ao verificar e-mail existente: {}", e.getMessage());
        }

        return false;
    }

}
