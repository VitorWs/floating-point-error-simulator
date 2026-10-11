package nucleo;

/**
 * Classe utilitaria para calculo de erros absolutos e relativos.
 *
 * @author VitorWs
 */

public final class Erros {

    private Erros() {
    }

    /** Ea = | Exato - Aproximado | */
    public static double absoluto(double exato, double aproximado) {
        return Math.abs(exato - aproximado);
    }

    /**
     * Er = Ea / | Aproximado |.
     * Retorna NaN quando o valor aproximado e zero (erro relativo indefinido).
     */
    public static double relativo(double exato, double aproximado) {
        if (aproximado == 0.0) {
            return Double.NaN;
        }
        return absoluto(exato, aproximado) / Math.abs(aproximado);
    }
}
