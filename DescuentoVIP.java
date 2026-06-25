public class DescuentoVIP implements EstrategiaDescuento {
    @Override
    public double calcular(double subtotal) {
        return subtotal * 0.20;
    }
}