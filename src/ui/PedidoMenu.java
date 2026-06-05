package ui;

import model.ItemPedido;
import model.Pedido;
import model.Produto;
import service.PedidoService;
import service.ProdutoService;
import exception.EstoqueInsuficienteException;
import exception.PedidoNotFoundException;
import exception.ValidacaoException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class PedidoMenu {

    private final PedidoService pedidoService;
    private final ProdutoService produtoService;
    private final Scanner scanner;

    public PedidoMenu(PedidoService pedidoService, ProdutoService produtoService, Scanner scanner) {
        this.pedidoService = pedidoService;
        this.produtoService = produtoService;
        this.scanner = scanner;
    }

    public void exibir() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n===== MENU DE PEDIDOS =====");
            System.out.println("1 - Criar novo pedido");
            System.out.println("2 - Listar todos os pedidos");
            System.out.println("3 - Buscar pedido por ID");
            System.out.println("0 - Voltar");
            System.out.print("Opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero valido.");
                continue;
            }

            switch (opcao) {
                case 1 -> criarPedido();
                case 2 -> listarPedidos();
                case 3 -> buscarPorId();
                case 0 -> System.out.println("Voltando ao menu principal...");
                default -> System.out.println("Opcao invalida.");
            }
        }
    }

    private void criarPedido() {
        System.out.println("\n-- Criar Pedido --");
        System.out.print("ID do cliente: ");
        int clienteId;
        try {
            clienteId = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
            return;
        }

        // Pedido nasce com status ABERTO (construtor sem itens)
        List<ItemPedido> itens = new ArrayList<>();
        boolean adicionando = true;

        while (adicionando) {
            System.out.println("\nProdutos disponiveis:");
            List<Produto> produtos = produtoService.listarTodos();
            if (produtos.isEmpty()) {
                System.out.println("Nenhum produto cadastrado. Impossivel criar pedido.");
                return;
            }

            System.out.printf("%-6s %-30s %-12s %-10s%n", "ID", "Nome", "Preco", "Estoque");
            System.out.println("-".repeat(60));
            for (Produto p : produtos) {
                System.out.printf("%-6d %-30s R$%-10.2f %-10d%n",
                        p.getId(), p.getNome(), p.getPreco(), p.getEstoque());
            }

            System.out.print("\nID do produto a adicionar (0 para encerrar): ");
            int produtoId;
            try {
                produtoId = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("ID invalido.");
                continue;
            }

            if (produtoId == 0) {
                adicionando = false;
                continue;
            }

            Produto produtoSelecionado = produtoService.buscarPorId(produtoId);
            if (produtoSelecionado == null) {
                System.out.println("Produto nao encontrado.");
                continue;
            }

            System.out.print("Quantidade: ");
            int quantidade;
            try {
                quantidade = Integer.parseInt(scanner.nextLine().trim());
                if (quantidade <= 0) {
                    System.out.println("Quantidade deve ser maior que zero.");
                    continue;
                }
            } catch (NumberFormatException e) {
                System.out.println("Quantidade invalida.");
                continue;
            }

            // id 0 pois ainda nao foi persistido (sem pedido_id definido)
            ItemPedido item = new ItemPedido(
                    0,
                    0,
                    produtoSelecionado.getId(),
                    produtoSelecionado.getNome(),
                    quantidade,
                    produtoSelecionado.getPreco()
            );
            itens.add(item);
            System.out.printf("Adicionado: %s x%d (R$%.2f cada)%n",
                    produtoSelecionado.getNome(), quantidade, produtoSelecionado.getPreco());
        }

        if (itens.isEmpty()) {
            System.out.println("Nenhum item adicionado. Pedido cancelado.");
            return;
        }

        // Exibir resumo antes de confirmar
        System.out.println("\n--- Resumo do Pedido ---");
        double total = 0;
        for (ItemPedido item : itens) {
            System.out.printf("  %s x%d = R$%.2f%n",
                    item.getNomeProduto(), item.getQuantidade(), item.getSubtotal());
            total += item.getSubtotal();
        }
        System.out.printf("Total: R$%.2f%n", total);
        System.out.print("Confirmar pedido? (s/n): ");
        String confirmacao = scanner.nextLine().trim();

        if (!confirmacao.equalsIgnoreCase("s")) {
            System.out.println("Pedido cancelado.");
            return;
        }

        // Monta o Pedido com status ABERTO e chama o service que muda para FILA
        Pedido pedido = new Pedido(0, clienteId, enums.StatusPedido.ABERTO, LocalDateTime.now(), itens);

        try {
            pedidoService.confirmarPedido(pedido);
            System.out.println("Pedido confirmado e enviado para a fila de processamento!");
        } catch (EstoqueInsuficienteException e) {
            System.out.println("Erro: " + e.getMessage());
        } catch (ValidacaoException e) {
            System.out.println("Erro de validacao: " + e.getMessage());
        }
    }

    private void listarPedidos() {
        System.out.println("\n-- Lista de Pedidos --");
        List<Pedido> pedidos = pedidoService.listarTodos();

        if (pedidos.isEmpty()) {
            System.out.println("Nenhum pedido encontrado.");
            return;
        }

        for (Pedido p : pedidos) {
            exibirPedido(p);
        }
    }

    private void buscarPorId() {
        System.out.print("\nDigite o ID do pedido: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Pedido pedido = pedidoService.buscarPorId(id);

            if (pedido == null) {
                System.out.println("Pedido nao encontrado.");
            } else {
                exibirPedido(pedido);
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
        } catch (PedidoNotFoundException e) {
            System.out.println(e.getMessage());
        }
    }

    private void exibirPedido(Pedido pedido) {
        System.out.println("\n" + "-".repeat(50));
        System.out.printf("Pedido #%d | Cliente ID: %d | Status: %s | Data: %s%n",
                pedido.getId(),
                pedido.getClienteId(),
                pedido.getStatus().name(),
                pedido.getDataCriacao().toString().replace("T", " ").substring(0, 16));

        if (pedido.getItens().isEmpty()) {
            System.out.println("  (sem itens registrados)");
        } else {
            double total = 0;
            for (ItemPedido item : pedido.getItens()) {
                System.out.printf("  -> %s x%d @ R$%.2f = R$%.2f%n",
                        item.getNomeProduto(),
                        item.getQuantidade(),
                        item.getPrecoUnitario(),
                        item.getSubtotal());
                total += item.getSubtotal();
            }
            System.out.printf("  Total: R$%.2f%n", total);
        }
    }
}
