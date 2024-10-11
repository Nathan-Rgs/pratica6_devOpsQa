package infra;

import java.util.ArrayList;
import java.util.List;

import br.com.valueprojects.mock_spring.model.Jogo;



public class JogoDaoFalso {

	private static List<Jogo> Jogos = new ArrayList<Jogo>();
	
	public void salva(Jogo jogo) {
		Jogos.add(jogo);
	}

	public List<Jogo> finalizados() {
		
		List<Jogo> selecionados = new ArrayList<Jogo>();
		for(Jogo jogo : Jogos) {
			if(jogo.isFinalizado()) selecionados.add(jogo);
		}

		return selecionados;
	}
	
	public List<Jogo> emAndamento() {
		
		List<Jogo> selecionados = new ArrayList<Jogo>();
		for(Jogo jogo : Jogos) {
			if(!jogo.isFinalizado()) selecionados.add(jogo);
		}

		return selecionados;
	}
	
	public void atualiza(Jogo jogo) { /* sem a��o! */ }
}
