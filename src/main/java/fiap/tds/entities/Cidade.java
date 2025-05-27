package fiap.tds.entities;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class Cidade {
    private int idCidade;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nomeCidade;
    private int quantidadeOcorrencias;
    private int quantidadeAbrigos;
    private BigDecimal lat;
    private BigDecimal lon;

    // Construtores
    public Cidade() {
    }

    public Cidade(int idCidade, boolean deleted, LocalDateTime dataCriacao, String nomeCidade, int quantidadeOcorrencias, int quantidadeAbrigos, BigDecimal lat, BigDecimal lon) {
        this.idCidade = idCidade;
        this.deleted = deleted;
        this.dataCriacao = dataCriacao;
        this.nomeCidade = nomeCidade;
        this.quantidadeOcorrencias = quantidadeOcorrencias;
        this.quantidadeAbrigos = quantidadeAbrigos;
        this.lat = lat;
        this.lon = lon;
    }

    // Getters and Setters
    public int getIdCidade() {
        return idCidade;
    }

    public void setIdCidade(int idCidade) {
        this.idCidade = idCidade;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }

    public void setDataCriacao(LocalDateTime dataCriacao) {
        this.dataCriacao = dataCriacao;
    }

    public String getNomeCidade() {
        return nomeCidade;
    }

    public void setNomeCidade(String nomeCidade) {
        this.nomeCidade = nomeCidade;
    }

    public int getQuantidadeOcorrencias() {
        return quantidadeOcorrencias;
    }

    public void setQuantidadeOcorrencias(int quantidadeOcorrencias) {
        this.quantidadeOcorrencias = quantidadeOcorrencias;
    }

    public int getQuantidadeAbrigos() {
        return quantidadeAbrigos;
    }

    public void setQuantidadeAbrigos(int quantidadeAbrigos) {
        this.quantidadeAbrigos = quantidadeAbrigos;
    }

    public BigDecimal getLat() {
        return lat;
    }

    public void setLat(BigDecimal lat) {
        this.lat = lat;
    }

    public BigDecimal getLon() {
        return lon;
    }

    public void setLon(BigDecimal lon) {
        this.lon = lon;
    }
}
