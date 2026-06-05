package src.service;

import src.enums.StatusPedido;
import src.exception.EstoqueInsuficienteException;
import src.model.ItemPedido;
import src.model.Pedido;
import src.repository.PedidoRepository;
import src.repository.ProdutoRepository;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository, ProdutoRepository produtoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = produtoRepository;
    }

    public Pedido confirmarPedido(Pedido pedido) throws EstoqueInsuficienteException {

        for (ItemPedido item : pedido.getItens()) {
            var produto = produtoRepository.findById(item.getProdutoId());

            if (produto == null || produto.getEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(item.getProdutoId(), item.getQuantidade());
            }
        }

        Pedido pedidoParaSalvar = new Pedido(
                pedido.getId(),
                pedido.getClienteId(),
                StatusPedido.FILA,
                pedido.getDataCriacao(),
                pedido.getItens()
        );

        return pedidoRepository.save(pedidoParaSalvar);
    }
}