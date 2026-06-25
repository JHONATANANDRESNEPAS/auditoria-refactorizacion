import java.util.*;
import java.io.*;
import java.sql.*;

public class GestorPedidos {

    // Método reutilizable para validar cliente
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

    // Método para obtener la estrategia de descuento según el tipo de cliente
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

        // Usamos la estrategia en lugar de múltiples if-else
        EstrategiaDescuento estrategia = obtenerEstrategia(tipoCliente);
        double descuento = estrategia.calcular(subtotal);

        double impuesto = (subtotal - descuento) * 0.12;
        double total = subtotal - descuento + impuesto;

        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement stmt = conexion.createStatement()) {

            String sql = "INSERT INTO pedidos (cliente, total) VALUES ('"
                    + nombreCliente + "', " + total + ")";
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error al guardar el pedido: " + e.getMessage());
        }

        try {
            FileWriter writer = new FileWriter("factura_" + nombreCliente + ".txt");
            writer.write("FACTURA\n");
            writer.write("Cliente: " + nombreCliente + "\n");
            for (int i = 0; i < nombresProductos.size(); i++) {
                writer.write(nombresProductos.get(i) + " x " + cantidades.get(i)
                        + " = $" + (preciosProductos.get(i) * cantidades.get(i)) + "\n");
            }
            writer.write("Subtotal: $" + subtotal + "\n");
            writer.write("Descuento: $" + descuento + "\n");
            writer.write("Impuesto: $" + impuesto + "\n");
            writer.write("Total a pagar: $" + total + "\n");
            writer.close();
        } catch (IOException e) {
            System.out.println("Error al generar la factura: " + e.getMessage());
        }

        System.out.println("Enviando correo a " + emailCliente + "...");
        System.out.println("Asunto: Confirmación de pedido");
        System.out.println("Cuerpo: Estimado " + nombreCliente + ", su pedido por $"
                + total + " ha sido procesado.");

        System.out.println("[LOG] Pedido procesado para " + nombreCliente + " - Total: " + total);
    }

    public void cancelarPedido(String nombreCliente, String emailCliente, int idPedido) {
        if (!esClienteValido(nombreCliente, emailCliente)) {
            return;
        }

        try (Connection conexion = ConexionBD.obtenerConexion();
             Statement stmt = conexion.createStatement()) {

            String sql = "DELETE FROM pedidos WHERE id = " + idPedido;
            stmt.executeUpdate(sql);
        } catch (SQLException e) {
            System.out.println("Error al cancelar el pedido: " + e.getMessage());
        }

        System.out.println("Enviando correo a " + emailCliente + "...");
        System.out.println("Asunto: Cancelación de pedido");
        System.out.println("Cuerpo: Estimado " + nombreCliente + ", su pedido nº "
                + idPedido + " ha sido cancelado.");
    }
}