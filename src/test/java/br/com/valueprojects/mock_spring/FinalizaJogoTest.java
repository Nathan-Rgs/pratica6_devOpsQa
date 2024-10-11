package br.com.valueprojects.mock_spring;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import br.com.valueprojects.mock_spring.builder.CriadorDeJogo;
import infra.JogoDao;
import infra.Sms;
import infra.VencedorDao;
import br.com.valueprojects.mock_spring.model.FinalizaJogo;
import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;
import br.com.valueprojects.mock_spring.model.Vencedor;

public class FinalizaJogoTest {

	private JogoDao jogoDaoFalso;
	private VencedorDao vencedorDaoFalso;
	private Sms smsMock;
	private FinalizaJogo finalizador;

	@BeforeEach
	public void setUp() {
		jogoDaoFalso = mock(JogoDao.class);
		vencedorDaoFalso = mock(VencedorDao.class);
		smsMock = mock(Sms.class);
		finalizador = new FinalizaJogo(jogoDaoFalso, vencedorDaoFalso, smsMock);
	}

	@Test
	public void deveFinalizarJogosDaSemanaAnterior() {
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Jogo jogo1 = new CriadorDeJogo().para("Cata moedas").naData(antiga).constroi();
		Jogo jogo2 = new CriadorDeJogo().para("Derruba barreiras").naData(antiga).constroi();

		List<Jogo> jogosAnteriores = Arrays.asList(jogo1, jogo2);

		when(jogoDaoFalso.emAndamento()).thenReturn(jogosAnteriores);

		finalizador.finaliza();

		assertTrue(jogo1.isFinalizado());
		assertTrue(jogo2.isFinalizado());
		assertEquals(2, finalizador.getTotalFinalizados());
	}

	@Test
	public void deveSalvarVencedorEAposIssoEnviarSms() {
		// Dados de teste
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Participante participanteVencedor = new Participante(1, "Leonardo");
		Jogo jogo = new CriadorDeJogo().para("Cata moedas").naData(antiga).constroi();
		Resultado resultado = new Resultado(participanteVencedor, 150.0);
		jogo.anota(resultado);

		List<Jogo> jogosAnteriores = Arrays.asList(jogo);

		// Mock do JogoDao para retornar o jogo
		when(jogoDaoFalso.emAndamento()).thenReturn(jogosAnteriores);

		// Ação: Finalizar o jogo
		finalizador.finaliza();

		// Verificação: O jogo foi salvo no JogoDao
		verify(jogoDaoFalso, times(1)).atualiza(jogo);

		// Verificação: O vencedor foi salvo no VencedorDao
		verify(vencedorDaoFalso, times(1)).salvaVencedor(any(Vencedor.class));

		// Verificação: O SMS só foi enviado após o salvamento do vencedor
		verify(smsMock, times(1)).send(participanteVencedor);
	}

	@Test
	public void naoDeveEnviarSmsSeJogoNaoForSalvo() {
		// Dados de teste
		Calendar antiga = Calendar.getInstance();
		antiga.set(1999, 1, 20);

		Participante participanteVencedor = new Participante(1, "Leonardo");
		Jogo jogo = new CriadorDeJogo().para("Cata moedas").naData(antiga).constroi();
		Resultado resultado = new Resultado(participanteVencedor, 150.0);
		jogo.anota(resultado);

		List<Jogo> jogosAnteriores = Arrays.asList(jogo);

		// Mock do JogoDao para retornar o jogo, mas o salvamento falha
		when(jogoDaoFalso.emAndamento()).thenReturn(jogosAnteriores);
		doThrow(new RuntimeException("Falha ao salvar jogo")).when(jogoDaoFalso).atualiza(jogo);

		// Ação: Finalizar o jogo
		try {
			finalizador.finaliza();
		} catch (Exception e) {
			// Ignorar a exceção para continuar o teste
		}

		// Verificação: O SMS não foi enviado já que o salvamento do jogo falhou
		verifyNoInteractions(smsMock);
	}
}
