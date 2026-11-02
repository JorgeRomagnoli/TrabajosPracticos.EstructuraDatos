package agenda;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;

// Maneja la lista dinamica de contactos y el archivo txt
public class Agenda {

    private ArrayList<Contacto> contactos = new ArrayList<>();
    private File archivo;

    public Agenda(File archivo) {
        this.archivo = archivo;
    }

    public ArrayList<Contacto> getContactos() {
        return contactos;
    }

    public File getArchivo() {
        return archivo;
    }

    public void agregar(Contacto c) {
        contactos.add(c);
    }

    public void modificar(int indice, Contacto c) {
        contactos.set(indice, c);
    }

    public void eliminar(int indice) {
        contactos.remove(indice);
    }

    public void ordenar() {
        contactos.sort(Comparator.comparing((Contacto c) -> c.getApellido().toLowerCase())
                .thenComparing(c -> c.getNombre().toLowerCase()));
    }

    // Devuelve los contactos cuyo nombre, apellido o telefono contengan el texto
    public ArrayList<Contacto> buscar(String texto) {
        ArrayList<Contacto> resultado = new ArrayList<>();
        String t = texto.toLowerCase();
        for (Contacto c : contactos) {
            if (c.getNombre().toLowerCase().contains(t)
                    || c.getApellido().toLowerCase().contains(t)
                    || c.getTelefono().contains(t)) {
                resultado.add(c);
            }
        }
        return resultado;
    }

    // Genera el txt con todos los registros
    public void guardarTxt() throws IOException {
        try (PrintWriter pw = new PrintWriter(archivo, StandardCharsets.UTF_8)) {
            pw.println("AGENDA - " + contactos.size() + " registro(s)");
            pw.println("========================================");
            for (int i = 0; i < contactos.size(); i++) {
                Contacto c = contactos.get(i);
                pw.println("Registro " + (i + 1));
                pw.println("Nombre    : " + c.getNombre());
                pw.println("Apellido  : " + c.getApellido());
                pw.println("Telefono  : " + c.getTelefono());
                pw.println("Email     : " + c.getEmail());
                pw.println("Direccion : " + c.getDireccion());
                pw.println("----------------------------------------");
            }
        }
    }

    // Lee el txt (si existe) para recuperar los registros al abrir el programa
    public void cargarTxt() throws IOException {
        contactos.clear();
        if (!archivo.exists()) {
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(archivo, StandardCharsets.UTF_8))) {
            String nombre = "", apellido = "", telefono = "", email = "", direccion = "";
            String linea;
            while ((linea = br.readLine()) != null) {
                int dosPuntos = linea.indexOf(':');
                if (linea.startsWith("-----")) {
                    contactos.add(new Contacto(nombre, apellido, telefono, email, direccion));
                    nombre = apellido = telefono = email = direccion = "";
                } else if (dosPuntos > 0) {
                    String clave = linea.substring(0, dosPuntos).trim();
                    String valor = linea.substring(dosPuntos + 1).trim();
                    switch (clave) {
                        case "Nombre": nombre = valor; break;
                        case "Apellido": apellido = valor; break;
                        case "Telefono": telefono = valor; break;
                        case "Email": email = valor; break;
                        case "Direccion": direccion = valor; break;
                    }
                }
            }
        }
    }
}
