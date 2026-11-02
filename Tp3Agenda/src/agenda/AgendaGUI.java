package agenda;

import java.awt.BorderLayout;
import java.awt.Desktop;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.table.DefaultTableModel;

public class AgendaGUI extends JFrame {

    private Agenda agenda;

    private JTextField txtNombre = new JTextField(15);
    private JTextField txtApellido = new JTextField(15);
    private JTextField txtTelefono = new JTextField(15);
    private JTextField txtEmail = new JTextField(15);
    private JTextField txtDireccion = new JTextField(15);
    private JTextField txtBuscar = new JTextField(15);

    private DefaultTableModel modelo = new DefaultTableModel(
            new String[]{"Nombre", "Apellido", "Telefono", "Email", "Direccion"}, 0) {
        @Override
        public boolean isCellEditable(int fila, int columna) {
            return false;
        }
    };
    private JTable tabla = new JTable(modelo);

    // Contactos que se ven en la tabla (todos, o el resultado de una busqueda)
    private ArrayList<Contacto> visibles = new ArrayList<>();

    public AgendaGUI() {
        super("Agenda Electronica");
        agenda = new Agenda(new File(carpetaPrograma(), "agenda.txt"));

        try {
            agenda.cargarTxt();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo leer agenda.txt: " + e.getMessage());
        }

        armarVentana();
        mostrar(agenda.getContactos());
    }

    private void armarVentana() {
        // Formulario
        JPanel formulario = new JPanel(new GridLayout(5, 2, 5, 5));
        formulario.setBorder(BorderFactory.createTitledBorder("Datos del contacto"));
        formulario.add(new JLabel("Nombre:"));
        formulario.add(txtNombre);
        formulario.add(new JLabel("Apellido:"));
        formulario.add(txtApellido);
        formulario.add(new JLabel("Telefono:"));
        formulario.add(txtTelefono);
        formulario.add(new JLabel("Email:"));
        formulario.add(txtEmail);
        formulario.add(new JLabel("Direccion:"));
        formulario.add(txtDireccion);

        // Botones
        JButton btnAgregar = new JButton("Agregar");
        JButton btnModificar = new JButton("Modificar");
        JButton btnEliminar = new JButton("Eliminar");
        JButton btnLimpiar = new JButton("Limpiar");
        JButton btnOrdenar = new JButton("Ordenar A-Z");
        JButton btnTxt = new JButton("Generar TXT");

        JPanel botones = new JPanel(new GridLayout(6, 1, 5, 5));
        botones.add(btnAgregar);
        botones.add(btnModificar);
        botones.add(btnEliminar);
        botones.add(btnLimpiar);
        botones.add(btnOrdenar);
        botones.add(btnTxt);

        JPanel arriba = new JPanel(new BorderLayout(10, 10));
        arriba.add(formulario, BorderLayout.CENTER);
        arriba.add(botones, BorderLayout.EAST);

        // Busqueda
        JButton btnBuscar = new JButton("Buscar");
        JButton btnVerTodos = new JButton("Ver todos");
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Buscar (nombre, apellido o telefono):"));
        busqueda.add(txtBuscar);
        busqueda.add(btnBuscar);
        busqueda.add(btnVerTodos);

        JPanel norte = new JPanel(new BorderLayout());
        norte.add(arriba, BorderLayout.CENTER);
        norte.add(busqueda, BorderLayout.SOUTH);

        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        JPanel principal = new JPanel(new BorderLayout(10, 10));
        principal.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        principal.add(norte, BorderLayout.NORTH);
        principal.add(new JScrollPane(tabla), BorderLayout.CENTER);
        setContentPane(principal);

        // Eventos
        btnAgregar.addActionListener(e -> agregar());
        btnModificar.addActionListener(e -> modificar());
        btnEliminar.addActionListener(e -> eliminar());
        btnLimpiar.addActionListener(e -> limpiar());
        btnOrdenar.addActionListener(e -> {
            agenda.ordenar();
            guardar();
            mostrar(agenda.getContactos());
        });
        btnTxt.addActionListener(e -> generarTxt());
        btnBuscar.addActionListener(e -> mostrar(agenda.buscar(txtBuscar.getText().trim())));
        txtBuscar.addActionListener(e -> mostrar(agenda.buscar(txtBuscar.getText().trim())));
        btnVerTodos.addActionListener(e -> {
            txtBuscar.setText("");
            mostrar(agenda.getContactos());
        });
        tabla.getSelectionModel().addListSelectionListener(e -> cargarSeleccionado());

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(850, 550);
        setLocationRelativeTo(null);
    }

    private Contacto leerFormulario() {
        String nombre = txtNombre.getText().trim();
        String telefono = txtTelefono.getText().trim();
        if (nombre.isEmpty() || telefono.isEmpty()) {
            JOptionPane.showMessageDialog(this, "El nombre y el telefono son obligatorios.");
            return null;
        }
        return new Contacto(nombre, txtApellido.getText().trim(), telefono,
                txtEmail.getText().trim(), txtDireccion.getText().trim());
    }

    private void agregar() {
        Contacto c = leerFormulario();
        if (c == null) return;
        agenda.agregar(c);
        guardar();
        mostrar(agenda.getContactos());
        limpiar();
    }

    private void modificar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto de la tabla.");
            return;
        }
        Contacto c = leerFormulario();
        if (c == null) return;
        int indice = agenda.getContactos().indexOf(visibles.get(fila));
        agenda.modificar(indice, c);
        guardar();
        mostrar(agenda.getContactos());
        limpiar();
    }

    private void eliminar() {
        int fila = tabla.getSelectedRow();
        if (fila < 0) {
            JOptionPane.showMessageDialog(this, "Seleccione un contacto de la tabla.");
            return;
        }
        int ok = JOptionPane.showConfirmDialog(this, "Eliminar el contacto seleccionado?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (ok != JOptionPane.YES_OPTION) return;
        int indice = agenda.getContactos().indexOf(visibles.get(fila));
        agenda.eliminar(indice);
        guardar();
        mostrar(agenda.getContactos());
        limpiar();
    }

    private void limpiar() {
        txtNombre.setText("");
        txtApellido.setText("");
        txtTelefono.setText("");
        txtEmail.setText("");
        txtDireccion.setText("");
        tabla.clearSelection();
        txtNombre.requestFocus();
    }

    private void cargarSeleccionado() {
        int fila = tabla.getSelectedRow();
        if (fila < 0 || fila >= visibles.size()) return;
        Contacto c = visibles.get(fila);
        txtNombre.setText(c.getNombre());
        txtApellido.setText(c.getApellido());
        txtTelefono.setText(c.getTelefono());
        txtEmail.setText(c.getEmail());
        txtDireccion.setText(c.getDireccion());
    }

    // Llena la tabla con la lista recibida
    private void mostrar(ArrayList<Contacto> lista) {
        visibles = new ArrayList<>(lista);
        modelo.setRowCount(0);
        for (Contacto c : visibles) {
            modelo.addRow(new Object[]{c.getNombre(), c.getApellido(), c.getTelefono(),
                    c.getEmail(), c.getDireccion()});
        }
    }

    // Se guarda automaticamente despues de cada cambio
    private boolean guardar() {
        try {
            agenda.guardarTxt();
            return true;
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "No se pudo guardar agenda.txt: " + e.getMessage());
            return false;
        }
    }

    private void generarTxt() {
        if (!guardar()) return;
        File f = agenda.getArchivo();
        int abrir = JOptionPane.showConfirmDialog(this,
                "Se genero el archivo con " + agenda.getContactos().size() + " registro(s):\n"
                        + f.getAbsolutePath() + "\n\nDesea abrirlo?",
                "TXT generado", JOptionPane.YES_NO_OPTION);
        if (abrir == JOptionPane.YES_OPTION) {
            try {
                Desktop.getDesktop().open(f);
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "No se pudo abrir el archivo.");
            }
        }
    }

    // Carpeta donde esta el .exe (o el .jar), para que agenda.txt viaje con el programa
    private static File carpetaPrograma() {
        String exe = System.getProperty("jpackage.app-path");
        if (exe != null) {
            return new File(exe).getParentFile();
        }
        try {
            File origen = new File(AgendaGUI.class.getProtectionDomain().getCodeSource().getLocation().toURI());
            return origen.isFile() ? origen.getParentFile() : new File(System.getProperty("user.dir"));
        } catch (Exception e) {
            return new File(System.getProperty("user.dir"));
        }
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {
        }
        SwingUtilities.invokeLater(() -> new AgendaGUI().setVisible(true));
    }
}
