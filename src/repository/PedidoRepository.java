package repository;

import exception.EstoqueInsuficienteException;
import model.ItemPedido;
import model.Pedido;
import enums.StatusPedido;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {

    public void salvar(Pedido pedido) {
        String sqlPedido  = "INSERT INTO pedido (id_cliente, status, data_criacao) VALUES (?, ?, ?)";
        String sqlItem    = "INSERT INTO item_pedido (id_pedido, id_produto, quantidade, preco_unitario) VALUES (?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produto SET estoque = estoque - ? WHERE id_produto = ? AND estoque >= ?";

        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            conn.setAutoCommit(false); 
            int pedidoId;
            try (PreparedStatement ps = conn.prepareStatement(sqlPedido, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, pedido.getClienteId());
                ps.setString(2, StatusPedido.FILA.name());
                ps.setTimestamp(3, Timestamp.valueOf(pedido.getDataCriacao()));
                ps.executeUpdate();

                try (ResultSet rs = ps.getGeneratedKeys()) {
                    rs.next();
                    pedidoId = rs.getInt(1);
                }
            }

           
            for (ItemPedido item : pedido.getItens()) {

                try (PreparedStatement ps = conn.prepareStatement(sqlItem)) {
                    ps.setInt(1, pedidoId);
                    ps.setInt(2, item.getProdutoId());
                    ps.setInt(3, item.getQuantidade());
                    ps.setDouble(4, item.getPrecoUnitario());
                    ps.executeUpdate();
                }

                try (PreparedStatement ps = conn.prepareStatement(sqlEstoque)) {
                    ps.setInt(1, item.getQuantidade()); 
                    ps.setInt(2, item.getProdutoId());  
                    ps.setInt(3, item.getQuantidade()); 
                    int linhasAfetadas = ps.executeUpdate();

                    if (linhasAfetadas == 0) {
                        conn.rollback(); 
                        throw new EstoqueInsuficienteException(item.getProdutoId(), item.getQuantidade());
                    }
                }
            }

            conn.commit(); 

        } catch (EstoqueInsuficienteException e) {
            throw e; 
        } catch (SQLException e) {
            if (conn != null) {
                try { conn.rollback(); } catch (SQLException ex) { ex.printStackTrace(); }
            }
            throw new RuntimeException("Erro ao salvar pedido: " + e.getMessage());
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true); 
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    
    public List<Pedido> findAll() {
        String sql =
            "SELECT p.id_pedido, p.id_cliente, p.status, p.data_criacao, " +
            "       ip.id_item, ip.id_produto, pr.nome AS nome_produto, " +
            "       ip.quantidade, ip.preco_unitario " +
            "FROM pedido p " +
            "LEFT JOIN item_pedido ip ON ip.id_pedido = p.id_pedido " +
            "LEFT JOIN produto pr ON pr.id_produto = ip.id_produto " +
            "ORDER BY p.id_pedido";

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Pedido pedidoAtual = null;

            while (rs.next()) {
                int pedidoId = rs.getInt("id_pedido");

                
                if (pedidoAtual == null || pedidoAtual.getId() != pedidoId) {
                    List<ItemPedido> itens = new ArrayList<>();
                    pedidoAtual = new Pedido(
                        pedidoId,
                        rs.getInt("id_cliente"),
                        StatusPedido.valueOf(rs.getString("status")),
                        rs.getTimestamp("data_criacao").toLocalDateTime(),
                        itens
                    );
                    pedidos.add(pedidoAtual);
                }

                
                if (rs.getInt("id_item") != 0) {
                    pedidoAtual.getItens().add(new ItemPedido(
                        rs.getInt("id_item"),
                        rs.getInt("id_pedido"),
                        rs.getInt("id_produto"),
                        rs.getString("nome_produto"),
                        rs.getInt("quantidade"),
                        rs.getDouble("preco_unitario")
                    ));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar pedidos: " + e.getMessage());
        }

        return pedidos;
    }

    public Pedido findById(int id) {
        String sql =
            "SELECT p.id_pedido, p.id_cliente, p.status, p.data_criacao, " +
            "       ip.id_item, ip.id_produto, pr.nome AS nome_produto, " +
            "       ip.quantidade, ip.preco_unitario " +
            "FROM pedido p " +
            "LEFT JOIN item_pedido ip ON ip.id_pedido = p.id_pedido " +
            "LEFT JOIN produto pr ON pr.id_produto = ip.id_produto " +
            "WHERE p.id_pedido = ? " +
            "ORDER BY p.id_pedido";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                Pedido pedido = null;

                while (rs.next()) {
                    if (pedido == null) {
                        List<ItemPedido> itens = new ArrayList<>();
                        pedido = new Pedido(
                            rs.getInt("id_pedido"),
                            rs.getInt("id_cliente"),
                            StatusPedido.valueOf(rs.getString("status")),
                            rs.getTimestamp("data_criacao").toLocalDateTime(),
                            itens
                        );
                    }

                    if (rs.getInt("id_item") != 0) {
                        pedido.getItens().add(new ItemPedido(
                            rs.getInt("id_item"),
                            rs.getInt("id_pedido"),
                            rs.getInt("id_produto"),
                            rs.getString("nome_produto"),
                            rs.getInt("quantidade"),
                            rs.getDouble("preco_unitario")
                        ));
                    }
                }

                return pedido;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar pedido: " + e.getMessage());
        }
    }
}