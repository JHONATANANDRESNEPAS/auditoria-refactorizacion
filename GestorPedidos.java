import java.util.*;
import java.io.*;
import java.sql.*;

public class GestorPedidos {

    public void procesarPedido(String nombreCliente, String emailCliente,
            List<String> nombresProductos, List<Double> preciosProductos,
            List<Integer> cantidades, String tipoCliente) {

        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            System.out.println("Error: nombre de cliente inválido");
            return;
        }
        if (emailCliente == null || !emailCliente.contains("@")) {
            System.out.println("Error: email inválido");
            return;
        }

        double subtotal = 0;
        for (int i = 0; i < nombresProductos.size(); i++) {
            subtotal += preciosProductos.get(i) * cantidades.get(i);
        }

        double descuento = 0;
        if (tipoCliente.equals("VIP")) {
            descuento = subtotal * 0.20;
        } else if (tipoCliente.equals("FRECUENTE")) {
            descuento = subtotal * 0.10;
        } else if (tipoCliente.equals("REGULAR")) {
            descuento = subtotal * 0.05;
        } else if (tipoCliente.equals("NUEVO")) {
            descuento = 0;
        }

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
        if (nombreCliente == null || nombreCliente.trim().isEmpty()) {
            System.out.println("Error: nombre de cliente inválido");
            return;
        }
        if (emailCliente == null || !emailCliente.contains("@")) {
            System.out.println("Error: email inválido");
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