import java.util.Scanner;

public class Ejercicio4 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        // Cantidad de camiones
        int n = 30;

        // Vectores
        String[] patentes = new String[n];
        String[] choferes = new String[n];
        String[] cargas = new String[n];
        String[] horas = new String[n];

        int cantidadTe = 0;

        // Cargar datos de los 30 camiones
        for (int i = 0; i < n; i++) {

            System.out.println("\n===== CAMION " + (i + 1) + " =====");

            System.out.print("Ingrese patente: ");
            patentes[i] = teclado.nextLine();

            System.out.print("Ingrese nombre y apellido del chofer: ");
            choferes[i] = teclado.nextLine();

            System.out.print("Ingrese tipo de carga (madera, yerba o te): ");
            cargas[i] = teclado.nextLine();

            System.out.print("Ingrese hora de egreso: ");
            horas[i] = teclado.nextLine();

            // Contar camiones que transportan té
            if (cargas[i].equalsIgnoreCase("te") ||
                    cargas[i].equalsIgnoreCase("té")) {

                cantidadTe++;
            }
        }

        // Mostrar todos los datos
        System.out.println("\n===== DATOS DE LOS CAMIONES =====");

        for (int i = 0; i < n; i++) {

            System.out.println("\nCamion " + (i + 1));
            System.out.println("Patente: " + patentes[i]);
            System.out.println("Chofer: " + choferes[i]);
            System.out.println("Carga: " + cargas[i]);
            System.out.println("Hora de egreso: " + horas[i]);
        }

        // Mostrar cantidad de camiones con té
        System.out.println("\n==============================");
        System.out.println("Cantidad de camiones que cargaron te: " + cantidadTe);

        teclado.close();
    }
}