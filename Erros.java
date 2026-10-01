package nucleo;

/** Cálculo dos erros absoluto e relativo. */
public final class Erros {

    private Erros() {
    }

    /** Ea = | Exato - Aproximado | */
    public static double absoluto(double exato, double aproximado) {
        return Math.abs(exato - aproximado);
    }

    /**
     * Er = Ea / | Exato |.
     * Retorna NaN quando o valor exato é zero (erro relativo indefinido).
     */
    public static double relativo(double exato, double aproximado) {
        if (exato == 0.0) {
            return Double.NaN;
        }
        return absoluto(exato, aproximado) / Math.abs(exato);
    }
}
