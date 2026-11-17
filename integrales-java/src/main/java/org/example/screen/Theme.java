
package org.example.screen;

import javax.swing.BorderFactory;
import javax.swing.JTextField;
import javax.swing.border.Border;
import java.awt.Color;
import java.awt.Font;

public final class Theme {

    public static final Color BACKGROUND = new Color(0xF1F4FA);
    public static final Color SURFACE = Color.WHITE;

    public static final Color PRIMARY = new Color(0x315DCE);
    public static final Color PRIMARY_HOVER = new Color(0x244DB5);
    public static final Color SECONDARY = new Color(0xE9EDF5);
    public static final Color SECONDARY_HOVER = new Color(0xDCE3F0);

    public static final Color HEADER = new Color(0x172B4D);
    public static final Color HEADER_TEXT = Color.WHITE;
    public static final Color ACCENT = new Color(0x4F7DF3);
    public static final Color RESULT_BACKGROUND = new Color(0xEAF0FF);

    public static final Color TEXT = new Color(0x26364F);
    public static final Color MUTED = new Color(0x68758A);
    public static final Color BORDER = new Color(0xDCE3ED);
    public static final Color ERROR = new Color(0xC92A2A);
    public static final Color DISABLED = new Color(0x91A7FF);

    public static final Font TITLE = new Font(Font.SANS_SERIF, Font.BOLD, 27);
    public static final Font SUBTITLE = new Font(Font.SANS_SERIF, Font.PLAIN, 14);
    public static final Font LABEL = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font INPUT = new Font(Font.SANS_SERIF, Font.PLAIN, 15);
    public static final Font BUTTON = new Font(Font.SANS_SERIF, Font.BOLD, 13);
    public static final Font SMALL = new Font(Font.SANS_SERIF, Font.PLAIN, 12);
    public static final Font RESULT = new Font(Font.MONOSPACED, Font.PLAIN, 14);
    public static final Font RESULT_VALUE = new Font(Font.SANS_SERIF, Font.BOLD, 27);

    private Theme() {
    }

    public static Border cardBorder() {
        return BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(16, 18, 16, 18)
        );
    }

    public static void styleField(JTextField campo) {
        campo.setFont(INPUT);
        campo.setForeground(TEXT);
        campo.setCaretColor(PRIMARY);
        campo.setBackground(new Color(0xF8FAFD));
        campo.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER),
                BorderFactory.createEmptyBorder(9, 11, 9, 11)
        ));
    }
}
