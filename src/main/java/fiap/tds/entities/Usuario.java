package fiap.tds.entities;


import java.time.LocalDateTime;

public class Usuario {
    private int idUsuario;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeUsuario;
    private String tipoUsuario;
    private String telefoneContato;
    private int idCidade;
    private AutenticaUsuario autenticaUsuario;


    // Construtores
    public Usuario() {
    }

    public Usuario(AutenticaUsuario autenticaUsuario, LocalDateTime dataCriacao, boolean deleted, int idCidade, int idUsuario, String nomeUsuario, String telefoneContato, String tipoUsuario) {
        this.autenticaUsuario = autenticaUsuario;
        this.dataCriacao = dataCriacao;
        this.deleted = deleted;
        this.idCidade = idCidade;
        this.idUsuario = idUsuario;
        this.nomeUsuario = nomeUsuario;
        this.tipoUsuario = tipoUsuario;
        this.telefoneContato = telefoneContato;
    }

    // Getters and Setters
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public int getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(int idCidade) {
        this.idCidade = idCidade;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public void setTipoUsuario(String tipoUsuario) {
        this.tipoUsuario = tipoUsuario;
    }

    public String getTelefoneContato() {
        return telefoneContato;
    }

    public void setTelefoneContato(String telefoneContato) {
        this.telefoneContato = telefoneContato;
    }

    public AutenticaUsuario getAutenticaUsuario() {
        return autenticaUsuario;
    }

    public void setAutenticaUsuario(AutenticaUsuario autenticaUsuario) {
        this.autenticaUsuario = autenticaUsuario;
    }
}
