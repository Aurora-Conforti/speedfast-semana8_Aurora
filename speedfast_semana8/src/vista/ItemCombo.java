package vista;

import javax.swing.JComboBox;

/**
 * Elemento de un JComboBox que muestra un texto legible (por ejemplo
 * "3 - Av. Picarte 123") pero conserva internamente el id de la entidad.
 * Un id null representa la opción "(Todos)" de los filtros.
 */
public class ItemCombo {

    private final Integer id;
    private final String texto;

    public ItemCombo(Integer id, String texto) {
        this.id = id;
        this.texto = texto;
    }

    public Integer getId() {
        return id;
    }

    /** Lo que ve el usuario en el combo. */
    @Override
    public String toString() {
        return texto;
    }

    /** Retorna el id del elemento seleccionado, o null si no hay selección o es "(Todos)". */
    public static Integer idSeleccionado(JComboBox<ItemCombo> combo) {
        ItemCombo item = (ItemCombo) combo.getSelectedItem();
        return item == null ? null : item.getId();
    }

    /** Selecciona en el combo el elemento con ese id (si existe). */
    public static void seleccionar(JComboBox<ItemCombo> combo, Integer id) {
        if (id == null) {
            return;
        }
        for (int i = 0; i < combo.getItemCount(); i++) {
            if (id.equals(combo.getItemAt(i).getId())) {
                combo.setSelectedIndex(i);
                return;
            }
        }
    }
}
