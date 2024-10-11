package infra;

import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import br.com.valueprojects.mock_spring.model.Jogo;
import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Resultado;



public class JogoDao {

	private Connection conexao;

	public JogoDao() {
		try {
			this.conexao = DriverManager.getConnection(
					"jdbc:mysql://localhost/mocks", "root", "");
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private Calendar data(Date date) {
		Calendar c = Calendar.getInstance();
		c.setTime(date);
		return c;
	}

	public void salva(Jogo jogo) {
		try {
			String sql = "INSERT INTO JOGO (DESCRICAO, DATA, FINALIZADO) VALUES (?,?,?);";
			PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			ps.setString(1, jogo.getDescricao());
			ps.setDate(2, new java.sql.Date(jogo.getData().getTimeInMillis()));
			ps.setBoolean(3, jogo.isFinalizado());
			
			ps.execute();
			
			ResultSet generatedKeys = ps.getGeneratedKeys();
	        if (generatedKeys.next()) {
	            jogo.setId(generatedKeys.getInt(1));
	        }
			
			for(Resultado resultado : jogo.getResultados()) {
				sql = "INSERT INTO RESULTADOS (JOGO_ID, PARTICIPANTE_ID, VALOR) VALUES (?,?,?);";
				PreparedStatement ps2 = conexao.prepareStatement(sql);
				ps2.setInt(1, jogo.getId());
				ps2.setInt(2, resultado.getParticipante().getId());
				ps2.setDouble(3, resultado.getMetrica());
				
				ps2.execute();
				ps2.close();
				
			}
			
			ps.close();
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	
	public List<Jogo> finalizados() {
		return foiFinalizado(true);
	}
	
	public List<Jogo> emAndamento() {
		return foiFinalizado(false);
	}
	
	private List<Jogo> foiFinalizado(boolean status) {
		try {
			String sql = "SELECT * FROM JOGO WHERE FINALIZADO = " + status + ";";
			
			PreparedStatement ps = conexao.prepareStatement(sql);
			ResultSet rs = ps.executeQuery();
			
			List<Jogo> jogos = new ArrayList<Jogo>();
			while(rs.next()) {
				Jogo jogo = new Jogo(rs.getString("descricao"), data(rs.getDate("data")));
				jogo.setId(rs.getInt("id"));
				if(rs.getBoolean("finalizado")) jogo.finaliza();
				
				String sql2 = "SELECT VALOR, NOME, U.ID AS PARTICIPANTE_ID, L.ID AS RESULTADO_ID FROM RESULTADOS L INNER JOIN PARTICIPANTE U ON U.ID = L.PARTICIPANTE_ID WHERE JOGO_ID = " + rs.getInt("id");
				PreparedStatement ps2 = conexao.prepareStatement(sql2);
				ResultSet rs2 = ps2.executeQuery();
				
				while(rs2.next()) {
					Participante participante = new Participante(rs2.getInt("id"), rs2.getString("nome"));
					Resultado resultado = new Resultado(participante, rs2.getDouble("metrica"));
					
					jogo.anota(resultado);
				}
				rs2.close();
				ps2.close();
				
				jogos.add(jogo);
				
			}
			rs.close();
			ps.close();
			
			return jogos;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}

	public void atualiza(Jogo jogo) {
		
		try {
			String sql = "UPDATE JOGO SET DESCRICAO=?, DATA=?, FINALIZADO=? WHERE ID = ?;";
			PreparedStatement ps = conexao.prepareStatement(sql);
			ps.setString(1, jogo.getDescricao());
			ps.setDate(2, new java.sql.Date(jogo.getData().getTimeInMillis()));
			ps.setBoolean(3, jogo.isFinalizado());
			ps.setInt(4, jogo.getId());

			ps.execute();
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	public int x() { return 10; }
}
