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
 * Painel visual correspondente à primeira aba da janela gráfica: "Operação única".
 *
 * Para que serve esta tela?
 * Permite ao usuário digitar dois números quaisquer ({@code x} e {@code y}), escolher uma das
 * quatro operações básicas (+, -, *, /) e definir o limite de dígitos significativos ({@code n}).
 * Ao clicar em "Calcular" (ou apertar Enter), a tela consulta o pacote {@code nucleo} e exibe:
 * 1) No centro: uma tabela comparando lado a lado o resultado por Truncamento e por Arredondamento,
 *    junto com o valor Exato, o valor Aproximado, o Erro Absoluto (Ea) e o Erro Relativo (Er);
 * 2) Na parte inferior: uma caixa mostrando como os operandos {@code x} e {@code y} ficaram
 *    armazenados na máquina após o corte de {@code n} dígitos (muito útil para explicar fenômenos
 *    como o Cancelamento Subtrativo do Exemplo 2).
 *
 * @author laeds
 */
public class PainelOperacao extends JPanel {

    /** Títulos das 6 colunas que aparecem no topo da tabela de resultados. */
    private static final String[] COLUNAS = {
        "Modo", "Precisão finita", "Exato", "Aproximado", "Erro absoluto", "Erro relativo"
    };

    /** Campo de texto onde o usuário digita o primeiro número (x). */
    private final JTextField campoX = new JTextField(10);

    /** Campo de texto onde o usuário digita o segundo número (y). */
    private final JTextField campoY = new JTextField(10);

    /** Caixa de seleção (dropdown) para escolher a operação matemática (+, -, *, /). */
    private final JComboBox<Operacao> caixaOperacao = new JComboBox<>(Operacao.values());

    /**
     * Seletor numérico com setinhas (spinner) para escolher o número de dígitos significativos 'n'.
     * Começa em 4 (valor padrão pedido nos testes do trabalho), aceitando valores de 1 até 50.
     */
    private final JSpinner campoN = new JSpinner(new SpinnerNumberModel(4, 1, 50, 1));

    /**
     * Modelo de dados que guarda as linhas da tabela.
     * Sobrescrevemos {@code isCellEditable} retornando {@code false} para impedir que o usuário
     * apague ou altere manualmente os resultados dando duplo-clique nas células da tabela.
     */
    private final DefaultTableModel modeloTabela = new DefaultTableModel(COLUNAS, 0) {
        @Override
        public boolean isCellEditable(int linha, int coluna) {
            return false;
        }
    };

    /** Área de texto no rodapé da aba que exibe como x e y ficaram guardados após o corte. */
    private final JTextArea areaOperandos = new JTextArea(3, 40);

    /**
     * Construtor do painel: divide a tela em três regiões usando {@link BorderLayout}:
     * - Norte (topo): barra com os campos de entrada e botões;
     * - Centro (meio): tabela comparativa de resultados e erros;
     * - Sul (embaixo): quadro mostrando os operandos armazenados após o corte.
     */
    public PainelOperacao() {
        setLayout(new BorderLayout(10, 10));
        // Cria uma margem interna de 10 pixels ao redor de todo o painel para não colar nas bordas da janela
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        add(montarFormulario(), BorderLayout.NORTH);
        add(montarTabela(), BorderLayout.CENTER);
        add(montarAreaOperandos(), BorderLayout.SOUTH);
    }

    /**
     * Monta a barra superior com os rótulos, caixas de texto e botões "Calcular" e "Limpar".
     */
    private JPanel montarFormulario() {
        // Personaliza a caixa de seleção de operações para mostrar apenas o símbolo matemático
        // ('+', '-', '*', '/') em vez do nome técnico do enum ("SOMA", "SUBTRACAO", etc.).
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

        // Conecta os botões às funções que devem rodar quando eles forem clicados
        botaoCalcular.addActionListener(e -> calcular());
        botaoLimpar.addActionListener(e -> limpar());

        // Atalho de usabilidade: apertar a tecla "Enter" dentro dos campos X ou Y também dispara o cálculo!
        campoX.addActionListener(e -> calcular());
        campoY.addActionListener(e -> calcular());

        // Organiza todos os itens em uma linha da esquerda para a direita (FlowLayout.LEFT)
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

    /**
     * Cria e configura a tabela visual onde os resultados são exibidos, envolvida em um
     * painel com barra de rolagem ({@link JScrollPane}).
     */
    private JScrollPane montarTabela() {
        JTable tabela = new JTable(modeloTabela);
        tabela.setRowHeight(26); // Deixa as linhas mais altas para facilitar a leitura
        tabela.getTableHeader().setReorderingAllowed(false); // Fixa a ordem das colunas

        // Alinha as colunas puramente numéricas (colunas 2 a 5: Exato, Aproximado, Ea e Er) à direita,
        // seguindo a convenção padrão de tabelas financeiras e científicas.
        DefaultTableCellRenderer direita = new DefaultTableCellRenderer();
        direita.setHorizontalAlignment(SwingConstants.RIGHT);
        for (int coluna = 2; coluna < COLUNAS.length; coluna++) {
            tabela.getColumnModel().getColumn(coluna).setCellRenderer(direita);
        }
        return new JScrollPane(tabela);
    }

    /**
     * Monta o quadro inferior que exibe os operandos {@code x} e {@code y} já normalizados
     * e cortados em {@code n} dígitos significativos.
     */
    private JScrollPane montarAreaOperandos() {
        areaOperandos.setEditable(false); // Apenas leitura (o usuário não digita aqui)
        JScrollPane painel = new JScrollPane(areaOperandos);
        painel.setBorder(BorderFactory.createTitledBorder("Operandos armazenados (depois do corte)"));
        return painel;
    }

    /**
     * Ação executada quando o usuário clica em "Calcular" ou pressiona Enter:
     * 1. Valida os valores digitados nos campos X e Y;
     * 2. Chama o núcleo ({@link Simulador#executar}) para Truncamento e para Arredondamento;
     * 3. Atualiza a tabela central e o quadro de operandos armazenados na parte inferior.
     */
    private void calcular() {
        // 1) Lê e valida os campos X e Y. Se algum estiver inválido, lerNumero já mostra o aviso e retorna null.
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

        // 2) Calcula os dois modos antes de limpar a tabela na tela. Assim, se ocorrer um erro
        // (como divisão por zero), a tabela não fica vazia ou preenchida pela metade.
        ModoCorte[] modos = ModoCorte.values();
        ResultadoOperacao[] resultados = new ResultadoOperacao[modos.length];
        try {
            for (int i = 0; i < modos.length; i++) {
                resultados[i] = Simulador.executar(x, y, op, n, modos[i]);
            }
        } catch (ArithmeticException e) {
            // Mostra uma janela de alerta amigável caso o usuário tente dividir por zero.
            JOptionPane.showMessageDialog(this, e.getMessage(),
                    "Erro no cálculo", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 3) Limpa os resultados anteriores da tabela e insere as novas linhas calculadas
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
     * Verifica se o texto digitado em um campo ({@code x} ou {@code y}) é um número válido
     * (aceitando tanto vírgula quanto ponto decimal).
     * Se o usuário tiver digitado letras ou deixado em branco, abre uma caixa de aviso,
     * coloca o cursor de volta no campo com problema e retorna {@code null}.
     *
     * @param campo Componente visual de texto a ser lido.
     * @param nome  Nome do campo ("x" ou "y") para indicar na mensagem de erro.
     * @return O texto numérico validado, ou {@code null} se for inválido.
     */
    private String lerNumero(JTextField campo, String nome) {
        String texto = campo.getText().trim();
        try {
            // Instancia um BigDecimal apenas para testar se o texto representa um número válido.
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

    /**
     * Limpa todos os campos de entrada e apaga a tabela de resultados, deixando a aba
     * pronta para um novo experimento (atendendo ao requisito de novos cálculos sem reiniciar).
     */
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
