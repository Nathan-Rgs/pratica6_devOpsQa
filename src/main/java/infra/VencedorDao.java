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

import br.com.valueprojects.mock_spring.model.Participante;
import br.com.valueprojects.mock_spring.model.Vencedor;
import br.com.valueprojects.mock_spring.model.Jogo;

public class VencedorDao {

    private Connection conexao;

    public VencedorDao() {
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

    public void salvaVencedor(Vencedor vencedor) {
        try {
            String sql = "INSERT INTO VENCEDOR (PARTICIPANTE_ID, JOGO_ID, DATA) VALUES (?,?,?);";
            PreparedStatement ps = conexao.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            ps.setInt(1, vencedor.getParticipante().getId());
            ps.setInt(2, vencedor.getJogo().getId());
            ps.setDate(3, new java.sql.Date(vencedor.getData().getTimeInMillis()));

            ps.execute();

            ResultSet generatedKeys = ps.getGeneratedKeys();
            if (generatedKeys.next()) {
                vencedor.setId(generatedKeys.getInt(1));
            }

            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Vencedor> listaVencedores() {
        try {
            String sql = "SELECT V.ID, V.DATA, P.NOME, J.DESCRICAO, P.ID AS PARTICIPANTE_ID, J.ID AS JOGO_ID " +
                    "FROM VENCEDOR V " +
                    "INNER JOIN PARTICIPANTE P ON P.ID = V.PARTICIPANTE_ID " +
                    "INNER JOIN JOGO J ON J.ID = V.JOGO_ID;";

            PreparedStatement ps = conexao.prepareStatement(sql);
            ResultSet rs = ps.executeQuery();

            List<Vencedor> vencedores = new ArrayList<>();
            while (rs.next()) {
                Participante participante = new Participante(rs.getInt("PARTICIPANTE_ID"), rs.getString("NOME"));
                String descricao = rs.getString("DESCRICAO");  // Pegue a descrição do jogo
                Calendar dataJogo = data(rs.getDate("DATA"));  // Pegue a data do jogo

                // Agora crie o objeto Jogo
                Jogo jogo = new Jogo(descricao, dataJogo);  // Use a descrição e a data para criar o objeto Jogo

                // Crie o objeto Vencedor com o objeto Jogo criado
                Vencedor vencedor = new Vencedor(
                        rs.getInt("ID"),  // O ID do vencedor
                        participante,     // O participante já existente
                        data(rs.getDate("DATA")),  // A data da vitória
                        jogo              // O objeto Jogo que você acabou de criar
                );
                vencedores.add(vencedor);
            }

            rs.close();
            ps.close();

            return vencedores;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void atualizaVencedor(Vencedor vencedor) {
        try {
            String sql = "UPDATE VENCEDOR SET PARTICIPANTE_ID=?, JOGO_ID=?, DATA=? WHERE ID=?;";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, vencedor.getParticipante().getId());
            ps.setInt(2, vencedor.getJogo().getId());
            ps.setDate(3, new java.sql.Date(vencedor.getData().getTimeInMillis()));
            ps.setInt(4, vencedor.getId());

            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deletaVencedor(int id) {
        try {
            String sql = "DELETE FROM VENCEDOR WHERE ID=?;";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);

            ps.execute();
            ps.close();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public Vencedor buscaPorId(int id) {
        try {
            String sql = "SELECT V.ID, V.DATA, P.NOME, J.DESCRICAO, P.ID AS PARTICIPANTE_ID, J.ID AS JOGO_ID " +
                    "FROM VENCEDOR V " +
                    "INNER JOIN PARTICIPANTE P ON P.ID = V.PARTICIPANTE_ID " +
                    "INNER JOIN JOGO J ON J.ID = V.JOGO_ID WHERE V.ID = ?;";
            PreparedStatement ps = conexao.prepareStatement(sql);
            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            Vencedor vencedor = null;
            if (rs.next()) {
                Participante participante = new Participante(rs.getInt("PARTICIPANTE_ID"), rs.getString("NOME"));
                String descricao = rs.getString("DESCRICAO");  // Pegue a descrição do jogo
                Calendar dataJogo = data(rs.getDate("DATA"));  // Pegue a data do jogo

                // Agora crie o objeto Jogo
                Jogo jogo = new Jogo(descricao, dataJogo);  // Use a descrição e a data para criar o objeto Jogo

                // Crie o objeto Vencedor com o objeto Jogo criado
                vencedor = new Vencedor(
                        rs.getInt("ID"),  // O ID do vencedor
                        participante,     // O participante já existente
                        data(rs.getDate("DATA")),  // A data da vitória
                        jogo              // O objeto Jogo que você acabou de criar
                );
            }

            rs.close();
            ps.close();

            return vencedor;
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
