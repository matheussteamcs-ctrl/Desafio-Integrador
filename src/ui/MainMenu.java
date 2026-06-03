package ui;

import repository.ClienteRepository;
import repository.PedidoRepository;
import repository.ProdutoRepository;
import service.ClienteService;
import service.PedidoService;
import service.ProdutoService;
import thread.OrderProcessorThread;

import java.util.Scanner;

public class MainMenu {

    public static void main(String[] args) {

        // --- Inicia a thread de processamento em background (Semana 3) ---
        OrderProcessorThread processador = new OrderProcessorThread();
        Thread threadProcessador = new Thread(processador);
        threadProcessador.setDaemon(true); // daemon: encerra junto com a JVM
        threadProcessador.setName("OrderProcessorThread");
        threadProcessador.start();
        System.out.println("[Sistema] Thread de processamento iniciada em background.");

        // --- Instancia repositorios, services e menus ---
        Scanner scanner = new Scanner(System.in);

        ClienteService clienteService = new ClienteService(new ClienteRepository());
        ProdutoService produtoService = new ProdutoService(new ProdutoRepository());
        PedidoService pedidoService   = new PedidoService(new PedidoRepository());

        ClienteMenu clienteMenu = new ClienteMenu(clienteService, scanner);
        ProdutoMenu produtoMenu = new ProdutoMenu(produtoService, scanner);
        PedidoMenu  pedidoMenu  = new PedidoMenu(pedidoService, produtoService, scanner);

        // --- Loop principal --- roda enquanto a thread daemon trabalha em background
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n=============================");
            System.out.println("  SISTEMA DE GESTAO DE PEDIDOS");
            System.out.println("=============================");
            System.out.println("1 - Clientes");
            System.out.println("2 - Produtos");
            System.out.println("3 - Pedidos");
            System.out.println("0 - Sair");
            System.out.print("Opcao: ");

            try {
                opcao = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Digite um numero valido.");
                continue;
            }

            switch (opcao) {
                case 1 -> clienteMenu.exibir();
                case 2 -> produtoMenu.exibir();
                case 3 -> pedidoMenu.exibir();
                case 0 -> System.out.println("Encerrando o sistema. Ate logo!");
                default -> System.out.println("Opcao invalida.");
            }
        }

        scanner.close();
    }
}
