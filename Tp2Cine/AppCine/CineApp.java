import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.Icon;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ItemEvent;
import java.awt.geom.QuadCurve2D;
import java.util.ArrayList;
import java.util.List;

/**
 * Ventana principal del sistema de reserva de asientos.
 *
 * Se divide en cuatro zonas:
 *   - Encabezado (arriba): título de la aplicación.
 *   - Plano de la sala (centro): la pantalla y los 100 asientos.
 *   - Panel lateral (derecha): resumen, formulario de reserva y lista de reservas.
 *   - Barra de estado (abajo): mensajes con el resultado de cada acción.
 */
public class CineApp extends JFrame {

    // ---------- Colores y fuentes ----------
    private static final Color COLOR_FONDO = new Color(0xF4F5F7);
    private static final Color COLOR_ENCABEZADO = new Color(0x1F2937);
    private static final Color COLOR_BORDE = new Color(0xE2E5EA);
    private static final Color COLOR_TEXTO = new Color(0x1F2937);
    private static final Color COLOR_TEXTO_SUAVE = new Color(0x6B7280);
    private static final Color COLOR_LIBRE = new Color(0x22A06B);
    private static final Color COLOR_RESERVADO = new Color(0xD64545);
    private static final Color COLOR_SELECCIONADO = new Color(0x2F6FEB);

    private static final Font FUENTE_TITULO = new Font("Segoe UI", Font.BOLD, 22);
    private static final Font FUENTE_SUBTITULO = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_SECCION = new Font("Segoe UI", Font.BOLD, 15);
    private static final Font FUENTE_NORMAL = new Font("Segoe UI", Font.PLAIN, 13);
    private static final Font FUENTE_ETIQUETA = new Font("Segoe UI", Font.BOLD, 12);
    private static final Font FUENTE_ASIENTO = new Font("Segoe UI", Font.BOLD, 11);
    private static final Font FUENTE_NUMERO_GRANDE = new Font("Segoe UI", Font.BOLD, 28);

    // ---------- Modelo ----------
    private final Sala sala = new Sala();
    private int filaSeleccionada = -1;
    private int asientoSeleccionado = -1;

    // ---------- Componentes de la interfaz ----------
    private final BotonRedondeado[][] botonesAsientos =
            new BotonRedondeado[Sala.FILAS][Sala.ASIENTOS_POR_FILA];

    private final JComboBox<String> comboFila = new JComboBox<>();
    private final JComboBox<String> comboAsiento = new JComboBox<>();
    private final JTextField campoNombre = new JTextField();
    private final JTextField campoTelefono = new JTextField();
    private final BotonRedondeado botonReservar = new BotonRedondeado("Reservar asiento", COLOR_SELECCIONADO);
    private final BotonRedondeado botonEliminar = new BotonRedondeado("Eliminar reserva", COLOR_RESERVADO);

    private final JLabel etiquetaDisponibles = new JLabel();
    private final JLabel etiquetaReservados = new JLabel();
    private final JLabel barraEstado = new JLabel();

    private final DefaultTableModel modeloTabla =
            new DefaultTableModel(new String[] {"Asiento", "Cliente", "Teléfono"}, 0) {
                @Override
                public boolean isCellEditable(int fila, int columna) {
                    return false;
                }
            };
    private final JTable tablaReservas = new JTable(modeloTabla);
    private final List<int[]> asientosDeLaTabla = new ArrayList<>();

    // Evita que los combos y la tabla disparen eventos mientras los actualizamos desde el código.
    private boolean sincronizando = false;

    public CineApp() {
        super("Cine - Reserva de Asientos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(COLOR_FONDO);
        setLayout(new BorderLayout());

        add(crearEncabezado(), BorderLayout.NORTH);
        add(crearPanelSala(), BorderLayout.CENTER);
        add(crearPanelLateral(), BorderLayout.EAST);
        add(crearBarraEstado(), BorderLayout.SOUTH);

        actualizarVista();
        mostrarEstado("Hacé clic en un asiento verde del plano para comenzar una reserva.");

        pack();
        setMinimumSize(getSize());
        setLocationRelativeTo(null);
    }

    // =====================================================================
    //  Construcción de la interfaz
    // =====================================================================

    private JPanel crearEncabezado() {
        JLabel titulo = new JLabel("Reserva de Asientos");
        titulo.setFont(FUENTE_TITULO);
        titulo.setForeground(Color.WHITE);

        JLabel subtitulo = new JLabel("Sala 1  ·  10 filas × 10 asientos  ·  100 butacas");
        subtitulo.setFont(FUENTE_SUBTITULO);
        subtitulo.setForeground(new Color(0xC7CDD6));

        JPanel textos = new JPanel(new GridLayout(2, 1, 0, 2));
        textos.setOpaque(false);
        textos.add(titulo);
        textos.add(subtitulo);

        JPanel contadores = new JPanel(new GridLayout(1, 2, 32, 0));
        contadores.setOpaque(false);
        contadores.add(crearIndicador(etiquetaDisponibles, "Disponibles", new Color(0x4ADE80)));
        contadores.add(crearIndicador(etiquetaReservados, "Reservados", new Color(0xF87171)));

        JPanel encabezado = new JPanel(new BorderLayout());
        encabezado.setBackground(COLOR_ENCABEZADO);
        encabezado.setBorder(new EmptyBorder(14, 24, 14, 24));
        encabezado.add(textos, BorderLayout.WEST);
        encabezado.add(contadores, BorderLayout.EAST);
        return encabezado;
    }

    private JPanel crearIndicador(JLabel numero, String descripcion, Color color) {
        numero.setFont(FUENTE_NUMERO_GRANDE);
        numero.setForeground(color);
        numero.setHorizontalAlignment(SwingConstants.CENTER);

        JLabel texto = new JLabel(descripcion, SwingConstants.CENTER);
        texto.setFont(FUENTE_SUBTITULO);
        texto.setForeground(new Color(0xC7CDD6));

        JPanel indicador = new JPanel(new BorderLayout());
        indicador.setOpaque(false);
        indicador.add(numero, BorderLayout.CENTER);
        indicador.add(texto, BorderLayout.SOUTH);
        return indicador;
    }

    private JPanel crearPanelSala() {
        JPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new BorderLayout(0, 12));
        tarjeta.add(new PanelPantalla(), BorderLayout.NORTH);
        tarjeta.add(crearGrillaAsientos(), BorderLayout.CENTER);
        tarjeta.add(crearLeyenda(), BorderLayout.SOUTH);

        JPanel contenedor = new JPanel(new BorderLayout());
        contenedor.setOpaque(false);
        contenedor.setBorder(new EmptyBorder(20, 20, 20, 10));
        contenedor.add(tarjeta);
        return contenedor;
    }

    private JPanel crearGrillaAsientos() {
        JPanel grilla = new JPanel(new GridBagLayout());
        grilla.setOpaque(false);

        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(3, 3, 3, 3);

        // Fila de números arriba (1 al 10)
        c.gridy = 0;
        for (int asiento = 0; asiento < Sala.ASIENTOS_POR_FILA; asiento++) {
            c.gridx = columnaEnGrilla(asiento);
            grilla.add(crearEtiquetaEje(String.valueOf(asiento + 1)), c);
        }

        // Espacio vacío que funciona como pasillo central
        c.gridx = 6;
        grilla.add(Box.createHorizontalStrut(18), c);

        // Una fila de botones por cada letra (A a J), con la letra a ambos lados
        for (int fila = 0; fila < Sala.FILAS; fila++) {
            c.gridy = fila + 1;

            c.gridx = 0;
            grilla.add(crearEtiquetaEje(Sala.letraFila(fila)), c);

            for (int asiento = 0; asiento < Sala.ASIENTOS_POR_FILA; asiento++) {
                BotonRedondeado boton = new BotonRedondeado(Sala.nombreAsiento(fila, asiento), COLOR_LIBRE);
                boton.setFont(FUENTE_ASIENTO);
                boton.setPreferredSize(new Dimension(48, 38));

                final int f = fila;
                final int a = asiento;
                boton.addActionListener(e -> alHacerClicEnAsiento(f, a));

                botonesAsientos[fila][asiento] = boton;
                c.gridx = columnaEnGrilla(asiento);
                grilla.add(boton, c);
            }

            c.gridx = columnaEnGrilla(Sala.ASIENTOS_POR_FILA - 1) + 1;
            grilla.add(crearEtiquetaEje(Sala.letraFila(fila)), c);
        }
        return grilla;
    }

    /** Columna de la grilla donde va cada asiento: deja libre la columna 6 para el pasillo. */
    private int columnaEnGrilla(int asiento) {
        return asiento < 5 ? asiento + 1 : asiento + 2;
    }

    private JLabel crearEtiquetaEje(String texto) {
        JLabel etiqueta = new JLabel(texto, SwingConstants.CENTER);
        etiqueta.setFont(FUENTE_ETIQUETA);
        etiqueta.setForeground(COLOR_TEXTO_SUAVE);
        etiqueta.setPreferredSize(new Dimension(22, 20));
        return etiqueta;
    }

    private JPanel crearLeyenda() {
        JPanel leyenda = new JPanel(new FlowLayout(FlowLayout.CENTER, 24, 0));
        leyenda.setOpaque(false);
        leyenda.add(crearItemLeyenda(COLOR_LIBRE, "Disponible"));
        leyenda.add(crearItemLeyenda(COLOR_RESERVADO, "Reservado"));
        leyenda.add(crearItemLeyenda(COLOR_SELECCIONADO, "Seleccionado"));
        return leyenda;
    }

    private JLabel crearItemLeyenda(Color color, String texto) {
        JLabel item = new JLabel(texto, new IconoColor(color), SwingConstants.LEFT);
        item.setFont(FUENTE_NORMAL);
        item.setForeground(COLOR_TEXTO);
        item.setIconTextGap(8);
        return item;
    }

    private JPanel crearPanelLateral() {
        JPanel superior = new JPanel();
        superior.setLayout(new BoxLayout(superior, BoxLayout.Y_AXIS));
        superior.setOpaque(false);
        superior.add(crearTarjetaFormulario());
        superior.add(Box.createVerticalStrut(14));

        JPanel lateral = new JPanel(new BorderLayout());
        lateral.setOpaque(false);
        lateral.setBorder(new EmptyBorder(20, 10, 20, 20));
        lateral.setPreferredSize(new Dimension(370, 0));
        lateral.add(superior, BorderLayout.NORTH);
        lateral.add(crearTarjetaReservas(), BorderLayout.CENTER);
        return lateral;
    }

    private JPanel crearTarjetaFormulario() {
        for (int fila = 0; fila < Sala.FILAS; fila++) {
            comboFila.addItem("Fila " + Sala.letraFila(fila));
        }
        for (int asiento = 0; asiento < Sala.ASIENTOS_POR_FILA; asiento++) {
            comboAsiento.addItem("Asiento " + (asiento + 1));
        }
        comboFila.setSelectedIndex(-1);
        comboAsiento.setSelectedIndex(-1);
        comboFila.addItemListener(this::alCambiarUbicacion);
        comboAsiento.addItemListener(this::alCambiarUbicacion);

        for (JComponent campo : new JComponent[] {comboFila, comboAsiento, campoNombre, campoTelefono}) {
            campo.setFont(FUENTE_NORMAL);
        }

        botonReservar.setFont(FUENTE_ETIQUETA);
        botonReservar.setPreferredSize(new Dimension(0, 38));
        botonReservar.addActionListener(e -> reservarAsientoSeleccionado());

        // Presionar Enter en el teléfono equivale a hacer clic en "Reservar asiento"
        campoTelefono.addActionListener(e -> reservarAsientoSeleccionado());

        JPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;
        c.gridx = 0;
        c.gridwidth = 2;

        c.gridy = 0;
        c.insets = new Insets(0, 0, 10, 0);
        tarjeta.add(crearTituloSeccion("Nueva reserva"), c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(crearEtiquetaCampo("Ubicación"), c);

        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 10, 6);
        tarjeta.add(comboFila, c);
        c.gridx = 1;
        c.insets = new Insets(0, 6, 10, 0);
        tarjeta.add(comboAsiento, c);

        c.gridx = 0;
        c.gridwidth = 2;
        c.gridy = 3;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(crearEtiquetaCampo("Nombre del cliente"), c);
        c.gridy = 4;
        c.insets = new Insets(0, 0, 10, 0);
        tarjeta.add(campoNombre, c);

        c.gridy = 5;
        c.insets = new Insets(0, 0, 4, 0);
        tarjeta.add(crearEtiquetaCampo("Teléfono"), c);
        c.gridy = 6;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(campoTelefono, c);

        c.gridy = 7;
        c.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(botonReservar, c);
        return tarjeta;
    }

    private JPanel crearTarjetaReservas() {
        tablaReservas.setFont(FUENTE_NORMAL);
        tablaReservas.setRowHeight(26);
        tablaReservas.setFillsViewportHeight(true);
        tablaReservas.setBackground(Color.WHITE);
        tablaReservas.setShowVerticalLines(false);
        tablaReservas.setGridColor(COLOR_BORDE);
        tablaReservas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaReservas.getTableHeader().setFont(FUENTE_ETIQUETA);
        tablaReservas.getTableHeader().setReorderingAllowed(false);
        tablaReservas.getColumnModel().getColumn(0).setMaxWidth(70);
        tablaReservas.getSelectionModel().addListSelectionListener(e -> {
            if (sincronizando || e.getValueIsAdjusting()) {
                return;
            }
            int filaTabla = tablaReservas.getSelectedRow();
            if (filaTabla >= 0) {
                int[] ubicacion = asientosDeLaTabla.get(filaTabla);
                seleccionarAsiento(ubicacion[0], ubicacion[1]);
            }
        });

        JScrollPane scroll = new JScrollPane(tablaReservas);
        scroll.setBorder(BorderFactory.createLineBorder(COLOR_BORDE));
        scroll.getViewport().setBackground(Color.WHITE);

        botonEliminar.setFont(FUENTE_ETIQUETA);
        botonEliminar.setPreferredSize(new Dimension(0, 38));
        botonEliminar.addActionListener(e -> eliminarReserva(filaSeleccionada, asientoSeleccionado, true));

        JPanel tarjeta = crearTarjeta();
        tarjeta.setLayout(new BorderLayout(0, 10));
        tarjeta.add(crearTituloSeccion("Reservas registradas"), BorderLayout.NORTH);
        tarjeta.add(scroll, BorderLayout.CENTER);
        tarjeta.add(botonEliminar, BorderLayout.SOUTH);
        return tarjeta;
    }

    private JPanel crearBarraEstado() {
        barraEstado.setFont(FUENTE_NORMAL);
        barraEstado.setForeground(COLOR_TEXTO);

        JPanel barra = new JPanel(new BorderLayout());
        barra.setBackground(Color.WHITE);
        barra.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createMatteBorder(1, 0, 0, 0, COLOR_BORDE),
                new EmptyBorder(10, 24, 10, 24)));
        barra.add(barraEstado, BorderLayout.CENTER);
        return barra;
    }

    private JPanel crearTarjeta() {
        JPanel tarjeta = new JPanel();
        tarjeta.setBackground(Color.WHITE);
        tarjeta.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(COLOR_BORDE),
                new EmptyBorder(16, 16, 16, 16)));
        tarjeta.setAlignmentX(Component.LEFT_ALIGNMENT);
        return tarjeta;
    }

    private JLabel crearTituloSeccion(String texto) {
        JLabel titulo = new JLabel(texto);
        titulo.setFont(FUENTE_SECCION);
        titulo.setForeground(COLOR_TEXTO);
        return titulo;
    }

    private JLabel crearEtiquetaCampo(String texto) {
        JLabel etiqueta = new JLabel(texto);
        etiqueta.setFont(FUENTE_ETIQUETA);
        etiqueta.setForeground(COLOR_TEXTO_SUAVE);
        return etiqueta;
    }

    // =====================================================================
    //  Acciones del usuario
    // =====================================================================

    private void alHacerClicEnAsiento(int fila, int asiento) {
        seleccionarAsiento(fila, asiento);

        if (sala.estaReservado(fila, asiento)) {
            mostrarAvisoAsientoReservado(fila, asiento);
        } else {
            campoNombre.requestFocusInWindow();
            mostrarEstado("Asiento " + Sala.nombreAsiento(fila, asiento)
                    + " seleccionado. Completá los datos del cliente y presioná \"Reservar asiento\".");
        }
    }

    private void alCambiarUbicacion(ItemEvent evento) {
        if (sincronizando || evento.getStateChange() != ItemEvent.SELECTED) {
            return;
        }
        if (comboFila.getSelectedIndex() >= 0 && comboAsiento.getSelectedIndex() >= 0) {
            seleccionarAsiento(comboFila.getSelectedIndex(), comboAsiento.getSelectedIndex());
        }
    }

    private void reservarAsientoSeleccionado() {
        if (filaSeleccionada < 0) {
            mostrarError("Seleccioná un asiento en el plano, o elegí la fila y el número de asiento.");
            return;
        }
        if (sala.estaReservado(filaSeleccionada, asientoSeleccionado)) {
            mostrarAvisoAsientoReservado(filaSeleccionada, asientoSeleccionado);
            return;
        }

        String nombre = campoNombre.getText().trim();
        String telefono = campoTelefono.getText().trim();

        if (nombre.isEmpty()) {
            mostrarError("Ingresá el nombre del cliente.");
            campoNombre.requestFocusInWindow();
            return;
        }
        if (!esTelefonoValido(telefono)) {
            mostrarError("Ingresá un teléfono válido (solo números, entre 6 y 15 dígitos).");
            campoTelefono.requestFocusInWindow();
            return;
        }

        String asiento = Sala.nombreAsiento(filaSeleccionada, asientoSeleccionado);
        sala.reservar(filaSeleccionada, asientoSeleccionado, new Reserva(nombre, telefono));

        campoNombre.setText("");
        campoTelefono.setText("");
        seleccionarAsiento(-1, -1);
        mostrarEstado("Asiento " + asiento + " reservado con éxito para " + nombre + ".");
    }

    /** Aviso obligatorio cuando se intenta usar un asiento ocupado: muestra quién lo tiene. */
    private void mostrarAvisoAsientoReservado(int fila, int asiento) {
        Reserva reserva = sala.getReserva(fila, asiento);
        String mensaje = "El asiento " + Sala.nombreAsiento(fila, asiento) + " ya está reservado.\n\n"
                + "Cliente:   " + reserva.getNombre() + "\n"
                + "Teléfono:  " + reserva.getTelefono();

        Object[] opciones = {"Eliminar reserva", "Cerrar"};
        int eleccion = JOptionPane.showOptionDialog(this, mensaje, "Asiento ocupado",
                JOptionPane.DEFAULT_OPTION, JOptionPane.WARNING_MESSAGE, null, opciones, opciones[1]);

        if (eleccion == 0) {
            eliminarReserva(fila, asiento, false);
        } else {
            mostrarEstado("El asiento " + Sala.nombreAsiento(fila, asiento)
                    + " está reservado por " + reserva.getNombre() + ".");
        }
    }

    private void eliminarReserva(int fila, int asiento, boolean pedirConfirmacion) {
        if (fila < 0 || !sala.estaReservado(fila, asiento)) {
            mostrarError("Seleccioná en la lista o en el plano un asiento que esté reservado.");
            return;
        }

        String nombreAsiento = Sala.nombreAsiento(fila, asiento);
        Reserva reserva = sala.getReserva(fila, asiento);

        if (pedirConfirmacion) {
            int respuesta = JOptionPane.showConfirmDialog(this,
                    "¿Eliminar la reserva de " + reserva.getNombre() + " para el asiento " + nombreAsiento + "?\n"
                            + "El asiento quedará disponible para nuevas reservas.",
                    "Confirmar eliminación", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE);
            if (respuesta != JOptionPane.YES_OPTION) {
                return;
            }
        }

        sala.liberar(fila, asiento);
        seleccionarAsiento(-1, -1);
        mostrarEstado("Se eliminó la reserva de " + reserva.getNombre() + ". El asiento "
                + nombreAsiento + " está disponible nuevamente.");
    }

    // =====================================================================
    //  Actualización de la pantalla
    // =====================================================================

    /** Marca un asiento como seleccionado (o ninguno si se pasa -1) y refresca la vista. */
    private void seleccionarAsiento(int fila, int asiento) {
        filaSeleccionada = fila;
        asientoSeleccionado = asiento;

        sincronizando = true;
        comboFila.setSelectedIndex(fila);
        comboAsiento.setSelectedIndex(asiento);
        sincronizando = false;

        actualizarVista();
    }

    /** Vuelve a pintar el plano, los contadores y la tabla según el estado actual de la sala. */
    private void actualizarVista() {
        for (int fila = 0; fila < Sala.FILAS; fila++) {
            for (int asiento = 0; asiento < Sala.ASIENTOS_POR_FILA; asiento++) {
                BotonRedondeado boton = botonesAsientos[fila][asiento];
                boolean reservado = sala.estaReservado(fila, asiento);
                boolean seleccionado = fila == filaSeleccionada && asiento == asientoSeleccionado;

                if (reservado) {
                    boton.setColorFondo(COLOR_RESERVADO);
                    Reserva reserva = sala.getReserva(fila, asiento);
                    boton.setToolTipText("Asiento " + Sala.nombreAsiento(fila, asiento)
                            + " - Reservado por " + reserva.getNombre() + " (Tel: " + reserva.getTelefono() + ")");
                } else {
                    boton.setColorFondo(seleccionado ? COLOR_SELECCIONADO : COLOR_LIBRE);
                    boton.setToolTipText("Asiento " + Sala.nombreAsiento(fila, asiento) + " - Disponible");
                }
                boton.setResaltado(seleccionado);
            }
        }

        etiquetaDisponibles.setText(String.valueOf(sala.cantidadDisponibles()));
        etiquetaReservados.setText(String.valueOf(sala.cantidadReservados()));

        sincronizando = true;
        modeloTabla.setRowCount(0);
        asientosDeLaTabla.clear();
        int filaTablaSeleccionada = -1;
        for (int fila = 0; fila < Sala.FILAS; fila++) {
            for (int asiento = 0; asiento < Sala.ASIENTOS_POR_FILA; asiento++) {
                if (sala.estaReservado(fila, asiento)) {
                    Reserva reserva = sala.getReserva(fila, asiento);
                    modeloTabla.addRow(new Object[] {
                            Sala.nombreAsiento(fila, asiento), reserva.getNombre(), reserva.getTelefono()});
                    asientosDeLaTabla.add(new int[] {fila, asiento});
                    if (fila == filaSeleccionada && asiento == asientoSeleccionado) {
                        filaTablaSeleccionada = asientosDeLaTabla.size() - 1;
                    }
                }
            }
        }
        if (filaTablaSeleccionada >= 0) {
            tablaReservas.setRowSelectionInterval(filaTablaSeleccionada, filaTablaSeleccionada);
        }
        sincronizando = false;

        botonEliminar.setEnabled(filaSeleccionada >= 0 && sala.estaReservado(filaSeleccionada, asientoSeleccionado));
    }

    private void mostrarEstado(String mensaje) {
        barraEstado.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Revisá los datos", JOptionPane.ERROR_MESSAGE);
    }

    private static boolean esTelefonoValido(String telefono) {
        String soloDigitos = telefono.replaceAll("[\\s()+-]", "");
        return soloDigitos.matches("\\d{6,15}");
    }

    // =====================================================================
    //  Componentes de dibujo propio
    // =====================================================================

    /** Dibuja la pantalla del cine como una curva, arriba del plano de asientos. */
    private static class PanelPantalla extends JPanel {

        PanelPantalla() {
            setOpaque(false);
            setPreferredSize(new Dimension(0, 58));
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

            int ancho = getWidth();
            int margen = ancho / 7;

            g2.setColor(new Color(0x9CA3AF));
            g2.setStroke(new BasicStroke(6f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
            g2.draw(new QuadCurve2D.Float(margen, 26, ancho / 2f, 2, ancho - margen, 26));

            String texto = "P A N T A L L A";
            g2.setFont(FUENTE_ETIQUETA);
            g2.setColor(COLOR_TEXTO_SUAVE);
            FontMetrics medidas = g2.getFontMetrics();
            g2.drawString(texto, (ancho - medidas.stringWidth(texto)) / 2, 48);

            g2.dispose();
        }
    }

    /** Cuadradito de color que se usa en la leyenda. */
    private static class IconoColor implements Icon {

        private final Color color;

        IconoColor(Color color) {
            this.color = color;
        }

        @Override
        public void paintIcon(Component componente, Graphics g, int x, int y) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(color);
            g2.fillRoundRect(x, y, getIconWidth(), getIconHeight(), 5, 5);
            g2.dispose();
        }

        @Override
        public int getIconWidth() {
            return 16;
        }

        @Override
        public int getIconHeight() {
            return 16;
        }
    }

    // =====================================================================
    //  Punto de entrada
    // =====================================================================

    public static void main(String[] args) {
        try {
            for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (Exception e) {
            // Si Nimbus no está disponible se usa el aspecto por defecto de Java.
        }

        // Textos de los botones de los diálogos en español, sin importar el idioma del sistema
        UIManager.put("OptionPane.yesButtonText", "Sí");
        UIManager.put("OptionPane.noButtonText", "No");
        UIManager.put("OptionPane.okButtonText", "Aceptar");
        UIManager.put("OptionPane.cancelButtonText", "Cancelar");

        SwingUtilities.invokeLater(() -> new CineApp().setVisible(true));
    }
}
