package br.com.valueprojects.mock_spring.model;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;

public class Jogo {

	private String descricao;
	private Calendar data;
	private List<Resultado> resultados;
	private boolean finaliza;
	private int id;
	
	public Jogo(String descricao) {
		this(descricao, Calendar.getInstance());
	}
	
	public Jogo(String descricao, Calendar data) {
		this.descricao = descricao;
		this.data = data;
		this.resultados = new ArrayList<Resultado>();
	}
	
	public void anota(Resultado resultado) {
		if(resultados.isEmpty() || podeTerResultado(resultado.getParticipante())) {
			resultados.add(resultado);
		}
	}

	private boolean podeTerResultado(Participante participante) {
		return !ultimoResultadoRecebido().getParticipante().equals(participante) && qtdDeResultadosDo(participante) <5;
	}

	private int qtdDeResultadosDo(Participante participante) {
		int total = 0;
		for(Resultado l : resultados) {
			if(l.getParticipante().equals(participante)) total++;
		}
		return total;
	}

	private Resultado ultimoResultadoRecebido() {
		return resultados.get(resultados.size()-1);
	}

	public String getDescricao() {
		return descricao;
	}

	public List<Resultado> getResultados() {
		return Collections.unmodifiableList(resultados);
	}

	public Calendar getData() {
		return (Calendar) data.clone();
	}

	public void finaliza() {
		this.finaliza = true;
	}
	
	public boolean isFinalizado() {
		return finaliza;
	}

	public void setId(int id) {
		this.id = id;
	}
	
	public int getId() {
		return id;
	}

	 public void setData(Calendar data) {
	        this.data = data;
	    }
}
