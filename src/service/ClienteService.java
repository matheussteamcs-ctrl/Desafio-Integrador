package service;

import exception.ValidacaoException;
import model.Cliente;
import repository.ClienteRepository;

public class ClienteService {

    private final ClienteRepository clienteRepository = new ClienteRepository();

    public void salvar(Cliente cliente) {
        if (cliente.getNome() == null || cliente.getNome().trim().isEmpty()) {
            throw new ValidacaoException("O nome do cliente nao pode ser vazio.");
        }

        if (cliente.getEmail() == null || cliente.getEmail().trim().isEmpty()) {
            throw new ValidacaoException("O e-mail do cliente nao pode ser vazio.");
        }

        if (!emailValido(cliente.getEmail())) {
            throw new ValidacaoException("O e-mail informado e invalido: " + cliente.getEmail());
        }

        clienteRepository.save(cliente);
    }

    private boolean emailValido(String email) {
        return email.matches("^[\\w._%+\\-]+@[\\w.\\-]+\\.[a-zA-Z]{2,}$");
    }
}