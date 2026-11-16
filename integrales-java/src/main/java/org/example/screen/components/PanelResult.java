package org.example.screen.components;

import org.example.Connection;
import org.example.screen.Theme;

import javax.swing.BorderFactory;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import java.awt.Color;
import java.util.Locale;

/** Panel donde se muestra la primitiva, el área y los mensajes de error. */
public class PanelResult extends JScrollPane {

    private static final String TEXTO_INICIAL = "El resultado aparecerá aquí.";

    private final JTextArea area = new JTextArea(14, 44);

    public PanelResult() {
        area.setEditable(false);
        area.setFont(Theme.RESULT);
        area.setMargin(new java.awt.Insets(10, 12, 10, 12));
        area.setLineWrap(true);
        area.setWrapStyleWord(true);

        setViewportView(area);
        setBorder(BorderFactory.createLineBorder(Theme.BORDER));
        mostrarInicio();
    }

    public void mostrarInicio() {
        mostrar(TEXTO_INICIAL, Theme.MUTED);
    }

    public void mostrarCalculando() {
        mostrar("Calculando...", Theme.MUTED);
    }

    public void mostrarError(String mensaje) {
        mostrar(mensaje, Theme.ERROR);
    }

    /** Muestra el procedimiento completo: primitiva, F(b), F(a) y el área. */
    public void mostrarResultado(String funcion, String a, String b, Connection.Resultado r) {
        StringBuilder texto = new StringBuilder();
        texto.append("Integral definida de f(x) = ").append(funcion).append("\n");
        texto.append("entre a = ").append(a).append(" y b = ").append(b).append("\n\n");
        texto.append("Primitiva:  F(x) = ").append(limpiarPrimitiva(r.primitiva())).append("\n");
        texto.append("F(b) = F(").append(b).append(") = ").append(formatear(r.valorB())).append("\n");
        texto.append("F(a) = F(").append(a).append(") = ").append(formatear(r.valorA())).append("\n\n");
        texto.append("Área = F(b) - F(a) = ").append(formatear(r.resultado())).append("\n");

        try {
            if (Double.parseDouble(r.resultado()) < 0) {
                texto.append("\nResultado negativo: la parte de la curva que queda debajo del eje x resta.\n");
            }
        } catch (NumberFormatException ignorado) {
            // si no es un número, no se agrega la nota
        }
        mostrar(texto.toString(), Theme.TEXT);
    }

    private void mostrar(String texto, Color color) {
        area.setForeground(color);
        area.setText(texto);
        area.setCaretPosition(0);
    }

    /** SWI escribe x^ -1/ -1, 3* -cos(x) y a+ -b; se muestran sin esos espacios. */
    private String limpiarPrimitiva(String primitiva) {
        return primitiva.replace("^ -", "^-").replace("/ -", "/-")
                .replace("* -", "*-").replace("+ -", "-");
    }

    /** Enteros sin decimales; el resto con hasta 6 decimales. */
    private String formatear(String valor) {
        try {
            double numero = Double.parseDouble(valor);
            if (Math.abs(numero - Math.rint(numero)) < 1e-9 && Math.abs(numero) < 1e15) {
                return String.valueOf((long) Math.rint(numero));
            }
            String texto = String.format(Locale.US, "%.6f", numero);
            return texto.replaceAll("0+$", "").replaceAll("\\.$", "");
        } catch (NumberFormatException e) {
            return valor;
        }
    }
}
