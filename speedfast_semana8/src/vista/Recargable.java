package vista;

/**
 * Los paneles que muestran datos de la base implementan esta interfaz para
 * que la ventana principal pueda refrescar tablas y combos cada vez que el
 * usuario cambia de pestaña (así los datos creados en una pestaña aparecen
 * en las demás).
 */
public interface Recargable {
    void recargar();
}
