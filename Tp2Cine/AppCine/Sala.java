/**
 * Modelo de la sala: una matriz de 10 x 10 donde cada posición guarda la
 * reserva de ese asiento, o null si el asiento está libre.
 *
 * Las filas y asientos se manejan con índices desde 0 (fila 0 = "A", asiento 0 = "1").
 */
public class Sala {

    public static final int FILAS = 10;
    public static final int ASIENTOS_POR_FILA = 10;

    private final Reserva[][] asientos = new Reserva[FILAS][ASIENTOS_POR_FILA];

    public boolean estaReservado(int fila, int asiento) {
        return asientos[fila][asiento] != null;
    }

    public Reserva getReserva(int fila, int asiento) {
        return asientos[fila][asiento];
    }

    /** Reserva el asiento. Devuelve false si ya estaba ocupado. */
    public boolean reservar(int fila, int asiento, Reserva reserva) {
        if (estaReservado(fila, asiento)) {
            return false;
        }
        asientos[fila][asiento] = reserva;
        return true;
    }

    /** Libera el asiento y devuelve la reserva eliminada (null si ya estaba libre). */
    public Reserva liberar(int fila, int asiento) {
        Reserva eliminada = asientos[fila][asiento];
        asientos[fila][asiento] = null;
        return eliminada;
    }

    public int cantidadReservados() {
        int cantidad = 0;
        for (int fila = 0; fila < FILAS; fila++) {
            for (int asiento = 0; asiento < ASIENTOS_POR_FILA; asiento++) {
                if (estaReservado(fila, asiento)) {
                    cantidad++;
                }
            }
        }
        return cantidad;
    }

    public int cantidadDisponibles() {
        return FILAS * ASIENTOS_POR_FILA - cantidadReservados();
    }

    /** Convierte índices en el nombre que ve el cliente. Ejemplo: (2, 4) -> "C5". */
    public static String nombreAsiento(int fila, int asiento) {
        return letraFila(fila) + (asiento + 1);
    }

    public static String letraFila(int fila) {
        return String.valueOf((char) ('A' + fila));
    }
}
