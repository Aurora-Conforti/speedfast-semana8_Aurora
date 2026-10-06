package model;

/** Pedido de tipo ENCOMIENDA. Aplica su propia fórmula de tiempo de entrega. */
public class PedidoEncomienda extends Pedido {

    public PedidoEncomienda(int id, String direccion, double distanciaKm) {
        super(id, direccion, distanciaKm);
    }

    @Override
    public TipoPedido getTipo() {
        return TipoPedido.ENCOMIENDA;
    }

    @Override
    public double calcularTiempoEntrega() {
        return Math.round(20 + (1.5 * getDistanciaKm()));
    }
}
