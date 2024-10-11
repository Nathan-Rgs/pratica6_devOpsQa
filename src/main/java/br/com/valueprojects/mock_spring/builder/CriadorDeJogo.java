package br.com.valueprojects.mock_spring.builder;

import java.util.Calendar;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;

public class CriadorDeJogo {
	
	private Jogo jogo;
    private Calendar data;
    private boolean finaliza;
    private int id;

    public CriadorDeJogo() { }

    public CriadorDeJogo para(String descricao) {
        this.jogo = new Jogo(descricao);
        return this;
    }

    public CriadorDeJogo resultado(Participante participante, double metrica) {
        jogo.anota(new Resultado(participante, metrica));
        return this;
    }

    public CriadorDeJogo naData(Calendar data) {
        this.jogo.setData(data);
        return this;
    }

    public CriadorDeJogo finaliza() {
        this.finaliza = true;
        return this;
    }

    public boolean isFinalizado() {
        return finaliza;
    }

    public CriadorDeJogo comId(int id) {
        this.jogo.setId(id); // suposição que Jogo tem um método setId(int id)
        return this;
    }

    public int getId() {
        return jogo.getId(); // suposição que Jogo tem um método getId()
    }

    public Jogo constroi() {
        return jogo;
    }
}