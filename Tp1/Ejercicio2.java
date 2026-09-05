import java.util.Scanner;

public class Ejercicio2 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Ingrese la cantidad de notas: ");
        int n = teclado.nextInt();

        double[] notas = new double[n];

        int aprobados = 0;
        int desaprobados = 0;

        for (int i = 0; i < n; i++) {
            System.out.print("Ingrese la nota " + (i + 1) + ": ");
            notas[i] = teclado.nextDouble();
        }

        for (int i = 0; i < n; i++) {

            if (notas[i] >= 6) {
                aprobados++;
            } else {
                desaprobados++;
            }
        }

        System.out.println("\nCantidad de aprobados: " + aprobados);
        System.out.println("Cantidad de desaprobados: " + desaprobados);

        teclado.close();
    }
}