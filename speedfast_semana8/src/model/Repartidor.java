package model;

/** Repartidor de SpeedFast (tabla repartidores). */
public class Repartidor {

    private int id;
    private String nombre;

    public Repartidor(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
    }

    /** Constructor para repartidores nuevos: el id lo genera la base de datos. */
    public Repartidor(String nombre) {
        this(0, nombre);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String toString() {
        return id + " - " + nombre;
    }
}
