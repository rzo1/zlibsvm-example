package de.hsheilbronn.mi;

import de.hsheilbronn.mi.controller.SvmController;
import de.hsheilbronn.mi.ui.MainWindow;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new MainWindow(new SvmController()));
    }
}
