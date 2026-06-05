package thread;

import util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Thread de processamento assincrono de pedidos.
 * Responsabilidade principal: Gustavo.
 * Este arquivo e necessario para que o MainMenu (Matheus) compile e rode.
 *
 * Ciclo de vida do status:
 *   FILA -> PROCESSANDO -> FINALIZADO
 *
 * A thread abre e fecha uma Connection propria a cada ciclo (nunca compartilha
 * conexao com o menu). O UPDATE atomico garante que dois ciclos concorrentes
 * nao peguem o mesmo pedido.
 */
public class OrderProcessorThread implements Runnable {

    // Intervalo entre cada ciclo de busca (em milissegundos)
    private static final long INTERVALO_CICLO_MS = 3000;

    // Tempo simulando processamento do pedido
    private static final long TEMPO_PROCESSAMENTO_MS = 2000;

    @Override
    public void run() {
        System.out.println("[OrderProcessorThread] Iniciada. Aguardando pedidos em FILA...");

        while (true) {
            try {
                processarProximoPedido();
                Thread.sleep(INTERVALO_CICLO_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[OrderProcessorThread] Interrompida.");
                break;
            }
        }
    }

    /**
     * Tenta pegar UM pedido em status FILA e processá-lo.
     * Abre e fecha sua propria Connection — isolado do menu.
     */
    private void processarProximoPedido() {
        // UPDATE atomico: garante que apenas esta thread "pegou" o pedido
        String sqlPegar = "UPDATE pedido SET status = 'PROCESSANDO' " +
                          "WHERE id = (SELECT id FROM (SELECT id FROM pedido WHERE status = 'FILA' LIMIT 1) AS sub) " +
                          "AND status = 'FILA'";

        String sqlBuscarProcessando = "SELECT id FROM pedido WHERE status = 'PROCESSANDO' LIMIT 1";

        String sqlFinalizar = "UPDATE pedido SET status = 'FINALIZADO' WHERE id = ?";

        // A cada ciclo: abre UMA Connection propria, processa, fecha.
        try (Connection conn = ConnectionFactory.getConnection()) {

            // Tenta marcar um pedido como PROCESSANDO de forma atomica
            int linhasAfetadas;
            try (PreparedStatement ps = conn.prepareStatement(sqlPegar)) {
                linhasAfetadas = ps.executeUpdate();
            }

            // Se 0 linhas: nao havia pedido em FILA ou outro ciclo ja pegou — ignora
            if (linhasAfetadas == 0) {
                return;
            }

            // Descobre o ID do pedido que acabamos de marcar como PROCESSANDO
            int pedidoId = -1;
            try (PreparedStatement ps = conn.prepareStatement(sqlBuscarProcessando);
                 ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidoId = rs.getInt("id");
                }
            }

            if (pedidoId == -1) {
                return; // seguranca: nao encontrou (cenario raro de concorrencia)
            }

            System.out.printf("[OrderProcessorThread] Processando pedido #%d...%n", pedidoId);

            // Simula tempo de processamento (ex: integracao com pagamento, logistica)
            Thread.sleep(TEMPO_PROCESSAMENTO_MS);

            // Finaliza o pedido
            try (PreparedStatement ps = conn.prepareStatement(sqlFinalizar)) {
                ps.setInt(1, pedidoId);
                ps.executeUpdate();
            }

            System.out.printf("[OrderProcessorThread] Pedido #%d FINALIZADO.%n", pedidoId);

        } catch (SQLException e) {
            System.out.println("[OrderProcessorThread] Erro SQL: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
