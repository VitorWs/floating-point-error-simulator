/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package commandLineInteface;

import java.math.BigDecimal;
import java.util.Scanner;
import nucleo.ModoCorte;
import nucleo.Operacao;
import nucleo.ResultadoOperacao;
import nucleo.Simulador;
import apresentacao.Formatador;
import java.util.List;

/**
 * Interface de Linha de Comando (CLI - Command Line Interface) do Simulador de Propagação de Erros.
 *
 * Para que serve esta classe?
 * Permite usar o simulador diretamente no terminal (console de texto), sem precisar abrir janelas gráficas.
 * Ela funciona como um atendente:
 * 1. Mostra um menu numerado para o usuário escolher o que deseja fazer;
 * 2. Faz perguntas no teclado ("Digite x:", "Digite y:", etc.) e valida cada resposta para garantir
 *    que o programa nunca trave caso o usuário digite letras ou símbolos errados;
 * 3. Envia os dados validados para o pacote {@code nucleo} ({@link Simulador}) fazer os cálculos matemáticos;
 * 4. Desenha tabelas alinhadas no terminal mostrando lado a lado o Truncamento e o Arredondamento.
 *
 * @author laeds
 */
public class Cli {

    /**
     * Objeto {@code Scanner} único conectado ao teclado ({@code System.in}) para ler o que o usuário digita.
     */
    private static Scanner entrada = new Scanner(System.in);

    /**
     * Molde de formatação das colunas da tabela de texto no terminal.
     * O que significam os códigos?
     * - {@code %-15s}: texto alinhado à esquerda ocupando 15 espaços (usado na 1ª coluna: Modo ou Passo);
     * - {@code %-17s}: texto alinhado à esquerda ocupando 17 espaços (2ª coluna: número em Precisão finita);
     * - {@code %13s}:  texto alinhado à direita ocupando 13 espaços (colunas numéricas: Exato, Aproximado, Ea, Er).
     */
    private static final String FORMATO_LINHA = "%-15s | %-17s | %13s | %13s | %13s | %13s";

    /**
     * Exibe na tela as opções do menu principal do programa.
     */
    public static void mostrarMenu() {
        System.out.println("|| Calculadora! ||");
        System.out.println("");
        System.out.println("- Escolha uma opcao: -");
        System.out.println("");
        System.out.println("1. Operacao unica");
        System.out.println("2. Soma sucessiva");
        System.out.println("3. Sair");
        System.out.println("");
    }

    /**
     * Lê uma linha inteira digitada pelo usuário no teclado e remove espaços em branco
     * acidentais no começo e no fim do texto (usando {@code trim()}).
     *
     * @return O texto digitado pelo usuário sem espaços nas bordas.
     */
    public static String lerLinha() {
        return entrada.nextLine().trim();
    }

    /**
     * Executa a Opção 1 do menu: "Operação única".
     *
     * Passo a passo do que este método faz:
     * 1. Pede ao usuário os dois números (x e y), qual operação deseja fazer (+, -, *, /)
     *    e a quantidade de dígitos significativos (n);
     * 2. Chama o {@link Simulador#executar} duas vezes: uma para TRUNCAMENTO e outra para ARREDONDAMENTO;
     * 3. Imprime uma tabela comparativa com o resultado normalizado, valor exato, valor aproximado,
     *    erro absoluto e erro relativo de cada modo;
     * 4. Imprime também como os números x e y ficaram guardados na máquina depois do corte,
     *    ajudando a entender visualmente de onde surgiu a diferença no resultado final.
     */
    public static void executarOperacaoUnica() {
        // 1) Coletar as entradas do usuário (cada função repete a pergunta até receber um valor válido)
        String x = lerNumero("Digite x: ");
        String y = lerNumero("Digite y: ");
        Operacao op = lerOperacao();
        int n = lerInteiro("Digitos significativos (n): ", 1);

        // 2) Calcular nos dois modos (Truncamento e Arredondamento) e, só se não houver erro, mostrar a tabela
        ModoCorte[] modos = ModoCorte.values();
        try {
            ResultadoOperacao[] resultados = new ResultadoOperacao[modos.length];
            for (int i = 0; i < modos.length; i++) {
                resultados[i] = Simulador.executar(x, y, op, n, modos[i]);
            }

            // 3) Exibir o título da conta e a tabela de resultados comparando os dois modos
            System.out.println("");
            System.out.println(x + " " + op.getSimbolo() + " " + y + "   (n = " + n + ")");
            imprimirCabecalho("Modo");
            for (int i = 0; i < modos.length; i++) {
                imprimirLinha(modos[i].toString(), resultados[i]);
            }

            // 4) Mostrar como os operandos x e y ficaram armazenados na memória de 'n' dígitos antes da conta
            System.out.println("");
            System.out.println("Operandos armazenados com " + n + " digitos:");
            for (int i = 0; i < modos.length; i++) {
                System.out.println(modos[i] + ": x = " + resultados[i].x()
                        + " | y = " + resultados[i].y());
            }
            System.out.println("");
        } catch (ArithmeticException e) {
            // Captura erros matemáticos (como tentativa de divisão por zero) e avisa sem fechar o programa.
            System.out.println(e.getMessage());
        }
    }

    /**
     * Pede um número decimal ao usuário (aceitando tanto vírgula quanto ponto) e repete a pergunta
     * em um laço ({@code while}) até que o usuário digite um número válido.
     *
     * Por que devolvemos {@code String} em vez de {@code double}?
     * Porque o núcleo de cálculo recebe o texto original para criar um {@code BigDecimal} exato,
     * evitando que o tipo {@code double} distorça os dígitos antes do corte.
     *
     * @param mensagem Texto de instrução que aparece na tela (ex: "Digite x: ").
     * @return O texto numérico validado digitado pelo usuário.
     */
    public static String lerNumero(String mensagem) {
        while (true) {
            System.out.print(mensagem);
            String texto = lerLinha();

            try {
                // Tentamos criar um BigDecimal apenas para testar se o texto tem formato de número válido.
                BigDecimal bigDecimal = new BigDecimal(texto.replace(',', '.'));
                return texto;
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Use números como 0,76545.");
            }
        }
    }

    /**
     * Pede um número inteiro ao usuário (como a quantidade de dígitos 'n' ou o número de somas)
     * e repete a pergunta até receber um inteiro válido que seja maior ou igual a {@code valorMinimo}.
     *
     * @param mensagem    Texto de instrução exibido na tela.
     * @param valorMinimo Menor valor aceito (por exemplo, 1 dígito significativo no mínimo).
     * @return O número inteiro validado.
     */
    public static int lerInteiro(String mensagem, int valorMinimo) {
        while (true) {
            System.out.print(mensagem);
            String texto = lerLinha();

            try {
                int numero = Integer.parseInt(texto);

                if (numero >= valorMinimo) {
                    return numero;
                } else {
                    System.out.println("Numero menor que o minimo (" + valorMinimo + ").");
                }
            } catch (NumberFormatException e) {
                System.out.println("Valor inválido. Use números inteiros, por exemplo 4.");
            }
        }
    }

    /**
     * Pede ao usuário o símbolo da operação aritmética desejada (+, -, * ou /)
     * e repete a pergunta até que um símbolo válido seja informado.
     *
     * @return O item da enumeração {@link Operacao} correspondente ao símbolo digitado.
     */
    public static Operacao lerOperacao() {

        while (true) {
            System.out.print("Operacao (+,-,*,/): ");
            String escolha = lerLinha();

            // Garante que o usuário digitou exatamente 1 caractere antes de inspecionar qual é.
            if (escolha.length() == 1) {

                // Procura entre as operações disponíveis (SOMA, SUBTRACAO, MULTIPLICACAO, DIVISAO)
                // qual delas tem o símbolo igual ao que o usuário digitou.
                for (Operacao operacao : Operacao.values()) {
                    if (operacao.getSimbolo() == escolha.charAt(0)) {
                        return operacao;
                    }
                }

                System.out.println("Digite uma opcao valida.");

            } else {
                System.out.println("Digite uma unica operacao sem espacos.");
            }
        }
    }

    /**
     * Imprime no terminal o cabeçalho da tabela de resultados e uma linha divisória de traços ('-').
     *
     * @param tituloPrimeiraColuna Nome da primeira coluna ("Modo" na operação única ou "Passo" na soma sucessiva).
     */
    private static void imprimirCabecalho(String tituloPrimeiraColuna) {
        String cabecalho = String.format(FORMATO_LINHA,
                tituloPrimeiraColuna, "Precisao finita", "Exato", "Aproximado",
                "Erro absoluto", "Erro relativo");
        System.out.println(cabecalho);
        System.out.println("-".repeat(cabecalho.length()));
    }

    /**
     * Imprime uma linha formatada da tabela no terminal com os dados de um {@link ResultadoOperacao}.
     *
     * @param rotulo Texto da primeira coluna (ex: "TRUNCAMENTO" ou o número do passo "1", "2"...).
     * @param r      Objeto contendo os resultados e erros da operação.
     */
    private static void imprimirLinha(String rotulo, ResultadoOperacao r) {
        System.out.println(String.format(FORMATO_LINHA,
                rotulo,
                r.resultado(),
                Formatador.formatar(r.exato()),
                Formatador.formatar(r.aproximado()),
                Formatador.formatar(r.erroAbsoluto()),
                Formatador.formatar(r.erroRelativo())));
    }

    /**
     * Executa a Opção 2 do menu: "Soma sucessiva" (Exemplo 3 do trabalho).
     *
     * Pede um número, quantas vezes ele deve ser somado a si mesmo e o número de dígitos 'n'.
     * Em seguida, mostra uma tabela completa passo a passo para o TRUNCAMENTO e outra para o
     * ARREDONDAMENTO, exibindo ao final de cada tabela o Erro Absoluto e o Erro Relativo acumulados.
     */
    public static void executarSomaSucessiva() {
        String x = lerNumero("Digite o numero a ser somado: ");
        int vezesSomadas = lerInteiro("Digite quantas vezes o numero sera somado: ", 1);
        int digitosSignificativos = lerInteiro("Digitos significativos (n): ", 1);

        // Executa e imprime uma tabela inteira para cada modo de corte (Truncamento e Arredondamento)
        for (ModoCorte modo : ModoCorte.values()) {
            List<ResultadoOperacao> lista = Simulador.somaSucessiva(x, vezesSomadas, digitosSignificativos, modo);

            System.out.println("");
            System.out.println(modo);
            imprimirCabecalho("Passo");

            for (int i = 0; i < lista.size(); i++) {
                imprimirLinha(String.valueOf(i + 1), lista.get(i));
            }

            // O último item da lista guarda o resultado após todas as somas (erro total acumulado).
            ResultadoOperacao ultimo = lista.get(lista.size() - 1);
            System.out.println("Erro total apos " + vezesSomadas + " somas: Ea = "
                    + Formatador.formatar(ultimo.erroAbsoluto())
                    + " | Er = " + Formatador.formatar(ultimo.erroRelativo()));
        }

        System.out.println("");
    }

    /**
     * Ponto de entrada principal da versão de linha de comando (CLI).
     * Mantém o programa rodando em um laço ({@code do-while}), permitindo que o usuário realize
     * quantos cálculos quiser seguidos até escolher a opção "3. Sair" (atendendo ao requisito
     * do edital de permitir novos testes sem precisar reiniciar o programa).
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        boolean continuar = true;

        do {
            mostrarMenu();

            switch (lerLinha()) {

                case "1":
                    executarOperacaoUnica();
                    break;

                case "2":
                    executarSomaSucessiva();
                    break;

                case "3":
                    System.out.println("Fechando...");
                    continuar = false;
                    break;

                default:
                    System.out.println("Opcao invalida");
                    break;
            }

        } while (continuar);

        // Fecha o leitor do teclado antes de encerrar o programa.
        entrada.close();
    }
}
