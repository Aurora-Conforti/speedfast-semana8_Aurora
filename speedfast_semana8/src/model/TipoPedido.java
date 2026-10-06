package model;

/**
 * Tipos de pedido soportados por SpeedFast. Los nombres (name()) coinciden
 * exactamente con los valores del ENUM de la columna pedidos.tipo en MySQL.
 */
public enum TipoPedido {
    COMIDA("Comida"),
    ENCOMIENDA("Encomienda"),
    EXPRESS("Express");

    private final String etiqueta;

    TipoPedido(String etiqueta) {
        this.etiqueta = etiqueta;
    }

    /** Texto legible que se muestra en tablas y combos. */
    @Override
    public String toString() {
        return etiqueta;
    }
}
