public enum Categoria{
    Alimentos,
    Eletronicos,
    Livros
}

public enum StatusPedido{
    Aberto,
    Fila,
    Processando,
    Finalizado
}

public class Cliente{
    private int id;
    private String nome;
    private String email;

    public Cliente(int id, String nome, String email){
        this.id = id;
        this.nome = nome;
        this.email = email;
    }

    public int getId(){
        return id;
    }

    public String getNome(){
        return nome;
    }

    public String getEmail(){
        return email;
    }
}

public class Produto{
    private int id;
    private String nome;
    private double preco;
    private int estoque;
    private Categoria categoria;

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