package src.exception;
 
public class PedidoNotFoundException extends RuntimeException {
 
    public PedidoNotFoundException(String mensagem) {
        super(mensagem);
    }
 
    public PedidoNotFoundException(int pedidoId) {
        super("Pedido não encontrado com o ID: " + pedidoId);
    }
}