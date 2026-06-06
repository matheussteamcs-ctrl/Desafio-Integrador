package repository;

import enums.Categoria;
import model.Produto;
import util.ConnectionFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    public void save(Produto produto) {
        String sql = "INSERT INTO produto (nome, preco, estoque, categoria) VALUES (?, ?, ?, ?)";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, produto.getNome());
            ps.setDouble(2, produto.getPreco());
            ps.setInt(3, produto.getEstoque());
            ps.setString(4, produto.getCategoria().name());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao salvar produto: " + e.getMessage());
        }
    }

    public Produto findById(int id) {

        String sql =
                "SELECT id_produto, nome, preco, estoque, categoria " +
                "FROM produto WHERE id_produto = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return new Produto(
                            rs.getInt("id_produto"),
                            rs.getString("nome"),
                            rs.getDouble("preco"),
                            rs.getInt("estoque"),
                            Categoria.valueOf(rs.getString("categoria"))
                    );
                }

                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao buscar produto: " + e.getMessage());
        }
    }

    public List<Produto> findAll() {

        String sql =
                "SELECT id_produto, nome, preco, estoque, categoria FROM produto";

        List<Produto> produtos = new ArrayList<>();

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                produtos.add(
                        new Produto(
                                rs.getInt("id_produto"),
                                rs.getString("nome"),
                                rs.getDouble("preco"),
                                rs.getInt("estoque"),
                                Categoria.valueOf(rs.getString("categoria"))
                        )
                );
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao listar produtos: " + e.getMessage());
        }

        return produtos;
    }

    public void delete(int id) {

        String sql = "DELETE FROM produto WHERE id_produto = ?";

        try (Connection conn = ConnectionFactory.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erro ao remover produto: " + e.getMessage());
        }
    }
}