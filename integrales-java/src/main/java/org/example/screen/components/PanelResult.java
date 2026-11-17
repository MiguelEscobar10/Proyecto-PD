
package org.example.screen.components;

import org.example.Connection;
import org.example.screen.Theme;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.util.Locale;

public class PanelResult extends JPanel {

    private static final String TEXTO_INICIAL =
            "Realiza un cálculo para visualizar el procedimiento.";

    private final JTextArea area = new JTextArea(8, 44);
    private final JLabel valorArea = new JLabel("--");
    private final JPanel resumen = new JPanel(new BorderLayout(0, 4));

    public PanelResult() {
        super(new BorderLayout(0, 12));

        setBackground(Theme.SURFACE);
        setBorder(Theme.cardBorder());
        setMinimumSize(new Dimension(0, 220));

        JLabel titulo = new JLabel("02  —  Resultado del cálculo");
        titulo.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 15));
        titulo.setForeground(Theme.TEXT);

        area.setEditable(false);
        area.setFont(Theme.RESULT);
        area.setForeground(Theme.MUTED);
        area.setBackground(new Color(0xF8FAFD));
        area.setMargin(new Insets(14, 14, 14, 14));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        JScrollPane scroll = new JScrollPane(area);
        scroll.setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        scroll.getViewport().setBackground(new Color(0xF8FAFD));
        scroll.setPreferredSize(new Dimension(400, 170));

        JLabel etiquetaArea = new JLabel("ÁREA CALCULADA");
        etiquetaArea.setFont(Theme.SMALL);
        etiquetaArea.setForeground(Theme.PRIMARY);

        valorArea.setFont(new Font(Font.SANS_SERIF, Font.BOLD, 23));
        valorArea.setForeground(Theme.PRIMARY);

        JLabel formula = new JLabel("F(b) - F(a)");
        formula.setFont(Theme.SMALL);
        formula.setForeground(Theme.MUTED);

        resumen.setBackground(Theme.RESULT_BACKGROUND);
        resumen.setBorder(BorderFactory.createEmptyBorder(8, 14, 8, 14));
        resumen.setPreferredSize(new Dimension(200, 82));

        resumen.add(etiquetaArea, BorderLayout.NORTH);
        resumen.add(valorArea, BorderLayout.CENTER);
        resumen.add(formula, BorderLayout.SOUTH);

        add(titulo, BorderLayout.NORTH);
        add(scroll, BorderLayout.CENTER);
        add(resumen, BorderLayout.SOUTH);

        mostrarInicio();
    }

    public void mostrarInicio() {
        valorArea.setText("--");
        resumen.setVisible(false);
        mostrar(TEXTO_INICIAL, Theme.MUTED);
        revalidate();
        repaint();
    }

    public void mostrarCalculando() {
        valorArea.setText("...");
        resumen.setVisible(false);
        mostrar("Calculando integral...", Theme.MUTED);
        revalidate();
        repaint();
    }

    public void mostrarError(String mensaje) {
        resumen.setVisible(false);
        mostrar(mensaje, Theme.ERROR);
        revalidate();
        repaint();
    }

    public void mostrarResultado(
            String funcion,
            String a,
            String b,
            Connection.Resultado r
    ) {
        StringBuilder texto = new StringBuilder();

        texto.append("INTEGRAL DEFINIDA\n");
        texto.append("f(x) = ").append(funcion).append("\n");
        texto.append("Intervalo: [").append(a).append(", ").append(b).append("]\n\n");

        texto.append("PRIMITIVA\n");
        texto.append("F(x) = ")
                .append(limpiarPrimitiva(r.primitiva())).append("\n\n");

        texto.append("EVALUACIÓN DE LOS LÍMITES\n");
        texto.append("F(b) = F(").append(b).append(") = ")
                .append(formatear(r.valorB())).append("\n");

        texto.append("F(a) = F(").append(a).append(") = ")
                .append(formatear(r.valorA())).append("\n\n");

        texto.append("OPERACIÓN\n");
        texto.append("F(b) - F(a) = ")
                .append(formatear(r.resultado()));

        try {
            if (Double.parseDouble(r.resultado()) < 0) {
                texto.append("\n\nEl resultado es negativo porque la curva ")
                        .append("se encuentra debajo del eje x en el intervalo.");
            }
        } catch (NumberFormatException ignorado) {
        }

        mostrar(texto.toString(), Theme.TEXT);

        valorArea.setText(formatear(r.resultado()));
        resumen.setVisible(true);

        revalidate();
        repaint();
    }

    private void mostrar(String texto, Color color) {
        area.setForeground(color);
        area.setText(texto);
        area.setCaretPosition(0);
    }

    private String limpiarPrimitiva(String primitiva) {
        return primitiva.replace("^ -", "^-")
                .replace("/ -", "/-")
                .replace("* -", "*-")
                .replace("+ -", "-");
    }

    private String formatear(String valor) {
        try {
            double numero = Double.parseDouble(valor);

            if (Math.abs(numero - Math.rint(numero)) < 1e-9
                    && Math.abs(numero) < 1e15) {
                return String.valueOf((long) Math.rint(numero));
            }

            String texto = String.format(Locale.US, "%.6f", numero);
            return texto.replaceAll("0+$", "").replaceAll("\\.$", "");

        } catch (NumberFormatException e) {
            return valor;
        }
    }
}
