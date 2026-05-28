package src.model;
 
public class ItemPedido {
 
    private final int id;
    private final int pedidoId;
    private final int produtoId;
    private final String nomeProduto;
    private final int quantidade;
    private final double precoUnitario;
 
    public ItemPedido(int id, int pedidoId, int produtoId, String nomeProduto, int quantidade, double precoUnitario) {
        this.id = id;
        this.pedidoId = pedidoId;
        this.produtoId = produtoId;
        this.nomeProduto = nomeProduto;
        this.quantidade = quantidade;
        this.precoUnitario = precoUnitario;
    }
 
    public int getId() {
        return id;
    }
 
    public int getPedidoId() {
        return pedidoId;
    }
 
    public int getProdutoId() {
        return produtoId;
    }
 
    public String getNomeProduto() {
        return nomeProduto;
    }
 
    public int getQuantidade() {
        return quantidade;
    }
 
    public double getPrecoUnitario() {
        return precoUnitario;
    }
 
    public double getSubtotal() {
        return quantidade * precoUnitario;
    }
 
    @Override
    public String toString() {
        return "ItemPedido{" +
                "id=" + id +
                ", pedidoId=" + pedidoId +
                ", produtoId=" + produtoId +
                ", nomeProduto='" + nomeProduto + '\'' +
                ", quantidade=" + quantidade +
                ", precoUnitario=" + precoUnitario +
                ", subtotal=" + getSubtotal() +
                '}';
    }
}