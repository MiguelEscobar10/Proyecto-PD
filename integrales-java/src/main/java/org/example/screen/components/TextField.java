
package org.example.screen.components;

import org.example.screen.Theme;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import java.awt.BorderLayout;

public class TextField extends JPanel {

    private final JTextField campo;

    public TextField(String etiqueta, String valorInicial, int columnas) {
        super(new BorderLayout(0, 6));

        setOpaque(false);

        JLabel titulo = new JLabel(etiqueta);
        titulo.setFont(Theme.LABEL);
        titulo.setForeground(Theme.TEXT);

        campo = new JTextField(valorInicial, columnas);
        Theme.styleField(campo);

        add(titulo, BorderLayout.NORTH);
        add(campo, BorderLayout.CENTER);
    }

    public String getText() {
        return campo.getText().trim();
    }

    public void setText(String texto) {
        campo.setText(texto);
    }

    public void insertText(String texto) {
        campo.replaceSelection(texto);
        campo.requestFocusInWindow();
    }

    public void onEnter(Runnable accion) {
        campo.addActionListener(e -> accion.run());
    }

    public void focus() {
        campo.requestFocusInWindow();
    }
}
