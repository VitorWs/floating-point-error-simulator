/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;
 
import apresentacao.Formatador;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.FlowLayout;
import java.math.BigDecimal;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import nucleo.ModoCorte;
import nucleo.Operacao;
import nucleo.ResultadoOperacao;
import nucleo.Simulador;
 
/**
 * Aba "Operação única": calcula x (op) y com truncamento e arredondamento
 * e mostra os dois resultados lado a lado numa tabela.
 *
 * @author laeds
 */
public class PainelOperacao extends JPanel {
 
    /* Títulos das colunas da tabela. */
    private static final String[] COLUNAS = {
        "Modo", "Precisão finita", "Exato", "Aproximado", "Erro absoluto", "Erro relativo"
    };
 
    private final JTextField campoX = new JTextField(10);
    private final JTextField campoY = new JTextField(10);
    private final JComboBox<Operacao> caixaOperacao = new JComboBox<>(Operacao.values());
    private final JSpinner campoN = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));
 
    /* Dados da tabela, com as células não editáveis. */
    private final DefaultTableModel modeloTabela = new DefaultTableModel(COLUNAS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };
 
    private final JTextArea areaOperandos = new JTextArea(3, 40);
 
    /* Monta a aba: formulário em cima, tabela no meio e operandos embaixo. */
    public PainelOperacao() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
 
        add(montarFormulario(), BorderLayout.NORTH);
        add(montarTabela(), BorderLayout.CENTER);
        add(montarAreaOperandos(), BorderLayout.SOUTH);
    }
 
    /* Linha com os campos de entrada e os botões. */
    private JPanel montarFormulario() {
        // A caixa mostra só o símbolo (+, -, *, /) em vez do nome do enum.
        caixaOperacao.setRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> lista, Object valor,
                    int indice, boolean selecionado, boolean comFoco) {
                super.getListCellRendererComponent(lista, valor, indice, selecionado, comFoco);
                if (valor instanceof Operacao op) {
                    setText(String.valueOf(op.getSimbolo()));
                }
                return this;
            }
        });
 
        JButton botaoCalcular = new JButton("Calcular");
        JButton botaoLimpar = new JButton("Limpar");
        botaoCalcular.addActionListener(e -> calcular());
        botaoLimpar.addActionListener(e -> limpar());
        // Enter nos campos de texto também calcula.
        campoX.addActionListener(e -> calcular());
        campoY.addActionListener(e -> calcular());
 
        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        formulario.add(new JLabel("x:"));
        formulario.add(campoX);
        formulario.add(new JLabel("Operação:"));
        formulario.add(caixaOperacao);
        formulario.add(new JLabel("y:"));
        formulario.add(campoY);
        formulario.add(new JLabel("Dígitos significativos (n):"));
        formulario.add(campoN);
        formulario.add(botaoCalcular);
        formulario.add(botaoLimpar);
        return formulario;
    }
 
    /* Tabela de resultados, dentro de um painel com rolagem. */
    private JScrollPane montarTabela() {
        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26);
        tabela.getTableHeader().setReorderingAllowed(false);
 
        // Números alinhados à direita (colunas 2 a 5).
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna = 2; coluna < COLUNAS.length; coluna++) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
        return new JScrollPane(tabela);
    }
 
    /* Área de texto com os operandos depois do corte. */
    private JScrollPane montarAreaOperandos() {
        areaOperandos.setEditable(false);
        JScrollPane painel = new JScrollPane(areaOperandos);
        painel.setBorder(BorderFactory.createTitledBorder("Operandos armazenados (depois do corte)"));
        return painel;
    }
 
    /* Lê os campos, calcula nos dois modos e preenche a tabela. */
    private void calcular() {
        String x = lerNumero(campoX, "x");
        if (x == null) {
            return;
        }
        String y = lerNumero(campoY, "y");
        if (y == null) {
            return;
        }
        Operacao op = (Operacao) caixaOperacao.getSelectedItem();
        int n = (Integer) campoN.getValue();
 
        // Calcula tudo antes de mexer na tabela, para um erro não deixá-la pela metade.
        ModoCorte[] modos = ModoCorte.values();
        ResultadoOperacao[] resultados = new ResultadoOperacao[modos.length];
        try {
            for (int i = 0; i < modos.length; i++) {
                resultados[i] = Simulador.executar(x, y, op, n, modos[i]);
            }
        } catch (ArithmeticException e) {
            // Por exemplo, divisão por zero.
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro no cálculo", JOptionPane.ERROR_MESSAGE);
            return;
        }
 
        modeloTabela.setRowCount(0);
        StringBuilder operandos = new StringBuilder();
        for (int i = 0; i < modos.length; i++) {
            ResultadoOperacao r = resultados[i];
            modeloTabela.addRow(new Object[] {
                modos[i].toString(),
                r.resultado().toString(),
                Formatador.formatar(r.exato()),
                Formatador.formatar(r.aproximado()),
                Formatador.formatar(r.erroAbsoluto()),
                Formatador.formatar(r.erroRelativo())
            });
            operandos.append(modos[i]).append(": x = ").append(r.x())
                    .append(" | y = ").append(r.y()).append("\n");
        }
        areaOperandos.setText(operandos.toString());
    }
 
    /**
     * Valida o texto de um campo como número (vírgula ou ponto).
     * Se for inválido, avisa o usuário e devolve {@code null}.
     */
    private String lerNumero(JTextField campo, String nome) {
        String texto = campo.getText().trim();
        try {
            // Criado só para validar o texto.
            new BigDecimal(texto.replace(',', '.'));
            return texto;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valor inválido em " + nome + ". Use números como 0,76545.",
                    "Entrada inválida", JOptionPane.WARNING_MESSAGE);
            campo.requestFocusInWindow();
            campo.selectAll();
            return null;
        }
    }
 
    /** Volta a aba ao estado inicial. */
    private void limpar() {
        campoX.setText("");
        campoY.setText("");
        caixaOperacao.setSelectedIndex(0);
        campoN.setValue(4);
        modeloTabela.setRowCount(0);
        areaOperandos.setText("");
        campoX.requestFocusInWindow();
    }
}
