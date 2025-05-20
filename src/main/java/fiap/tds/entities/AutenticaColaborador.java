package fiap.tds.entities;

public class AutenticaColaborador {
    private int id_colaborador;
    private String email;
    private String senha;

    public AutenticaColaborador() {
    }

    public AutenticaColaborador(String email, int id_colaborador, String senha) {
        this.email = email;
        this.id_colaborador = id_colaborador;
        this.senha = senha;
    }

    // Getters and Setters
    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId_colaborador() {
        return id_colaborador;
    }

    public void setId_colaborador(int id_colaborador) {
        this.id_colaborador = id_colaborador;
    }

    public String getSenha() {
        return senha;
    }

    public void setSenha(String senha) {
        this.senha = senha;
    }

}
