import java.util.Scanner;

public class Ejercicio1 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        System.out.print("Ingrese la cantidad de notas: ");
        int n = teclado.nextInt();

        double[] notas = new double[n];

        for (int i = 0; i < n; i++) {
            System.out.print("Ingrese la nota " + (i + 1) + ": ");
            notas[i] = teclado.nextDouble();
        }

        double notaMasAlta = notas[0];
        double suma = 0;

        for (int i = 0; i < n; i++) {

            suma = suma + notas[i];

            if (notas[i] > notaMasAlta) {
                notaMasAlta = notas[i];
            }
        }

        double promedio = suma / n;

        System.out.println("\n===== RESULTADOS =====");
        System.out.println("Nota más alta: " + notaMasAlta);
        System.out.println("Promedio de notas: " + promedio);

        teclado.close();
    }
}