import java.util.*;
import java.io.*;
import java.sql.*;

public class GestorPedidos {

    // Método reutilizable para validar datos del cliente
    private boolean esClienteValido(String nombre, String email) {
        if (nombre == null || nombre.trim().isEmpty()) {
            System.out.println("Error: nombre de cliente inválido");
            return false;
        }
        if (email == null || !email.contains("@")) {
            System.out.println("Error: email inválido");
            return false;
        }
        return true;
    }

    // Obtener estrategia de descuento según tipo de cliente
    private EstrategiaDescuento obtenerEstrategia(String tipoCliente) {
        return switch (tipoCliente.toUpperCase()) {
            case "VIP" -> new DescuentoVIP();
            case "FRECUENTE" -> new DescuentoFrecuente();
            case "REGULAR" -> new DescuentoRegular();
            case "NUEVO" -> new DescuentoNuevo();
            default -> new DescuentoNuevo();
        };
    }

    public void procesarPedido(String nombreCliente, String emailCliente,
            List<String> nombresProductos, List<Double> preciosProductos,
            List<Integer> cantidades, String tipoCliente) {

        if (!esClienteValido(nombreCliente, emailCliente)) {
            return;
        }

        double subtotal = 0;
        for (int i = 0; i < nombresProductos.size(); i++) {
            subtotal += preciosProductos.get(i) * cantidades.get(i);
        }

        double descuento = obtenerEstrategia(tipoCliente).calcular(subtotal);
        double impuesto = (subtotal - descuento) * 0.12;
        double total = subtotal - descuento + impuesto;

        // Usamos PreparedStatement para evitar inyección SQL
        String sqlInsert = "INSERT INTO pedidos (cliente, total) VALUES (?, ?)";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sqlInsert)) {

            pstmt.setString(1, nombreCliente);
            pstmt.setDouble(2, total);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error al guardar el pedido: " + e.getMessage());
        }

        // Generación de factura
        try {
            FileWriter writer = new FileWriter("factura_" + nombreCliente.replaceAll("\\s+", "_") + ".txt");
            writer.write("FACTURA\n");
            writer.write("Cliente: " + nombreCliente + "\n");
            for (int i = 0; i < nombresProductos.size(); i++) {
                writer.write(nombresProductos.get(i) + " x " + cantidades.get(i)
                        + " = $" + String.format("%.2f", preciosProductos.get(i) * cantidades.get(i)) + "\n");
            }
            writer.write("-------------------------\n");
            writer.write("Subtotal: $" + String.format("%.2f", subtotal) + "\n");
            writer.write("Descuento: $" + String.format("%.2f", descuento) + "\n");
            writer.write("Impuesto (12%): $" + String.format("%.2f", impuesto) + "\n");
            writer.write("Total a pagar: $" + String.format("%.2f", total) + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error al generar la factura: " + e.getMessage());
        }

        // Notificación por correo
        System.out.println("Enviando correo a " + emailCliente + "...");
        System.out.println("Asunto: Confirmación de pedido");
        System.out.println("Cuerpo: Estimado " + nombreCliente + ", su pedido por $"
                + String.format("%.2f", total) + " ha sido procesado exitosamente.");

        System.out.println("[LOG] Pedido procesado para " + nombreCliente + " - Total: $" + String.format("%.2f", total));
    }

    public void cancelarPedido(String nombreCliente, String emailCliente, int idPedido) {
        if (!esClienteValido(nombreCliente, emailCliente)) {
            return;
        }

        // Consulta segura con PreparedStatement
        String sqlDelete = "DELETE FROM pedidos WHERE id = ?";
        try (Connection conexion = ConexionBD.obtenerConexion();
             PreparedStatement pstmt = conexion.prepareStatement(sqlDelete)) {

            pstmt.setInt(1, idPedido);
            int filasAfectadas = pstmt.executeUpdate();

            if (filasAfectadas == 0) {
                System.out.println("Aviso: No se encontró el pedido con ID " + idPedido);
                return;
            }

        } catch (SQLException e) {
            System.out.println("Error al cancelar el pedido: " + e.getMessage());
            return;
        }

        System.out.println("Enviando correo a " + emailCliente + "...");
        System.out.println("Asunto: Cancelación de pedido");
        System.out.println("Cuerpo: Estimado " + nombreCliente + ", su pedido nº "
                + idPedido + " ha sido cancelado correctamente.");
    }
}