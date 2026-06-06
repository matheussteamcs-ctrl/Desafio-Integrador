package exception;
 
public class EstoqueInsuficienteException extends RuntimeException {
 
    public EstoqueInsuficienteException(String mensagem) {
        super(mensagem);
    }
 
    public EstoqueInsuficienteException(int produtoId, int quantidadeSolicitada) {
        super("Estoque insuficiente para o produto ID " + produtoId +
              ". Quantidade solicitada: " + quantidadeSolicitada);
    }
}
 