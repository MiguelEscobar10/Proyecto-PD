package org.example.screen.components;

import org.example.screen.Theme;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.awt.Cursor;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.function.Consumer;

/** Botones para insertar funciones y botones Calcular / Limpiar. */
public class Buttons extends JPanel {

    private static final String[] ATAJOS = {
            "x^2", "2^x", "sin(x)", "cos(x)", "tan(x)", "sinh(x)", "cosh(x)"
    };

    private final RoundButton botonCalcular = new RoundButton("Calcular", true, false);

    /**
     * @param alInsertar texto de la función que se debe insertar en el campo f(x)
     * @param alCalcular acción del botón Calcular
     * @param alLimpiar  acción del botón Limpiar
     */
    public Buttons(Consumer<String> alInsertar, Runnable alCalcular, Runnable alLimpiar) {
        super(new BorderLayout(0, 10));
        setOpaque(false);

        add(panelAtajos(alInsertar), BorderLayout.NORTH);
        add(panelAcciones(alCalcular, alLimpiar), BorderLayout.SOUTH);
    }

    private JPanel panelAtajos(Consumer<String> alInsertar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 0));
        panel.setOpaque(false);

        JLabel etiqueta = new JLabel("Insertar:");
        etiqueta.setFont(Theme.SMALL);
        etiqueta.setForeground(Theme.MUTED);
        panel.add(etiqueta);

        for (String atajo : ATAJOS) {
            RoundButton boton = new RoundButton(atajo, false, true);
            boton.addActionListener(e -> alInsertar.accept(atajo));
            panel.add(boton);
        }
        return panel;
    }

    private JPanel panelAcciones(Runnable alCalcular, Runnable alLimpiar) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 8, 0));
        panel.setOpaque(false);

        RoundButton limpiar = new RoundButton("Limpiar", false, false);
        limpiar.addActionListener(e -> alLimpiar.run());
        botonCalcular.addActionListener(e -> alCalcular.run());

        panel.add(limpiar);
        panel.add(botonCalcular);
        return panel;
    }

    /** Mientras se calcula, el botón Calcular se bloquea y cambia su texto. */
    public void setCalculando(boolean calculando) {
        botonCalcular.setEnabled(!calculando);
        botonCalcular.setText(calculando ? "Calculando..." : "Calcular");
    }

    /** Botón redondeado que se ve igual en cualquier sistema operativo. */
    private static class RoundButton extends JButton {

        private final boolean principal;
        private boolean encima;

        RoundButton(String texto, boolean principal, boolean compacto) {
            super(texto);
            this.principal = principal;

            setContentAreaFilled(false);
            setFocusPainted(false);
            setOpaque(false);
            setFont(compacto ? Theme.SMALL : Theme.BUTTON);
            setForeground(principal ? java.awt.Color.WHITE : Theme.TEXT);
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            setBorder(compacto
                    ? BorderFactory.createEmptyBorder(5, 10, 5, 10)
                    : BorderFactory.createEmptyBorder(8, 18, 8, 18));

            addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    encima = true;
                    repaint();
                }

                @Override
                public void mouseExited(MouseEvent e) {
                    encima = false;
                    repaint();
                }
            });
        }

        @Override
        protected void paintComponent(Graphics g) {
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            if (!isEnabled()) {
                g2.setColor(Theme.DISABLED);
            } else if (encima || getModel().isArmed()) {
                g2.setColor(principal ? Theme.PRIMARY_HOVER : Theme.SECONDARY_HOVER);
            } else {
                g2.setColor(principal ? Theme.PRIMARY : Theme.SECONDARY);
            }
            g2.fillRoundRect(0, 0, getWidth(), getHeight(), 10, 10);
            g2.dispose();

            super.paintComponent(g);
        }
    }
}
