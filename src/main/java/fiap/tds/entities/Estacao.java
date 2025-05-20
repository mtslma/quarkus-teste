package fiap.tds.entities;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class Estacao {

    private int id;
    private int idLinha;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeEstacao;
    private String statusEstacao;
    private LocalTime inicioOperacao;
    private LocalTime fimOperacao;

    public Estacao() {
    }

    public Estacao(LocalDateTime dataCriacao, boolean deleted, LocalTime fimOperacao, int id, int idLinha, LocalTime inicioOperacao, String nomeEstacao, String statusEstacao) {
        this.dataCriacao = dataCriacao;
        this.deleted = deleted;
        this.fimOperacao = fimOperacao;
        this.id = id;
        this.idLinha = idLinha;
        this.inicioOperacao = inicioOperacao;
        this.nomeEstacao = nomeEstacao;
        this.statusEstacao = statusEstacao;
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

    public LocalTime getFimOperacao() {
        return fimOperacao;
    }

    public void setFimOperacao(LocalTime fimOperacao) {
        this.fimOperacao = fimOperacao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(int idLinha) {
        this.idLinha = idLinha;
    }

    public LocalTime getInicioOperacao() {
        return inicioOperacao;
    }

    public void setInicioOperacao(LocalTime inicioOperacao) {
        this.inicioOperacao = inicioOperacao;
    }

    public String getNomeEstacao() {
        return nomeEstacao;
    }

    public void setNomeEstacao(String nomeEstacao) {
        this.nomeEstacao = nomeEstacao;
    }

    public String getStatusEstacao() {
        return statusEstacao;
    }

    public void setStatusEstacao(String statusEstacao) {
        this.statusEstacao = statusEstacao;
    }
}
