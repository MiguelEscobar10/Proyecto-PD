package org.example.screen;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

/** Colores, fuentes y estilos compartidos por toda la interfaz. */
public final class Theme {

    // Colores
    public static final Color BACKGROUND = new Color(0xF1F3F9);
    public static final Color SURFACE = Color.WHITE;
    public static final Color PRIMARY = new Color(0x3B5BDB);
    public static final Color PRIMARY_HOVER = new Color(0x364FC7);
    public static final Color SECONDARY = new Color(0xE9ECEF);
    public static final Color SECONDARY_HOVER = new Color(0xDEE2E6);
    public static final Color TEXT = new Color(0x212529);
    public static final Color MUTED = new Color(0x6C757D);
    public static final Color BORDER = new Color(0xCED4DA);
    public static final Color ERROR = new Color(0xC92A2A);
    public static final Color DISABLED = new Color(0x91A7FF);

    // Fuentes
    public static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 22);
    public static final Font SUBTITLE = new Font(Font.SANS_SERIF, Font.PLAIN, 13);
    public static final Font LABEL = new Font(Font.SANS_SERIF, Font.BOLD, 12);
    public static final Font INPUT = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font RESULT = new Font(Font.MONOSPACED, Font.PLAIN, 14);

    private Theme() {
    }

    /** Borde de las tarjetas blancas: línea fina y espacio interior. */
    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(14, 14, 14, 14));
    }

    /** Aplica el estilo común a un campo de texto. */
    public static void styleField(JTextField campo) {
        campo.setFont(INPUT);
        campo.setForeground(TEXT);
        campo.setCaretColor(TEXT);
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(6, 8, 6, 8)));
    }
}
