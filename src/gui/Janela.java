/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import javax.swing.JFrame;
import javax.swing.JTabbedPane;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * Janela principal do simulador, com uma aba para cada funcionalidade.
 *
 * @author laeds
 */
public class Janela extends JFrame {

    /** Monta a janela com as duas abas. */
    public Janela() {
        super("Simulador de Erros Numéricos");
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Operação única", new PainelOperacao());
        abas.addTab("Soma sucessiva", new PainelSoma());
        add(abas);

        setSize(1000, 680);
        setLocationRelativeTo(null);   // centraliza na tela
    }

    /**
     * Abre a janela.
     *
     * @param args argumentos de linha de comando (não utilizados)
     */
    public static void main(String[] args) {
        try {
            // Usa a aparência do sistema operacional.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se falhar, segue com a aparência padrão do Swing.
        }
        // Toda criação de componentes Swing deve ocorrer na thread da interface.
        SwingUtilities.invokeLater(() -> new Janela().setVisible(true));
    }
}
