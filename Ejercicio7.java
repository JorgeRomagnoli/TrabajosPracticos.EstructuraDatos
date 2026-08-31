import java.util.Scanner;

public class Ejercicio7 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        int documento;
        int edad;
        char sexo;

        int totalPersonas = 0;
        int varones = 0;
        int mujeres = 0;
        int varonesEntre16y65 = 0;

        int mayorEdad = -1;
        int documentoMayor = 0;
        char sexoMayor = ' ';

        System.out.print("Ingrese número de documento (0 para terminar): ");
        documento = teclado.nextInt();

        while (documento != 0) {

            System.out.print("Ingrese edad: ");
            edad = teclado.nextInt();

            System.out.print("Ingrese sexo (F/M): ");
            sexo = Character.toUpperCase(teclado.next().charAt(0));

            // Contar total de personas
            totalPersonas++;

            // Contar varones y mujeres
            if (sexo == 'M') {

                varones++;

                // Contar varones entre 16 y 65 años
                if (edad >= 16 && edad <= 65) {
                    varonesEntre16y65++;
                }

            } else if (sexo == 'F') {

                mujeres++;
            }

            // Buscar persona de mayor edad
            if (edad > mayorEdad) {
                mayorEdad = edad;
                documentoMayor = documento;
                sexoMayor = sexo;
            }

            System.out.println();

            // Pedir siguiente persona
            System.out.print("Ingrese número de documento (0 para terminar): ");
            documento = teclado.nextInt();
        }

        // Calcular porcentaje de varones entre 16 y 65
        double porcentaje = 0;

        if (varones > 0) {
            porcentaje = (varonesEntre16y65 * 100.0) / varones;
        }

        // Mostrar resultados
        System.out.println("\n===== RESULTADOS DEL CENSO =====");

        System.out.println("Cantidad total de personas censadas: " + totalPersonas);
        System.out.println("Cantidad de varones: " + varones);
        System.out.println("Cantidad de mujeres: " + mujeres);
        System.out.println("Porcentaje de varones entre 16 y 65 años: "
                + porcentaje + "%");

        // Mostrar persona de mayor edad
        if (totalPersonas > 0) {
            System.out.println("\n===== PERSONA DE MAYOR EDAD =====");
            System.out.println("Documento: " + documentoMayor);
            System.out.println("Edad: " + mayorEdad);
            System.out.println("Sexo: " + sexoMayor);
        }

        teclado.close();
    }
}