package fiap.tds.entities;

import java.time.LocalDateTime;

public class SessaoColaborador {

    private int idSessao;
    private String tokenSessao;
    private int idColaborador;
    private LocalDateTime dataLogin = LocalDateTime.now();
    private LocalDateTime dataLogout;
    private String statusSessao; // Corrigido o nome (statuSesssao)

    public SessaoColaborador() {
    }

    public SessaoColaborador(LocalDateTime dataLogin, LocalDateTime dataLogout, int idColaborador, int idSessao, String statusSessao, String tokenSessao) {
        this.dataLogin = dataLogin;
        this.dataLogout = dataLogout;
        this.idColaborador = idColaborador;
        this.idSessao = idSessao;
        this.statusSessao = statusSessao;
        this.tokenSessao = tokenSessao;
    }

    // Getters e Setters
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

    public int getIdColaborador() {
        return idColaborador;
    }

    public void setIdColaborador(int idColaborador) {
        this.idColaborador = idColaborador;
    }

    public int getIdSessao() {
        return idSessao;
    }

    public void setIdSessao(int idSessao) {
        this.idSessao = idSessao;
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
