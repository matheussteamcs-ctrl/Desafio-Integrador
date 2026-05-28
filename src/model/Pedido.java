package src.model;
 
import java.time.LocalDateTime;
import java.util.List;

import src.enums.StatusPedido;
 
public class Pedido {
 
    private final int id;
    private final int clienteId;
    private final StatusPedido status;
    private final LocalDateTime dataCriacao;
    private final List<ItemPedido> itens;
 
    public Pedido(int id, int clienteId, StatusPedido status, LocalDateTime dataCriacao, List<ItemPedido> itens) {
        this.id = id;
        this.clienteId = clienteId;
        this.status = status;
        this.dataCriacao = dataCriacao;
        this.itens = itens;
    }
 
    
    public Pedido(int clienteId) {
        this.id = 0;
        this.clienteId = clienteId;
        this.status = StatusPedido.ABERTO;
        this.dataCriacao = LocalDateTime.now();
        this.itens = new java.util.ArrayList<>();
    }
 
    public int getId() {
        return id;
    }
 
    public int getClienteId() {
        return clienteId;
    }
 
    public StatusPedido getStatus() {
        return status;
    }
 
    public LocalDateTime getDataCriacao() {
        return dataCriacao;
    }
 
    public List<ItemPedido> getItens() {
        return itens;
    }
 
    @Override
    public String toString() {
        return "Pedido{" +
                "id=" + id +
                ", clienteId=" + clienteId +
                ", status=" + status +
                ", dataCriacao=" + dataCriacao +
                ", itens=" + itens +
                '}';
    }
}