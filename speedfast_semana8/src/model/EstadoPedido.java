package model;

/**
 * Estados posibles de un pedido. Los nombres (name()) coinciden con los
 * valores del ENUM de la columna pedidos.estado en MySQL.
 */
public enum EstadoPedido {
    PENDIENTE("Pendiente"),
    EN_REPARTO("En reparto"),
    ENTREGADO("Entregado");

    private final String etiqueta;

    EstadoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto legible que se muestra en tablas y combos. */
    @Override
    public String toString() {
        return etiqueta;
    }
}
