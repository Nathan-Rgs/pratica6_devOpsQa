package br.com.valueprojects.mock_spring.model;

import java.util.Calendar;
import java.util.List;

import infra.JogoDao;
import infra.VencedorDao;
import infra.Sms;

public class FinalizaJogo {

	private int total = 0;
	private final JogoDao jogoDao;
	private final VencedorDao vencedorDao;
	private final Sms smsService;

	public FinalizaJogo(JogoDao jogoDao, VencedorDao vencedorDao, Sms smsService) {
		this.jogoDao = jogoDao;
		this.vencedorDao = vencedorDao;
		this.smsService = smsService;
	}

	public void finaliza() {
		List<Jogo> todosJogosEmAndamento = jogoDao.emAndamento();

		for (Jogo jogo : todosJogosEmAndamento) {
			if (iniciouSemanaAnterior(jogo)) {
				// Finaliza o jogo
				jogo.finaliza();
				total++;
				jogoDao.atualiza(jogo);

				// Identifica o vencedor
				Participante vencedor = identificarVencedor(jogo);
				if (vencedor != null) {
					// Cria e salva o Vencedor
					Vencedor novoVencedor = new Vencedor(vencedor, Calendar.getInstance(), jogo);
					vencedorDao.salvaVencedor(novoVencedor);

					// Envia o SMS após o vencedor ser salvo
					smsService.send(vencedor);
				}
			}
		}
	}

	private boolean iniciouSemanaAnterior(Jogo jogo) {
		return diasEntre(jogo.getData(), Calendar.getInstance()) >= 7;
	}

	private int diasEntre(Calendar inicio, Calendar fim) {
		Calendar data = (Calendar) inicio.clone();
		int diasNoIntervalo = 0;
		while (data.before(fim)) {
			data.add(Calendar.DAY_OF_MONTH, 1);
			diasNoIntervalo++;
		}

		return diasNoIntervalo;
	}

	public int getTotalFinalizados() {
		return total;
	}

	private Participante identificarVencedor(Jogo jogo) {
		// Lógica para identificar o participante com a maior métrica (vencedor)
		Participante vencedor = null;
		double maiorMetrica = Double.MIN_VALUE;
		for (Resultado resultado : jogo.getResultados()) {
			if (resultado.getMetrica() > maiorMetrica) {
				maiorMetrica = resultado.getMetrica();
				vencedor = resultado.getParticipante();
			}
		}
		return vencedor;
	}
}
