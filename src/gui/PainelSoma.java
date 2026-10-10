/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package gui;

import apresentacao.Formatador;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.math.BigDecimal;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSpinner;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import nucleo.ModoCorte;
import nucleo.ResultadoOperacao;
import nucleo.Simulador;

/**
 * Aba "Soma sucessiva": soma um valor várias vezes, cortando o acumulado a
 * cada passo, e mostra uma tabela por modo de corte com o erro total.
 *
 * @author laeds
 */
public class PainelSoma extends JPanel {

    /** Títulos das colunas das tabelas. */
    private static final String[] COLUNAS = {
        "Passo", "Precisão finita", "Exato", "Aproximado", "Erro absoluto", "Erro relativo"
    };

    private final JTextField campoValor = new JTextField(10);
    private final JSpinner campoVezes = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));
    private final JSpinner campoN = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));

    // Uma tabela e um rótulo de erro total para cada modo (mesma ordem de ModoCorte.values()).
    private final DefaultTableModel[] modelos = new DefaultTableModel[ModoCorte.values().length];
    private final JLabel[] rotulosTotal = new JLabel[ModoCorte.values().length];

    /** Monta a aba: formulário em cima e uma tabela para cada modo embaixo. */
    public PainelSoma() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(montarFormulario(), BorderLayout.NORTH);
        add(montarTabelas(), BorderLayout.CENTER);
    }

    /** Linha com os campos de entrada e os botões. */
    private JPanel montarFormulario() {
        JButton botaoCalcular = new JButton("Calcular");
        JButton botaoLimpar = new JButton("Limpar");
        botaoCalcular.addActionListener(e -> calcular());
        botaoLimpar.addActionListener(e -> limpar());
        // Enter no campo de texto também calcula.
        campoValor.addActionListener(e -> calcular());

        JPanel formulario = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 5));
        formulario.add(new JLabel("Valor a somar:"));
        formulario.add(campoValor);
        formulario.add(new JLabel("Quantidade de somas:"));
        formulario.add(campoVezes);
        formulario.add(new JLabel("Dígitos significativos (n):"));
        formulario.add(campoN);
        formulario.add(botaoCalcular);
        formulario.add(botaoLimpar);
        return formulario;
    }

    /** Uma tabela por modo de corte, uma embaixo da outra. */
    private JPanel montarTabelas() {
        JPanel painel = new JPanel(new GridLayout(modelos.length, 1, 0, 10));
        ModoCorte[] modos = ModoCorte.values();
        for (int i = 0; i < modos.length; i++) {
            modelos[i] = new DefaultTableModel(COLUNAS, 0) {
                @Override
                public boolean isCellEditable(int linha, int coluna) {
                    return false;
                }
            };
            rotulosTotal[i] = new JLabel(" ");

            JPanel bloco = new JPanel(new BorderLayout(0, 5));
            bloco.setBorder(BorderFactory.createTitledBorder(modos[i].getDescricao()));
            bloco.add(new JScrollPane(criarTabela(modelos[i])), BorderLayout.CENTER);
            bloco.add(rotulosTotal[i], BorderLayout.SOUTH);
            painel.add(bloco);
        }
        return painel;
    }

    /** Cria uma tabela com o alinhamento das colunas já configurado. */
    private JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(22);
        tabela.getTableHeader().setReorderingAllowed(false);

        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centro);

        // Números alinhados à direita (colunas 2 a 5).
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna = 2; coluna < COLUNAS.length; coluna++) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
        return tabela;
    }

    /** Lê os campos, faz a soma sucessiva nos dois modos e preenche as tabelas. */
    private void calcular() {
        String valor = lerNumero();
        if (valor == null) {
            return;
        }
        int vezes = (Integer) campoVezes.getValue();
        int n = (Integer) campoN.getValue();

        ModoCorte[] modos = ModoCorte.values();
        for (int i = 0; i < modos.length; i++) {
            List<ResultadoOperacao> lista = Simulador.somaSucessiva(valor, vezes, n, modos[i]);

            modelos[i].setRowCount(0);
            for (int passo = 0; passo < lista.size(); passo++) {
                ResultadoOperacao r = lista.get(passo);
                modelos[i].addRow(new Object[] {
                    String.valueOf(passo + 1),
                    r.resultado().toString(),
                    Formatador.formatar(r.exato()),
                    Formatador.formatar(r.aproximado()),
                    Formatador.formatar(r.erroAbsoluto()),
                    Formatador.formatar(r.erroRelativo())
                });
            }

            // O último elemento é o acumulado final (erro total).
            ResultadoOperacao ultimo = lista.get(lista.size() - 1);
            rotulosTotal[i].setText("Erro total após " + vezes + " somas:  Ea = "
                    + Formatador.formatar(ultimo.erroAbsoluto())
                    + "   |   Er = " + Formatador.formatar(ultimo.erroRelativo()));
        }
    }

    /**
     * Valida o texto do campo como número (vírgula ou ponto).
     * Se for inválido, avisa o usuário e devolve {@code null}.
     */
    private String lerNumero() {
        String texto = campoValor.getText().trim();
        try {
            // Criado só para validar o texto.
            new BigDecimal(texto.replace(',', '.'));
            return texto;
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this,
                    "Valor inválido. Use números como 0,56786.",
                    "Entrada inválida", JOptionPane.WARNING_MESSAGE);
            campoValor.requestFocusInWindow();
            campoValor.selectAll();
            return null;
        }
    }

    /** Volta a aba ao estado inicial. */
    private void limpar() {
        campoValor.setText("");
        campoVezes.setValue(10);
        campoN.setValue(4);
        for (int i = 0; i < modelos.length; i++) {
            modelos[i].setRowCount(0);
            rotulosTotal[i].setText(" ");
        }
        campoValor.requestFocusInWindow();
    }
}
