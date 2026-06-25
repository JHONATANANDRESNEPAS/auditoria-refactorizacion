public class DescuentoFrecuente implements EstrategiaDescuento {
    @Override
    public double calcular(double subtotal) {
        return subtotal * 0.10;
    }
}