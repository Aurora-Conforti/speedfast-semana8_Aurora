package model;

/** Pedido de tipo COMIDA. Aplica su propia fórmula de tiempo de entrega. */
public class PedidoComida extends Pedido {

    public PedidoComida(int id, String direccion, double distanciaKm) {
        super(id, direccion, distanciaKm);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.COMIDA;
    }

    @Override
    public double calcularTiempoEntrega() {
        return 15 + (2 * getDistanciaKm());
    }
}
