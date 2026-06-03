package ui;

import enums.Categoria;
import model.Produto;
import service.ProdutoService;
import exception.ValidacaoException;

import java.util.List;
import java.util.Scanner;

public class ProdutoMenu {

    private final ProdutoService produtoService;
    private final Scanner scanner;

    public ProdutoMenu(ProdutoService produtoService, Scanner scanner) {
        this.produtoService = produtoService;
        this.scanner = scanner;
    }

    public void exibir() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n===== MENU DE PRODUTOS =====");
            System.out.println("1 - Cadastrar produto");
            System.out.println("2 - Listar produtos");
            System.out.println("3 - Buscar produto por ID");
            System.out.println("4 - Excluir produto");
            System.out.println("0 - Voltar");
            System.out.print("Opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero valido.");
                continue;
            }

            switch (opcao) {
                case 1 -> cadastrar();
                case 2 -> listar();
                case 3 -> buscarPorId();
                case 4 -> excluir();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void cadastrar() {
        System.out.println("\n-- Cadastrar Produto --");
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        double preco = 0;
        System.out.print("Preco (ex: 19.90): ");
        try {
            preco = Double.parseDouble(scanner.nextLine().trim().replace(",", "."));
        } catch (NumberFormatException e) {
            System.out.println("Preco invalido.");
            return;
        }

        int estoque = 0;
        System.out.print("Estoque: ");
        try {
            estoque = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Estoque invalido.");
            return;
        }

        Categoria categoria = selecionarCategoria();
        if (categoria == null) return;

        // id 0 pois o banco gera o ID automaticamente (AUTO_INCREMENT)
        Produto produto = new Produto(0, nome, preco, estoque, categoria);

        try {
            produtoService.salvar(produto);
            System.out.println("Produto cadastrado com sucesso!");
        } catch (ValidacaoException e) {
            System.out.println("Erro de validacao: " + e.getMessage());
        }
    }

    private Categoria selecionarCategoria() {
        System.out.println("Categoria:");
        Categoria[] categorias = Categoria.values();
        for (int i = 0; i < categorias.length; i++) {
            System.out.printf("%d - %s%n", i + 1, categorias[i].name());
        }
        System.out.print("Opcao: ");
        try {
            int idx = Integer.parseInt(scanner.nextLine().trim()) - 1;
            if (idx < 0 || idx >= categorias.length) {
                System.out.println("Categoria invalida.");
                return null;
            }
            return categorias[idx];
        } catch (NumberFormatException e) {
            System.out.println("Opcao invalida.");
            return null;
        }
    }

    private void listar() {
        System.out.println("\n-- Lista de Produtos --");
        List<Produto> produtos = produtoService.listarTodos();

        if (produtos.isEmpty()) {
            System.out.println("Nenhum produto cadastrado.");
            return;
        }

        System.out.printf("%-6s %-30s %-12s %-10s %-15s%n",
                "ID", "Nome", "Preco", "Estoque", "Categoria");
        System.out.println("-".repeat(75));
        for (Produto p : produtos) {
            System.out.printf("%-6d %-30s R$%-10.2f %-10d %-15s%n",
                    p.getId(), p.getNome(), p.getPreco(),
                    p.getEstoque(), p.getCategoria().name());
        }
    }

    private void buscarPorId() {
        System.out.print("\nDigite o ID do produto: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Produto produto = produtoService.buscarPorId(id);

            if (produto == null) {
                System.out.println("Produto nao encontrado.");
            } else {
                System.out.printf("ID: %d | Nome: %s | Preco: R$%.2f | Estoque: %d | Categoria: %s%n",
                        produto.getId(), produto.getNome(), produto.getPreco(),
                        produto.getEstoque(), produto.getCategoria().name());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
        }
    }

    private void excluir() {
        System.out.print("\nDigite o ID do produto a excluir: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            produtoService.excluir(id);
            System.out.println("Produto excluido com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
        } catch (RuntimeException e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }
}
