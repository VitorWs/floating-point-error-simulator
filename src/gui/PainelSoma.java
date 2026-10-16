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
 * Painel visual correspondente à segunda aba da janela gráfica: "Soma sucessiva".
 *
 * Para que serve esta tela?
 * Foi construída especificamente para demonstrar na prática a propagação e o acúmulo de erros
 * em cálculos sequenciais (como o Exemplo 3 do trabalho: somar 0,56786 dez vezes seguidas).
 *
 * O usuário informa:
 * - O valor que será somado repetidamente;
 * - A quantidade de somas (padrão: 10 vezes);
 * - O número de dígitos significativos da máquina (padrão: n = 4).
 *
 * A tela exibe duas tabelas completas (uma para Truncamento e outra para Arredondamento simétrico),
 * mostrando passo a passo (da 1ª até a última soma) como o erro vai crescendo a cada corte,
 * além de destacar o Erro Absoluto (Ea) e o Erro Relativo (Er) totais ao final da sequência.
 *
 * @author laeds
 */
public class PainelSoma extends JPanel {

    /** Títulos das 6 colunas das tabelas passo a passo. */
    private static final String[] COLUNAS = {
        "Passo", "Precisão finita", "Exato", "Aproximado", "Erro absoluto", "Erro relativo"
    };

    /** Campo onde o usuário digita a parcela que será somada repetidas vezes (ex: "0,56786"). */
    private final JTextField campoValor = new JTextField(10);

    /** Seletor da quantidade de vezes que o número será somado (padrão: 10, mínimo: 1, máximo: 1000). */
    private final JSpinner campoVezes = new JSpinner(new SpinnerNumberModel(10, 1, 1000, 1));

    /** Seletor da quantidade de dígitos significativos 'n' (padrão: 4, mínimo: 1, máximo: 50). */
    private final JSpinner campoN = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));

    /**
     * Guarda um modelo de tabela e um rótulo de resumo ("Erro total após N somas...") para cada
     * modo de corte existente em {@link ModoCorte#values()} (posições 0 = Truncamento, 1 = Arredondamento).
     */
    private final DefaultTableModel[] modelos = new DefaultTableModel[ModoCorte.values().length];
    private final JLabel[] rotulosTotal = new JLabel[ModoCorte.values().length];

    /**
     * Construtor do painel: posiciona o formulário de entrada no topo ({@code NORTH})
     * e as duas tabelas comparativas no espaço central ({@code CENTER}).
     */
    public PainelSoma() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(montarFormulario(), BorderLayout.NORTH);
        add(montarTabelas(), BorderLayout.CENTER);
    }

    /**
     * Monta a barra superior com os campos "Valor a somar", "Quantidade de somas",
     * "Dígitos significativos (n)" e os botões "Calcular" e "Limpar".
     */
    private JPanel montarFormulario() {
        JButton botaoCalcular = new JButton("Calcular");
        JButton botaoLimpar = new JButton("Limpar");
        botaoCalcular.addActionListener(e -> calcular());
        botaoLimpar.addActionListener(e -> limpar());

        // Permite iniciar o cálculo simplesmente pressionando Enter no campo do valor
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

    /**
     * Cria um painel dividido verticalmente ({@link GridLayout}) contendo dois blocos:
     * o bloco superior com a tabela do Truncamento e o bloco inferior com a tabela do Arredondamento.
     */
    private JPanel montarTabelas() {
        JPanel painel = new JPanel(new GridLayout(modelos.length, 1, 0, 10));
        ModoCorte[] modos = ModoCorte.values();
        for (int i = 0; i < modos.length; i++) {
            // Cria o modelo de dados da tabela bloqueando edição manual das células
            modelos[i] = new DefaultTableModel(COLUNAS, 0) {
                @Override
                public boolean isCellEditable(int linha, int coluna) {
                    return false;
                }
            };
            rotulosTotal[i] = new JLabel(" ");

            // Empacota a tabela (com rolagem) e o rótulo de resumo final dentro de uma moldura com título
            JPanel bloco = new JPanel(new BorderLayout(0, 5));
            bloco.setBorder(BorderFactory.createTitledBorder(modos[i].getDescricao()));
            bloco.add(new JScrollPane(criarTabela(modelos[i])), BorderLayout.CENTER);
            bloco.add(rotulosTotal[i], BorderLayout.SOUTH);
            painel.add(bloco);
        }
        return painel;
    }

    /**
     * Configura a aparência visual de uma tabela: altura das linhas, travamento da ordem das colunas,
     * número do passo centralizado (coluna 0) e valores numéricos alinhados à direita (colunas 2 a 5).
     */
    private JTable criarTabela(DefaultTableModel modelo) {
        JTable tabela = new JTable(modelo);
        tabela.setRowHeight(22);
        tabela.getTableHeader().setReorderingAllowed(false);

        // Centraliza a coluna "Passo" (1, 2, 3...)
        DefaultTableCellRenderer centro = new DefaultTableCellRenderer();
        centro.setHorizontalAlignment(SwingConstants.CENTER);
        tabela.getColumnModel().getColumn(0).setCellRenderer(centro);

        // Alinha à direita as colunas de valores e erros (Exato, Aproximado, Erro absoluto e Erro relativo)
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna = 2; coluna < COLUNAS.length; coluna++) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
        return tabela;
    }

    /**
     * Ação acionada pelo botão "Calcular":
     * Lê o valor, a quantidade de repetições e 'n', chama {@link Simulador#somaSucessiva}
     * para cada modo de corte e preenche todas as linhas das duas tabelas passo a passo.
     */
    private void calcular() {
        String valor = lerNumero();
        if (valor == null) {
            return;
        }
        int vezes = (Integer) campoVezes.getValue();
        int n = (Integer) campoN.getValue();

        ModoCorte[] modos = ModoCorte.values();
        for (int i = 0; i < modos.length; i++) {
            // Solicita ao núcleo a lista com o resultado de cada passo da soma sucessiva
            List<ResultadoOperacao> lista = Simulador.somaSucessiva(valor, vezes, n, modos[i]);

            // Limpa a tabela do modo atual e adiciona uma linha para cada passo da sequência
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

            // Destaca no rodapé da tabela o erro total acumulado após o último passo
            ResultadoOperacao ultimo = lista.get(lista.size() - 1);
            rotulosTotal[i].setText("Erro total após " + vezes + " somas:  Ea = "
                    + Formatador.formatar(ultimo.erroAbsoluto())
                    + "   |   Er = " + Formatador.formatar(ultimo.erroRelativo()));
        }
    }

    /**
     * Valida se o campo "Valor a somar" contém um número decimal válido (com vírgula ou ponto).
     * Caso contrário, exibe uma janela de aviso e devolve {@code null}.
     */
    private String lerNumero() {
        String texto = campoValor.getText().trim();
        try {
            // Criado só para validar se o texto tem formato numérico válido.
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

    /**
     * Restaura a aba ao seu estado original (limpa o campo de texto, volta para 10 somas e n = 4,
     * e esvazia as duas tabelas).
     */
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
