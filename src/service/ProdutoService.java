package service;

import enums.Categoria;
import exception.ValidacaoException;
import model.Produto;
import repository.ProdutoRepository;

import java.util.List;

public class ProdutoService {

    private final ProdutoRepository produtoRepository;

    public ProdutoService(ProdutoRepository produtoRepository) {
        this.produtoRepository = produtoRepository;
    }

    public void salvar(Produto produto) {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do produto nao pode ser vazio.");
        }
        if (produto.getPreco() <= 0) {
            throw new ValidacaoException("O preco deve ser maior que zero.");
        }
        if (produto.getEstoque() < 0) {
            throw new ValidacaoException("O estoque nao pode ser negativo.");
        }
        produtoRepository.save(produto);
    }

    public List<Produto> listarTodos() {
        return produtoRepository.findAll();
    }

    public Produto buscarPorId(int id) {
        return produtoRepository.findById(id);
    }

    public void excluir(int id) {
        produtoRepository.delete(id);
    }
}
