import java.util.Scanner;

public class Ejercicio5 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        int[] dni = new int[5];
        int[] servicio = new int[5];
        double[] monto = new double[5];

        for (int i = 0; i < 5; i++) {

            System.out.println("\nCliente " + (i + 1));

            System.out.print("Ingrese el DNI: ");
            dni[i] = teclado.nextInt();

            System.out.println("1 - Internet 30 megas ($750)");
            System.out.println("2 - Internet 50 megas ($1100)");
            System.out.println("3 - Internet 100 megas ($1500 con 5% de descuento)");

            System.out.print("Ingrese el tipo de servicio: ");
            servicio[i] = teclado.nextInt();

            if (servicio[i] == 1) {
                monto[i] = 750;

            } else if (servicio[i] == 2) {
                monto[i] = 1100;

            } else if (servicio[i] == 3) {
                monto[i] = 1500 - (1500 * 0.05);

            } else {
                monto[i] = 0;
                System.out.println("Servicio incorrecto.");
            }
        }

        System.out.println("\n===== RESULTADOS =====");

        for (int i = 0; i < 5; i++) {

            System.out.println("\nCliente " + (i + 1));
            System.out.println("DNI: " + dni[i]);
            System.out.println("Servicio: " + servicio[i]);
            System.out.println("Monto a pagar: $" + monto[i]);
        }

        teclado.close();
    }
}