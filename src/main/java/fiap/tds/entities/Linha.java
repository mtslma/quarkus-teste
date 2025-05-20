package fiap.tds.entities;

import java.time.LocalDateTime;

public class Linha {

    private int id;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeLinha;
    private int numeroLinha;
    private String statusLinha;

    public Linha() {
    }

    public Linha(LocalDateTime dataCriacao, boolean deleted, int id, String nomeLinha, int numeroLinha, String statusLinha) {
        this.dataCriacao = dataCriacao;
        this.deleted = deleted;
        this.id = id;
        this.nomeLinha = nomeLinha;
        this.numeroLinha = numeroLinha;
        this.statusLinha = statusLinha;
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNomeLinha() {
        return nomeLinha;
    }

    public void setNomeLinha(String nomeLinha) {
        this.nomeLinha = nomeLinha;
    }

    public int getNumeroLinha() {
        return numeroLinha;
    }

    public void setNumeroLinha(int numeroLinha) {
        this.numeroLinha = numeroLinha;
    }

    public String getStatusLinha() {
        return statusLinha;
    }

    public void setStatusLinha(String statusLinha) {
        this.statusLinha = statusLinha;
    }
}
