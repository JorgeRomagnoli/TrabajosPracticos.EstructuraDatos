import java.util.Scanner;
import java.util.Locale;

public class Ejercicio6 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        teclado.useLocale(Locale.US);

        int numeroVehiculo;
        double tiempo;

        int vehiculoGanador = 0;
        double menorTiempo = 0;

        for (int i = 0; i < 12; i++) {

            System.out.println("\nCompetidor " + (i + 1));

            System.out.print("Ingrese numero de vehiculo: ");
            numeroVehiculo = teclado.nextInt();

            System.out.print("Ingrese tiempo en segundos: ");
            tiempo = teclado.nextDouble();

            if (i == 0) {
                menorTiempo = tiempo;
                vehiculoGanador = numeroVehiculo;
            }

            if (tiempo < menorTiempo) {
                menorTiempo = tiempo;
                vehiculoGanador = numeroVehiculo;
            }
        }

        System.out.println("\n===== MEJOR TIEMPO =====");
        System.out.println("Numero de vehiculo: " + vehiculoGanador);
        System.out.println("Tiempo: " + menorTiempo + " segundos");

        teclado.close();
    }
}