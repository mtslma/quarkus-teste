package fiap.tds.entities;

import java.time.LocalDateTime;

public class Manutencao {
    private int id;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeManutencao;
    private String descricaoManutencao;
    private String nivelPrioridade;
    private int idLinha;
    private int idEstacao;

    public Manutencao() {
    }

    public Manutencao(LocalDateTime dataCriacao, boolean deleted, String descricaoManutencao, int id, int idEstacao, int idLinha, String nivelPrioridade, String nomeManutencao) {
        this.dataCriacao = dataCriacao;
        this.deleted = deleted;
        this.descricaoManutencao = descricaoManutencao;
        this.id = id;
        this.idEstacao = idEstacao;
        this.idLinha = idLinha;
        this.nivelPrioridade = nivelPrioridade;
        this.nomeManutencao = nomeManutencao;
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

    public String getDescricaoManutencao() {
        return descricaoManutencao;
    }

    public void setDescricaoManutencao(String descricaoManutencao) {
        this.descricaoManutencao = descricaoManutencao;
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

    public String getNivelPrioridade() {
        return nivelPrioridade;
    }

    public void setNivelPrioridade(String nivelPrioridade) {
        this.nivelPrioridade = nivelPrioridade;
    }

    public String getNomeManutencao() {
        return nomeManutencao;
    }

    public void setNomeManutencao(String nomeManutencao) {
        this.nomeManutencao = nomeManutencao;
    }
}
