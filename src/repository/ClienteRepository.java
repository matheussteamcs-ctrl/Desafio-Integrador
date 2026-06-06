package repository;

import model.Cliente;
import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ClienteRepository {

public void save(Cliente cliente) {
    String sql = "INSERT INTO cliente (nome, email) VALUES (?, ?)";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setString(1, cliente.getNome());
        ps.setString(2, cliente.getEmail());
        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao salvar cliente: " + e.getMessage());
    }
}

public Cliente findById(int id) {
    String sql = "SELECT id_cliente, nome, email FROM cliente WHERE id_cliente = ?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);

        try (ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Cliente(
                    rs.getInt("id_cliente"),
                    rs.getString("nome"),
                    rs.getString("email")
                );
            }
            return null;
        }

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao buscar cliente: " + e.getMessage());
    }
}

public List<Cliente> findAll() {
    String sql = "SELECT id_cliente, nome, email FROM cliente";
    List<Cliente> clientes = new ArrayList<>();

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {

        while (rs.next()) {
            clientes.add(new Cliente(
                rs.getInt("id_cliente"),
                rs.getString("nome"),
                rs.getString("email")
            ));
        }

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao listar clientes: " + e.getMessage());
    }

    return clientes;
}

public void delete(int id) {
    String sql = "DELETE FROM cliente WHERE id_cliente = ?";

    try (Connection conn = ConnectionFactory.getConnection();
         PreparedStatement ps = conn.prepareStatement(sql)) {

        ps.setInt(1, id);
        ps.executeUpdate();

    } catch (SQLException e) {
        throw new RuntimeException("Erro ao deletar cliente: " + e.getMessage());
    }
}

}
