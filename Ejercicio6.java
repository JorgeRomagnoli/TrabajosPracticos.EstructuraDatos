import java.util.Scanner;
import java.util.Locale;

public class Ejercicio6 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        // Permite ingresar decimales con punto
        teclado.useLocale(Locale.US);

        int numeroVehiculo;
        double tiempo;

        int vehiculoGanador = 0;
        double menorTiempo = 0;

        // Cargar los 12 competidores
        for (int i = 0; i < 12; i++) {

            System.out.println("\nCompetidor " + (i + 1));

            System.out.print("Ingrese numero de vehiculo: ");
            numeroVehiculo = teclado.nextInt();

            System.out.print("Ingrese tiempo en segundos: ");
            tiempo = teclado.nextDouble();

            // Primer competidor
            if (i == 0) {
                menorTiempo = tiempo;
                vehiculoGanador = numeroVehiculo;
            }

            // Buscar el menor tiempo
            if (tiempo < menorTiempo) {
                menorTiempo = tiempo;
                vehiculoGanador = numeroVehiculo;
            }
        }

        // Mostrar ganador
        System.out.println("\n===== MEJOR TIEMPO =====");
        System.out.println("Numero de vehiculo: " + vehiculoGanador);
        System.out.println("Tiempo: " + menorTiempo + " segundos");

        teclado.close();
    }
}