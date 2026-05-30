package src.repository;

import src.model.Produto;
import java.util.ArrayList;
import java.util.List;

public class ProdutoRepository {

    public void save(Produto produto) {
        System.out.println("Produto salvo.");
    }

    public Produto findById(int id) {
        return null;
    }

    public List<Produto> findAll() {
        return new ArrayList<>();
    }

    public void delete(int id) {
        System.out.println("Produto removido.");
    }
}