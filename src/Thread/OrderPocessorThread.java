package src.thread;

import src.util.ConnectionFactory;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class OrderProcessorThread implements Runnable {

    private static final int INTERVALO_CICLO_MS = 3000;
    private static final int TEMPO_PROCESSAMENTO_MS = 5000;

    @Override
    public void run() {
        System.out.println("[OrderProcessor] Thread iniciada.");

        while (true) {
            try {
                processarProximoPedido();
                Thread.sleep(INTERVALO_CICLO_MS);

            } catch (InterruptedException e) {
                System.out.println("[OrderProcessor] Thread interrompida.");
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void processarProximoPedido() {

        try (Connection conn = ConnectionFactory.getConnection()) {

            String sqlBusca = "SELECT id FROM pedido WHERE status = 'FILA' LIMIT 1";
            int pedidoId = -1;

            try (PreparedStatement stmt = conn.prepareStatement(sqlBusca);
                 ResultSet rs = stmt.executeQuery()) {

                if (rs.next()) {
                    pedidoId = rs.getInt("id");
                }
            }

            if (pedidoId == -1) return; 

            String sqlUpdate = "UPDATE pedido SET status = 'PROCESSANDO' WHERE id = ? AND status = 'FILA'";
            int linhasAfetadas;

            try (PreparedStatement stmt = conn.prepareStatement(sqlUpdate)) {
                stmt.setInt(1, pedidoId);
                linhasAfetadas = stmt.executeUpdate();
            }

            if (linhasAfetadas == 0) {
                System.out.println("[OrderProcessor] Pedido " + pedidoId + " já foi pego por outro ciclo.");
                return;
            }

            System.out.println("[OrderProcessor] Processando pedido " + pedidoId + "...");

            Thread.sleep(TEMPO_PROCESSAMENTO_MS);

            String sqlFinalizar = "UPDATE pedido SET status = 'FINALIZADO' WHERE id = ?";
            try (PreparedStatement stmt = conn.prepareStatement(sqlFinalizar)) {
                stmt.setInt(1, pedidoId);
                stmt.executeUpdate();
            }

            System.out.println("[OrderProcessor] Pedido " + pedidoId + " FINALIZADO.");

        } catch (SQLException e) {
            System.out.println("[OrderProcessor] Erro ao processar pedido: " + e.getMessage());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}