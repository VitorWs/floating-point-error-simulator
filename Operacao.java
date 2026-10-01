package nucleo;

/** Operações aritméticas básicas suportadas pelo simulador. */
public enum Operacao {
    SOMA('+'),
    SUBTRACAO('-'),
    MULTIPLICACAO('*'),
    DIVISAO('/');

    private final char simbolo;

    Operacao(char simbolo) {
        this.simbolo = simbolo;
    }

    public char getSimbolo() {
        return simbolo;
    }
}
