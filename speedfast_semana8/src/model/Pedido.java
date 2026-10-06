package model;

/**
 * Clase abstracta que representa un pedido de SpeedFast, reutilizada de
 * las semanas anteriores (herencia y polimorfismo se mantienen).
 *
 * Cambios de esta semana:
 *  - El identificador pasa a ser un int generado por la base de datos
 *    (AUTO_INCREMENT) en lugar de un String ingresado a mano.
 *  - Tipo y estado usan enums que coinciden con los ENUM de MySQL.
 *  - La distancia no existe en la tabla pedidos, así que se sigue usando
 *    DISTANCIA_POR_DEFECTO_KM para el cálculo del tiempo estimado.
 */
public abstract class Pedido {

    public static final double DISTANCIA_POR_DEFECTO_KM = 5.0;

    private int id;
    private String direccion;
    private double distanciaKm;
    private EstadoPedido estado;

    public Pedido(int id, String direccion, double distanciaKm) {
        this.id = id;
        this.direccion = direccion;
        this.distanciaKm = distanciaKm;
        this.estado = EstadoPedido.PENDIENTE;
    }

    /**
     * Fábrica: crea la subclase de Pedido que corresponde al tipo indicado.
     * La usan tanto la interfaz (al registrar/editar) como el DAO (al leer).
     */
    public static Pedido crear(TipoPedido tipo, int id, String direccion, EstadoPedido estado) {
        Pedido pedido;
        switch (tipo) {
            case COMIDA:
                pedido = new PedidoComida(id, direccion, DISTANCIA_POR_DEFECTO_KM);
                break;
            case ENCOMIENDA:
                pedido = new PedidoEncomienda(id, direccion, DISTANCIA_POR_DEFECTO_KM);
                break;
            default:
                pedido = new PedidoExpress(id, direccion, DISTANCIA_POR_DEFECTO_KM);
                break;
        }
        pedido.setEstado(estado);
        return pedido;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public double getDistanciaKm() {
        return distanciaKm;
    }

    public EstadoPedido getEstado() {
        return estado;
    }

    public void setEstado(EstadoPedido estado) {
        this.estado = estado;
    }

    /** Tipo de pedido de cada subclase (se guarda en la columna tipo). */
    public abstract TipoPedido getTipo();

    /** Cada subclase calcula el tiempo estimado de entrega con su propia fórmula. */
    public abstract double calcularTiempoEntrega();

    public void mostrarResumen() {
        System.out.println("Pedido " + id + " (" + getTipo() + ") -> " + direccion
                + " | Estado: " + estado);
    }
}
