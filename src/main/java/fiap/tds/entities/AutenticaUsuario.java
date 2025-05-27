package fiap.tds.entities;

public class AutenticaUsuario {
    private int idUsuario;
    private String emailUsuario;
    private String senhaUsuario;

    // Construtores
    public AutenticaUsuario() {
    }

    public AutenticaUsuario(String emailUsuario, int idUsuario, String senhaUsuario) {
        this.emailUsuario = emailUsuario;
        this.idUsuario = idUsuario;
        this.senhaUsuario = senhaUsuario;
    }

    // Getters and Setters
    public String getEmailUsuario() {
        return emailUsuario;
    }

    public void setEmailUsuario(String emailUsuario) {
        this.emailUsuario = emailUsuario;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getSenhaUsuario() {
        return senhaUsuario;
    }

    public void setSenhaUsuario(String senhaUsuario) {
        this.senhaUsuario = senhaUsuario;
    }
}
