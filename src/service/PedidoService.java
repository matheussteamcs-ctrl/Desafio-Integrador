package src.service;

import src.enums.StatusPedido;
import src.exception.EstoqueInsuficienteException;
import src.exception.PedidoNotFoundException;
import src.exception.ValidacaoException;
import src.model.ItemPedido;
import src.model.Pedido;
import src.model.Produto;
import src.repository.PedidoRepository;
import src.repository.ProdutoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public void confirmarPedido(Pedido pedido) throws EstoqueInsuficienteException {

        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new ValidacaoException("O pedido deve ter ao menos um item.");
        }

        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProdutoId());

            if (produto == null) {
                throw new ValidacaoException("Produto nao encontrado: ID " + item.getProdutoId());
            }

            if (produto.getEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(item.getProdutoId(), item.getQuantidade());
            }
        }

        Pedido pedidoParaSalvar = new Pedido(
                0,
                pedido.getClienteId(),
                StatusPedido.FILA,
                LocalDateTime.now(),
                pedido.getItens()
        );

        pedidoRepository.salvar(pedidoParaSalvar);
    }

    public List<Pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Pedido buscarPorId(int id) {
        Pedido pedido = pedidoRepository.findById(id);
        if (pedido == null) {
            throw new PedidoNotFoundException(id);
        }
        return pedido;
    }
}