package fiap.tds.entities;

import java.sql.Date;
import java.time.LocalDateTime;

public class MensagemContato {
    private int id;
    private boolean deleted;
    private LocalDateTime dataCriacao = LocalDateTime.now();
    private String nome;
    private String email;
    private String mensagem;

    // Construtores
    public MensagemContato() {
    }

    public MensagemContato(LocalDateTime dataCriacao, boolean deleted, String email, int id, String mensagem, String nome) {
        this.dataCriacao = dataCriacao;
        this.deleted = deleted;
        this.email = email;
        this.id = id;
        this.mensagem = mensagem;
        this.nome = nome;
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }
}


