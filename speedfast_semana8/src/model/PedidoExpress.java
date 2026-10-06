package model;

/** Pedido de tipo EXPRESS. Aplica su propia fórmula de tiempo de entrega. */
public class PedidoExpress extends Pedido {

    public PedidoExpress(int id, String direccion, double distanciaKm) {
        super(id, direccion, distanciaKm);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.EXPRESS;
    }

    @Override
    public double calcularTiempoEntrega() {
        double tiempo = 10;
        if (getDistanciaKm() > 5) {
            tiempo += 5;
        }
        return tiempo;
    }
}
