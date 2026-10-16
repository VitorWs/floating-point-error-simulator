package nucleo;

/**
 * Enumeração que representa as quatro operações aritméticas básicas que o simulador
 * é capaz de realizar: Soma (+), Subtração (-), Multiplicação (*) e Divisão (/).
 *
 * Por que usar um 'enum' em vez de um caractere solto ('+', '-', etc.)?
 * Usar uma enumeração evita erros de digitação no código e garante que apenas essas
 * quatro operações válidas possam ser enviadas para o motor de cálculo (o núcleo).
 * Além disso, cada operação já guarda consigo o símbolo matemático correspondente
 * para facilitar a leitura e a exibição na tela.
 *
 * @author VitorWs
 */
public enum Operacao {

    /** Adição de dois números: x + y */
    SOMA('+'),

    /** Subtração entre dois números: x - y (importante para estudar o cancelamento subtrativo) */
    SUBTRACAO('-'),

    /** Multiplicação de dois números: x * y */
    MULTIPLICACAO('*'),

    /** Divisão entre dois números: x / y (não permite divisor zero) */
    DIVISAO('/');

    /** Caractere matemático que representa visualmente a operação (+, -, * ou /). */
    private final char simbolo;

    /**
     * Construtor interno da enumeração que vincula cada operação ao seu símbolo.
     *
     * @param simbolo Caractere da operação matemática.
     */
    Operacao(char simbolo) {
        this.simbolo = simbolo;
    }

    /**
     * Retorna o símbolo matemático da operação ('+', '-', '*' ou '/'),
     * útil para exibir nas telas e comparar com o que o usuário digitou.
     *
     * @return O caractere correspondente à operação.
     */
    public char getSimbolo() {
        return simbolo;
    }
}
