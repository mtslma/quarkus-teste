package fiap.tds.entities;

import java.time.LocalDateTime;

public class Colaborador {
    private int id;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeColaborador;
    private String tipoColaborador;
    private AutenticaColaborador autenticaColaborador;

    public Colaborador() {
    }

    public Colaborador(AutenticaColaborador autenticaColaborador, LocalDateTime dataCriacao, boolean deleted, int id, String nomeColaborador, String tipoColaborador) {
        this.autenticaColaborador = autenticaColaborador;
        this.dataCriacao = dataCriacao;
        this.deleted = deleted;
        this.id = id;
        this.nomeColaborador = nomeColaborador;
        this.tipoColaborador = tipoColaborador;
    }

    public AutenticaColaborador getAutenticaColaborador() {
        return autenticaColaborador;
    }

    public void setAutenticaColaborador(AutenticaColaborador autenticaColaborador) {
        this.autenticaColaborador = autenticaColaborador;
    }

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

    public String getNomeColaborador() {
        return nomeColaborador;
    }

    public void setNomeColaborador(String nomeColaborador) {
        this.nomeColaborador = nomeColaborador;
    }

    public String getTipoColaborador() {
        return tipoColaborador;
    }

    public void setTipoColaborador(String tipoColaborador) {
        this.tipoColaborador = tipoColaborador;
    }
}
