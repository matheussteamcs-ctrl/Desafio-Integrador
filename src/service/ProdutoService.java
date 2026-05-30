package src.service;

import src.model.Produto;
import src.repository.ProdutoRepository;
import src.exception.ValidacaoException;

public class ProdutoService {

    private ProdutoRepository repository;

    public ProdutoService(ProdutoRepository repository) {
        this.repository = repository;
    }

    public void salvar(Produto produto) throws ValidacaoException {

        if (produto.getPreco() <= 0) {
            throw new ValidacaoException("Preço deve ser maior que zero.");
        }

        if (produto.getEstoque() <= 0) {
            throw new ValidacaoException("Estoque não pode ser negativo.");
        }

        repository.save(produto);
    }
}