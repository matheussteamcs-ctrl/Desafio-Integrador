
package src.model;
import src.enums.Categoria; 

public class Produto{
    private final int id;
    private final String nome;
    private final double preco;
    private final int estoque;
    private final Categoria categoria;

    public Produto(int id, String nome, double preco, int estoque, Categoria categoria){
        this.id = id;
        this.nome = nome;
        this.preco = preco;
        this.estoque = estoque;
        this.categoria = categoria;
    }

    public int getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public double getPreco(){
        return preco;
    }

    public int getEstoque(){
        return estoque;
    }

    public Categoria getCategoria(){
        return categoria;
    }
}