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
 * Janela principal da interface gráfica (GUI - Graphical User Interface) do Simulador.
 *
 * Para que serve esta classe?
 * Ela cria a janela visual do programa (usando a biblioteca Swing do Java) e organiza as duas
 * funcionalidades principais do simulador em abas navegáveis na parte superior da tela:
 * - Aba 1 ("Operação única"): representada pela classe {@link PainelOperacao}, para fazer contas
 *   individuais (x + y, x - y, x * y, x / y) e comparar Truncamento vs. Arredondamento.
 * - Aba 2 ("Soma sucessiva"): representada pela classe {@link PainelSoma}, para repetir várias
 *   somas seguidas e visualizar o acúmulo progressivo de erros.
 *
 * Assim, o usuário pode realizar quantos cálculos quiser e alternar entre os modos livremente,
 * sem precisar fechar e abrir o programa novamente.
 *
 * @author laeds
 */
public class Janela extends JFrame {

    /**
     * Construtor que configura o título, o tamanho, o comportamento de fechamento
     * e adiciona as duas abas dentro da janela principal.
     */
    public Janela() {
        // Define o título que aparece na barra superior da janela
        super("Simulador de Erros Numéricos");

        // Garante que o programa encerre completamente quando o usuário clicar no botão "X" de fechar
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // Cria o componente de abas (JTabbedPane) e encaixa os dois painéis do projeto nele
        JTabbedPane abas = new JTabbedPane();
        abas.addTab("Operação única", new PainelOperacao());
        abas.addTab("Soma sucessiva", new PainelSoma());
        add(abas);

        // Define largura de 1000 pixels por 680 pixels de altura
        setSize(1000, 680);

        // Passar 'null' faz a janela abrir centralizada no meio do monitor do usuário
        setLocationRelativeTo(null);
    }

    /**
     * Ponto de entrada principal para iniciar o simulador no modo de janela gráfica.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        try {
            // Pede ao Java para usar o visual nativo do sistema operacional (Windows, Linux ou macOS),
            // deixando botões, tabelas e caixas de texto com uma aparência moderna e familiar.
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            // Se por algum motivo o tema do sistema não puder ser carregado, continua com o visual padrão do Java.
        }

        // Boa prática do Java Swing: toda janela gráfica deve ser criada e exibida dentro da
        // "Event Dispatch Thread" (a fila de eventos visuais) usando invokeLater, evitando travamentos.
        SwingUtilities.invokeLater(() -> new Janela().setVisible(true));
    }
}
