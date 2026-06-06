package repository;

import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RelatorioRepository {

    public List<String[]> relatorioPedidosPorCliente() {
        String sql =
            "SELECT c.nome AS cliente, " +
            "       COUNT(p.id) AS total_pedidos, " +
            "       SUM(ip.quantidade * ip.preco_unitario) AS valor_total, " +
            "       AVG(ip.quantidade * ip.preco_unitario) AS ticket_medio " +
            "FROM cliente c " +
            "JOIN pedido p ON p.cliente_id = c.id " +
            "JOIN item_pedido ip ON ip.pedido_id = p.id " +
            "WHERE p.status = 'FINALIZADO' " +
            "GROUP BY c.id, c.nome " +
            "ORDER BY valor_total DESC";

        List<String[]> resultado = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] linha = {
                    rs.getString("cliente"),
                    String.valueOf(rs.getInt("total_pedidos")),
                    String.format("R$ %.2f", rs.getDouble("valor_total")),
                    String.format("R$ %.2f", rs.getDouble("ticket_medio"))
                };
                resultado.add(linha);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatorio de pedidos por cliente: " + e.getMessage());
        }

        return resultado;
    }

    public List<String[]> relatorioProdutosMaisVendidos() {
        String sql =
            "SELECT pr.nome AS produto, " +
            "       pr.categoria AS categoria, " +
            "       SUM(ip.quantidade) AS quantidade_vendida " +
            "FROM item_pedido ip " +
            "JOIN produto pr ON pr.id = ip.produto_id " +
            "GROUP BY pr.id, pr.nome, pr.categoria " +
            "ORDER BY quantidade_vendida DESC";

        List<String[]> resultado = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                String[] linha = {
                    rs.getString("produto"),
                    rs.getString("categoria"),
                    String.valueOf(rs.getInt("quantidade_vendida"))
                };
                resultado.add(linha);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao gerar relatorio de produtos mais vendidos: " + e.getMessage());
        }

        return resultado;
    }
}