/**
 * Interfaz para definir el cálculo de descuentos
 * Aplica el principio Abierto/Cerrado (OCP)
 */
public interface EstrategiaDescuento {
    double calcular(double subtotal);
}