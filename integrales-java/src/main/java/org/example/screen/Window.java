
package org.example.screen;

import org.example.Connection;
import org.example.screen.components.Buttons;
import org.example.screen.components.PanelResult;
import org.example.screen.components.TextField;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingWorker;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;

public class Window extends JFrame {

    private final Connection conexion;

    private final TextField funcion = new TextField("Función f(x)", "x^2 + 4", 30);
    private final TextField limiteA = new TextField("Límite inferior (a)", "0", 10);
    private final TextField limiteB = new TextField("Límite superior (b)", "3", 10);
    private final PanelResult resultado = new PanelResult();
    private final Buttons botones;

    public Window(Connection conexion) {
        super("Integrales definidas");
        this.conexion = conexion;
        this.botones = new Buttons(funcion::insertText, this::calcular, this::limpiar);

        construir();
    }

    private void construir() {
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JPanel raiz = new JPanel(new BorderLayout(0, 18));
        raiz.setBackground(Theme.BACKGROUND);
        raiz.setBorder(BorderFactory.createEmptyBorder(22, 24, 24, 24));

        JPanel arriba = new JPanel(new BorderLayout(0, 16));
        arriba.setOpaque(false);
        arriba.add(encabezado(), BorderLayout.NORTH);
        arriba.add(formulario(), BorderLayout.CENTER);

        raiz.add(arriba, BorderLayout.NORTH);
        raiz.add(resultado, BorderLayout.CENTER);

        setContentPane(raiz);

        funcion.onEnter(this::calcular);
        limiteA.onEnter(this::calcular);
        limiteB.onEnter(this::calcular);

        setSize(900, 850);
        setMinimumSize(new Dimension(750, 700));
        setLocationRelativeTo(null);
    }

    private JPanel encabezado() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBackground(Theme.HEADER);
        panel.setBorder(BorderFactory.createEmptyBorder(20, 22, 20, 22));

        JLabel etiqueta = new JLabel("CALCULADORA MATEMÁTICA");
        etiqueta.setFont(Theme.SMALL);
        etiqueta.setForeground(new java.awt.Color(0xAFC5F5));

        JLabel titulo = new JLabel("Integrales definidas");
        titulo.setFont(Theme.TITLE);
        titulo.setForeground(Theme.HEADER_TEXT);

        JLabel subtitulo = new JLabel(
                "Calcula el área bajo la curva entre dos límites."
        );
        subtitulo.setFont(Theme.SUBTITLE);
        subtitulo.setForeground(new java.awt.Color(0xDCE6F5));

        panel.add(etiqueta);
        panel.add(Box.createVerticalStrut(5));
        panel.add(titulo);
        panel.add(Box.createVerticalStrut(4));
        panel.add(subtitulo);

        return panel;
    }

    private JPanel formulario() {
        JPanel tarjeta = new JPanel(new GridBagLayout());
        tarjeta.setBackground(Theme.SURFACE);
        tarjeta.setBorder(Theme.cardBorder());

        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.weightx = 1;

        JLabel titulo = new JLabel("01  —  Define tu integral");
        titulo.setFont(new java.awt.Font(
                java.awt.Font.SANS_SERIF,
                java.awt.Font.BOLD,
                15
        ));
        titulo.setForeground(Theme.TEXT);

        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 16, 0);
        tarjeta.add(titulo, c);

        c.gridy = 1;
        c.insets = new Insets(0, 0, 12, 0);
        tarjeta.add(funcion, c);

        c.gridy = 2;
        c.gridwidth = 1;
        c.insets = new Insets(0, 0, 12, 8);
        tarjeta.add(limiteA, c);

        c.gridx = 1;
        c.insets = new Insets(0, 8, 12, 0);
        tarjeta.add(limiteB, c);

        JLabel ayuda = new JLabel(
                "Usa * para indicar multiplicación (3*x). "
                        + "Los límites pueden ser números o expresiones como pi/2."
        );
        ayuda.setFont(Theme.SMALL);
        ayuda.setForeground(Theme.MUTED);

        c.gridx = 0;
        c.gridy = 3;
        c.gridwidth = 2;
        c.insets = new Insets(0, 0, 14, 0);
        tarjeta.add(ayuda, c);

        c.gridy = 4;
        c.insets = new Insets(0, 0, 0, 0);
        tarjeta.add(botones, c);

        return tarjeta;
    }

    private void limpiar() {
        funcion.setText("");
        limiteA.setText("");
        limiteB.setText("");
        resultado.mostrarInicio();
        funcion.focus();
    }

    private void calcular() {
        final String f = funcion.getText();
        final String a = limiteA.getText();
        final String b = limiteB.getText();

        if (f.isEmpty() || a.isEmpty() || b.isEmpty()) {
            resultado.mostrarError("Completa la función y los dos límites.");
            return;
        }

        botones.setCalculando(true);
        resultado.mostrarCalculando();

        new SwingWorker<Connection.Resultado, Void>() {

            @Override
            protected Connection.Resultado doInBackground() throws Exception {
                return conexion.definida(f, a, b);
            }

            @Override
            protected void done() {
                botones.setCalculando(false);

                try {
                    resultado.mostrarResultado(f, a, b, get());
                } catch (Exception e) {
                    Throwable causa = e.getCause() != null ? e.getCause() : e;
                    resultado.mostrarError(causa.getMessage());
                }
            }
        }.execute();
    }
}
