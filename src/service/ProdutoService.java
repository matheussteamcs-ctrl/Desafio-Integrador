package service;

import exception.ValidacaoException;
import model.Produto;
import repository.ProdutoRepository;
import java.util.List;

public class ProdutoService {

    private final ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Produto produto) throws ValidacaoException {
        if (produto.getNome() == null || produto.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do produto nao pode ser vazio.");
        }
        if (produto.getPreco() <= 0) {
            throw new ValidacaoException("Preco deve ser maior que zero.");
        }
        if (produto.getEstoque() < 0) {
            throw new ValidacaoException("Estoque nao pode ser negativo.");
        }
        repository.save(produto);
    }

    public List<Produto> listarTodos() {
        return repository.findAll();
    }

    public Produto buscarPorId(int id) {
        return repository.findById(id);
    }

    public void excluir(int id) {
        repository.delete(id);
    }
}