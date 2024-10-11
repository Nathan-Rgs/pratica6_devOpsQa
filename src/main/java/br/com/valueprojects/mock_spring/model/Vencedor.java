package br.com.valueprojects.mock_spring.model;

import java.util.Calendar;

public class Vencedor {
    private int id;
    private Participante participante;
    private Jogo jogo;
    private Calendar data;

    // Construtor com todos os atributos
    public Vencedor(int id, Participante participante, Calendar data, Jogo jogo) {
        this.id = id;
        this.participante = participante;
        this.data = data;
        this.jogo = jogo;
    }

    // Construtor sem o ID (para quando o ID é gerado no banco de dados)
    public Vencedor(Participante participante, Calendar data, Jogo jogo) {
        this.participante = participante;
        this.data = data;
        this.jogo = jogo;
    }

    // Getters e Setters
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Participante getParticipante() {
        return participante;
    }

    public void setParticipante(Participante participante) {
        this.participante = participante;
    }

    public Calendar getData() {
        return data;
    }

    public void setData(Calendar data) {
        this.data = data;
    }

    public Jogo getJogo() {
        return jogo;
    }

    public void setJogo(Jogo jogo) {
        this.jogo = jogo;
    }

    @Override
    public String toString() {
        return "Vencedor [id=" + id + ", participante=" + participante.getNome() + ", data="
                + data.getTime() + ", jogo=" + jogo.getDescricao() + "]";
    }
}
