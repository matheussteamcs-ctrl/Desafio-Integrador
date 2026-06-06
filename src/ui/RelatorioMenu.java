package ui;

import repository.RelatorioRepository;

import java.util.List;
import java.util.Scanner;

public class RelatorioMenu {

private final RelatorioRepository repository;
private final Scanner scanner;

public RelatorioMenu(Scanner scanner) {
    this.repository = new RelatorioRepository();
    this.scanner = scanner;
}

public void exibir() {

    int opcao = -1;

    while (opcao != 0) {

        System.out.println("\n===== RELATORIOS =====");
        System.out.println("1 - Pedidos por cliente");
        System.out.println("2 - Produtos mais vendidos");
        System.out.println("0 - Voltar");
        System.out.print("Opcao: ");

        opcao = Integer.parseInt(scanner.nextLine());

        switch (opcao) {

            case 1 -> mostrarPedidosPorCliente();

            case 2 -> mostrarProdutosMaisVendidos();

            case 0 -> {
            }

            default -> System.out.println("Opcao invalida.");
        }
    }
}

private void mostrarPedidosPorCliente() {

    List<String[]> dados =
            repository.relatorioPedidosPorCliente();

    System.out.println("\n===== PEDIDOS POR CLIENTE =====");

    for (String[] linha : dados) {

        System.out.println(
                "Cliente: " + linha[0]
                + " | Pedidos: " + linha[1]
                + " | Total: " + linha[2]
                + " | Ticket Medio: " + linha[3]
        );
    }
}

private void mostrarProdutosMaisVendidos() {

    List<String[]> dados =
            repository.relatorioProdutosMaisVendidos();

    System.out.println("\n===== PRODUTOS MAIS VENDIDOS =====");

    for (String[] linha : dados) {

        System.out.println(
                "Produto: " + linha[0]
                + " | Categoria: " + linha[1]
                + " | Quantidade: " + linha[2]
        );
    }
}


}

