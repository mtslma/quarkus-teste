package fiap.tds.entities;

import java.time.LocalDateTime;

public class SessaoUsuario {

    private int idSessao;
    private int idUsuario;
    private String tokenSessao;
    private LocalDateTime dataLogin = LocalDateTime.now();
    private LocalDateTime dataLogout;
    private String statusSessao;

    // Construtores
    public SessaoUsuario() {
    }

    public SessaoUsuario(LocalDateTime dataLogin, LocalDateTime dataLogout, int idSessao, int idUsuario, String statusSessao, String tokenSessao) {
        this.dataLogin = dataLogin;
        this.dataLogout = dataLogout;
        this.idSessao = idSessao;
        this.idUsuario = idUsuario;
        this.statusSessao = statusSessao;
        this.tokenSessao = tokenSessao;
    }

    // Getters and Setters
    public LocalDateTime getDataLogin() {
        return dataLogin;
    }

    public void setDataLogin(LocalDateTime dataLogin) {
        this.dataLogin = dataLogin;
    }

    public LocalDateTime getDataLogout() {
        return dataLogout;
    }

    public void setDataLogout(LocalDateTime dataLogout) {
        this.dataLogout = dataLogout;
    }

    public int getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(int idSessao) {
        this.idSessao = idSessao;
    }

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getStatusSessao() {
        return statusSessao;
    }

    public void setStatusSessao(String statusSessao) {
        this.statusSessao = statusSessao;
    }

    public String getTokenSessao() {
        return tokenSessao;
    }

    public void setTokenSessao(String tokenSessao) {
        this.tokenSessao = tokenSessao;
    }
}
