package src.repository;

import src.exception.EstoqueInsuficienteException;
import src.model.ItemPedido;
import src.model.Pedido;
import src.enums.StatusPedido;
import src.util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PedidoRepository {

    public void salvar(Pedido pedido) {
        String sqlPedido  = "INSERT INTO pedido (cliente_id, status, data_criacao) VALUES (?, ?, ?)";
        String sqlItem    = "INSERT INTO item_pedido (pedido_id, produto_id, nome_produto, quantidade, preco_unitario) VALUES (?, ?, ?, ?, ?)";
        String sqlEstoque = "UPDATE produto SET estoque = estoque - ? WHERE id = ? AND estoque >= ?";

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
                    ps.setString(3, item.getNomeProduto());
                    ps.setInt(4, item.getQuantidade());
                    ps.setDouble(5, item.getPrecoUnitario());
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
            "SELECT p.id, p.cliente_id, p.status, p.data_criacao, " +
            "       ip.id AS item_id, ip.produto_id, ip.nome_produto, " +
            "       ip.quantidade, ip.preco_unitario " +
            "FROM pedido p " +
            "LEFT JOIN item_pedido ip ON ip.pedido_id = p.id " +
            "LEFT JOIN produto pr ON pr.id = ip.produto_id " +
            "ORDER BY p.id";

        List<Pedido> pedidos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Pedido pedidoAtual = null;

            while (rs.next()) {
                int pedidoId = rs.getInt("id");

                
                if (pedidoAtual == null || pedidoAtual.getId() != pedidoId) {
                    List<ItemPedido> itens = new ArrayList<>();
                    pedidoAtual = new Pedido(
                        pedidoId,
                        rs.getInt("cliente_id"),
                        StatusPedido.valueOf(rs.getString("status")),
                        rs.getTimestamp("data_criacao").toLocalDateTime(),
                        itens
                    );
                    pedidos.add(pedidoAtual);
                }

                
                if (rs.getInt("item_id") != 0) {
                    pedidoAtual.getItens().add(new ItemPedido(
                        rs.getInt("item_id"),
                        pedidoId,
                        rs.getInt("produto_id"),
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
            "SELECT p.id, p.cliente_id, p.status, p.data_criacao, " +
            "       ip.id AS item_id, ip.produto_id, ip.nome_produto, " +
            "       ip.quantidade, ip.preco_unitario " +
            "FROM pedido p " +
            "LEFT JOIN item_pedido ip ON ip.pedido_id = p.id " +
            "LEFT JOIN produto pr ON pr.id = ip.produto_id " +
            "WHERE p.id = ? " +
            "ORDER BY p.id";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                Pedido pedido = null;

                while (rs.next()) {
                    if (pedido == null) {
                        List<ItemPedido> itens = new ArrayList<>();
                        pedido = new Pedido(
                            rs.getInt("id"),
                            rs.getInt("cliente_id"),
                            StatusPedido.valueOf(rs.getString("status")),
                            rs.getTimestamp("data_criacao").toLocalDateTime(),
                            itens
                        );
                    }

                    if (rs.getInt("item_id") != 0) {
                        pedido.getItens().add(new ItemPedido(
                            rs.getInt("item_id"),
                            rs.getInt("id"),
                            rs.getInt("produto_id"),
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