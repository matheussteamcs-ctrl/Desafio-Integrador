package ui;

import model.Cliente;
import service.ClienteService;
import exception.ValidacaoException;

import java.util.List;
import java.util.Scanner;

public class ClienteMenu {

    private final ClienteService clienteService;
    private final Scanner scanner;

    public ClienteMenu(ClienteService clienteService, Scanner scanner) {
        this.clienteService = clienteService;
        this.scanner = scanner;
    }

    public void exibir() {
        int opcao = -1;
        while (opcao != 0) {
            System.out.println("\n===== MENU DE CLIENTES =====");
            System.out.println("1 - Cadastrar cliente");
            System.out.println("2 - Listar clientes");
            System.out.println("3 - Buscar cliente por ID");
            System.out.println("4 - Excluir cliente");
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
        System.out.println("\n-- Cadastrar Cliente --");
        System.out.print("Nome: ");
        String nome = scanner.nextLine().trim();

        System.out.print("E-mail: ");
        String email = scanner.nextLine().trim();

        
        Cliente cliente = new Cliente(0, nome, email);

        try {
            clienteService.salvar(cliente);
            System.out.println("Cliente cadastrado com sucesso!");
        } catch (ValidacaoException e) {
            System.out.println("Erro de validacao: " + e.getMessage());
        }
    }

    private void listar() {
        System.out.println("\n-- Lista de Clientes --");
        List<Cliente> clientes = clienteService.listarTodos();

        if (clientes.isEmpty()) {
            System.out.println("Nenhum cliente cadastrado.");
            return;
        }

        System.out.printf("%-6s %-30s %-40s%n", "ID", "Nome", "E-mail");
        System.out.println("-".repeat(78));
        for (Cliente c : clientes) {
            System.out.printf("%-6d %-30s %-40s%n", c.getId(), c.getNome(), c.getEmail());
        }
    }

    private void buscarPorId() {
        System.out.print("\nDigite o ID do cliente: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            Cliente cliente = clienteService.buscarPorId(id);

            if (cliente == null) {
                System.out.println("Cliente nao encontrado.");
            } else {
                System.out.printf("ID: %d | Nome: %s | E-mail: %s%n",
                        cliente.getId(), cliente.getNome(), cliente.getEmail());
            }
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
        }
    }

    private void excluir() {
        System.out.print("\nDigite o ID do cliente a excluir: ");
        try {
            int id = Integer.parseInt(scanner.nextLine().trim());
            clienteService.excluir(id);
            System.out.println("Cliente excluido com sucesso!");
        } catch (NumberFormatException e) {
            System.out.println("ID invalido.");
        } catch (RuntimeException e) {
            System.out.println("Erro ao excluir: " + e.getMessage());
        }
    }
}
