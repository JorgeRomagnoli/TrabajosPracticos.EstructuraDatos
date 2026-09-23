/**
 * Datos del cliente que reservó un asiento.
 */
public class Reserva {

    private final String nombre;
    private final String telefono;

    public Reserva(String nombre, String telefono) {
        this.nombre = nombre;
        this.telefono = telefono;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTelefono() {
        return telefono;
    }
}
