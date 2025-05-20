package fiap.tds.entities;

import java.time.LocalDateTime;

public class Alerta {

    private int id;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeAlerta;
    private String descricaoAlerta;
    private LocalDateTime dataEncerramento;
    private String nivelGravidade;
    private int idLinha;
    private int idEstacao;

    public Alerta() {
    }

    public Alerta(LocalDateTime dataCriacao, LocalDateTime dataEncerramento, boolean deleted, String descricaoAlerta, int id, int idEstacao, int idLinha, String nivelGravidade, String nomeAlerta) {
        this.dataCriacao = dataCriacao;
        this.dataEncerramento = dataEncerramento;
        this.deleted = deleted;
        this.descricaoAlerta = descricaoAlerta;
        this.id = id;
        this.idEstacao = idEstacao;
        this.idLinha = idLinha;
        this.nivelGravidade = nivelGravidade;
        this.nomeAlerta = nomeAlerta;
    }

    // Getters and Setters
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public LocalDateTime getDataEncerramento() {
        return dataEncerramento;
    }

    public void setDataEncerramento(LocalDateTime dataEncerramento) {
        this.dataEncerramento = dataEncerramento;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescricaoAlerta() {
        return descricaoAlerta;
    }

    public void setDescricaoAlerta(String descricaoAlerta) {
        this.descricaoAlerta = descricaoAlerta;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getIdEstacao() {
        return idEstacao;
    }

    public void setIdEstacao(int idEstacao) {
        this.idEstacao = idEstacao;
    }

    public int getIdLinha() {
        return idLinha;
    }

    public void setIdLinha(int idLinha) {
        this.idLinha = idLinha;
    }

    public String getNivelGravidade() {
        return nivelGravidade;
    }

    public void setNivelGravidade(String nivelGravidade) {
        this.nivelGravidade = nivelGravidade;
    }

    public String getNomeAlerta() {
        return nomeAlerta;
    }

    public void setNomeAlerta(String nomeAlerta) {
        this.nomeAlerta = nomeAlerta;
    }
}
