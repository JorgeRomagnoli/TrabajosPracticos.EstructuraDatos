import javax.swing.JButton;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

/**
 * Botón con esquinas redondeadas y color de fondo propio.
 * Se dibuja a mano para que se vea igual en Windows, Mac y Linux.
 * Se usa tanto para los asientos como para los botones de acción.
 */
public class BotonRedondeado extends JButton {

    private static final Color COLOR_DESHABILITADO = new Color(0xCBD2DA);
    private static final Color COLOR_CONTORNO = new Color(0x111827);

    private Color colorFondo;
    private boolean resaltado;

    public BotonRedondeado(String texto, Color colorFondo) {
        super(texto);
        this.colorFondo = colorFondo;
        setContentAreaFilled(false);
        setBorderPainted(false);
        setFocusPainted(false);
        setOpaque(false);
        setForeground(Color.WHITE);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    public void setColorFondo(Color colorFondo) {
        this.colorFondo = colorFondo;
        repaint();
    }

    /** Dibuja un contorno oscuro alrededor del botón (se usa para el asiento seleccionado). */
    public void setResaltado(boolean resaltado) {
        this.resaltado = resaltado;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);

        int ancho = getWidth();
        int alto = getHeight();

        g2.setColor(colorSegunEstado());
        g2.fillRoundRect(2, 2, ancho - 4, alto - 4, 12, 12);

        if (resaltado) {
            g2.setColor(COLOR_CONTORNO);
            g2.setStroke(new BasicStroke(2.5f));
            g2.drawRoundRect(1, 1, ancho - 3, alto - 3, 12, 12);
        }

        g2.setFont(getFont());
        g2.setColor(getForeground());
        FontMetrics medidas = g2.getFontMetrics();
        int x = (ancho - medidas.stringWidth(getText())) / 2;
        int y = (alto - medidas.getHeight()) / 2 + medidas.getAscent();
        g2.drawString(getText(), x, y);

        g2.dispose();
    }

    private Color colorSegunEstado() {
        if (!isEnabled()) {
            return COLOR_DESHABILITADO;
        }
        if (getModel().isPressed()) {
            return mezclar(colorFondo, Color.BLACK, 0.15);
        }
        if (getModel().isRollover()) {
            return mezclar(colorFondo, Color.WHITE, 0.18);
        }
        return colorFondo;
    }

    private static Color mezclar(Color base, Color otro, double proporcion) {
        int r = (int) (base.getRed() * (1 - proporcion) + otro.getRed() * proporcion);
        int g = (int) (base.getGreen() * (1 - proporcion) + otro.getGreen() * proporcion);
        int b = (int) (base.getBlue() * (1 - proporcion) + otro.getBlue() * proporcion);
        return new Color(r, g, b);
    }
}
