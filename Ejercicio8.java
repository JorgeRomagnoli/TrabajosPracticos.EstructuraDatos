import java.util.Scanner;

public class Ejercicio8 {
    public static void main(String[] args) {

        Scanner teclado = new Scanner(System.in);

        final int FILAS = 10;
        final int COLUMNAS = 10;

        boolean[][] reservado = new boolean[FILAS][COLUMNAS];
        String[][] nombreCliente = new String[FILAS][COLUMNAS];
        String[][] telefonoCliente = new String[FILAS][COLUMNAS];

        int opcion;
        int fila;
        int columna;

        do {

            System.out.println("\n===== SISTEMA DE RESERVA DE ASIENTOS - CINE =====");
            System.out.println("1 - Mostrar mapa de la sala");
            System.out.println("2 - Reservar asiento");
            System.out.println("3 - Eliminar reserva");
            System.out.println("4 - Salir");

            System.out.print("Ingrese una opcion: ");
            opcion = teclado.nextInt();

            if (opcion == 1) {

                System.out.println("\n===== MAPA DE LA SALA =====");
                System.out.println("(L = Libre, R = Reservado)\n");

                System.out.print("       ");
                for (columna = 0; columna < COLUMNAS; columna++) {
                    System.out.printf("%3d", columna + 1);
                }
                System.out.println();

                for (fila = 0; fila < FILAS; fila++) {

                    System.out.printf("Fila %2d", fila + 1);

                    for (columna = 0; columna < COLUMNAS; columna++) {
                        System.out.print(reservado[fila][columna] ? "  R" : "  L");
                    }

                    System.out.println();
                }

            } else if (opcion == 2) {

                System.out.print("\nIngrese numero de fila (1-10): ");
                fila = teclado.nextInt();

                System.out.print("Ingrese numero de asiento/columna (1-10): ");
                columna = teclado.nextInt();

                if (fila < 1 || fila > FILAS || columna < 1 || columna > COLUMNAS) {

                    System.out.println("Ubicacion invalida. La fila y la columna deben estar entre 1 y 10.");

                } else {

                    fila--;
                    columna--;

                    if (reservado[fila][columna]) {

                        System.out.println("El asiento ya se encuentra reservado por "
                                + nombreCliente[fila][columna] + " (Tel: "
                                + telefonoCliente[fila][columna] + ").");

                    } else {

                        teclado.nextLine();

                        System.out.print("Ingrese nombre del cliente: ");
                        String nombre = teclado.nextLine();

                        System.out.print("Ingrese telefono del cliente: ");
                        String telefono = teclado.nextLine();

                        reservado[fila][columna] = true;
                        nombreCliente[fila][columna] = nombre;
                        telefonoCliente[fila][columna] = telefono;

                        System.out.println("Asiento fila " + (fila + 1) + ", columna " + (columna + 1)
                                + " reservado con exito para " + nombre + ".");
                    }
                }

            } else if (opcion == 3) {

                System.out.print("\nIngrese numero de fila (1-10): ");
                fila = teclado.nextInt();

                System.out.print("Ingrese numero de asiento/columna (1-10): ");
                columna = teclado.nextInt();

                if (fila < 1 || fila > FILAS || columna < 1 || columna > COLUMNAS) {

                    System.out.println("Ubicacion invalida. La fila y la columna deben estar entre 1 y 10.");

                } else {

                    fila--;
                    columna--;

                    if (!reservado[fila][columna]) {

                        System.out.println("Ese asiento no tiene ninguna reserva.");

                    } else {

                        System.out.println("Se elimino la reserva de " + nombreCliente[fila][columna]
                                + " para el asiento fila " + (fila + 1) + ", columna " + (columna + 1) + ".");

                        reservado[fila][columna] = false;
                        nombreCliente[fila][columna] = null;
                        telefonoCliente[fila][columna] = null;
                    }
                }

            } else if (opcion == 4) {

                System.out.println("Saliendo del sistema...");

            } else {

                System.out.println("Opcion invalida.");
            }

        } while (opcion != 4);

        teclado.close();
    }
}
