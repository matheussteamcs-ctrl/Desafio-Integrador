package service;

import enums.StatusPedido;
import exception.EstoqueInsuficienteException;
import exception.PedidoNotFoundException;
import exception.ValidacaoException;
import model.ItemPedido;
import model.Pedido;
import model.Produto;
import repository.PedidoRepository;
import repository.ProdutoRepository;

import java.time.LocalDateTime;
import java.util.List;

public class PedidoService {

    private final PedidoRepository pedidoRepository;
    private final ProdutoRepository produtoRepository;

    public PedidoService(PedidoRepository pedidoRepository) {
        this.pedidoRepository = pedidoRepository;
        this.produtoRepository = new ProdutoRepository();
    }

    /**
     * Valida estoque de todos os itens e, se ok, salva o pedido com status FILA.
     * O fluxo e: ABERTO (montagem no menu) -> FILA (apos confirmacao aqui).
     */
    public void confirmarPedido(Pedido pedido) {
        if (pedido.getItens() == null || pedido.getItens().isEmpty()) {
            throw new ValidacaoException("O pedido deve ter ao menos um item.");
        }

        // Verificar estoque de TODOS os itens antes de persistir
        for (ItemPedido item : pedido.getItens()) {
            Produto produto = produtoRepository.findById(item.getProdutoId());
            if (produto == null) {
                throw new ValidacaoException("Produto nao encontrado: ID " + item.getProdutoId());
            }
            if (produto.getEstoque() < item.getQuantidade()) {
                throw new EstoqueInsuficienteException(item.getProdutoId(), item.getQuantidade());
            }
        }

        // Cria novo Pedido com status FILA para persistencia
        // (o UPDATE condicional no repository e quem garante atomicidade real no banco)
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
