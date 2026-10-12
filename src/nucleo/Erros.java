package nucleo;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Calculo dos erros absoluto e relativo (em BigDecimal, sem ruido de double).
 *
 * @author VitorWs
 */

public final class Erros {
 
    /** Precisao da divisao do erro relativo (34 digitos significativos). */
    private static final MathContext PRECISAO = MathContext.DECIMAL128;
 
    private Erros() {
    }
 
    /** Ea = | Exato - Aproximado | */
    public static BigDecimal absoluto(BigDecimal exato, BigDecimal aproximado) {
        return exato.subtract(aproximado).abs();
    }
 
    /**
     * Er = Ea / | Aproximado |.
     * Retorna null quando o valor aproximado e zero (erro relativo indefinido).
     */
    public static BigDecimal relativo(BigDecimal exato, BigDecimal aproximado) {
        if (aproximado.signum() == 0) {
            return null;
        }
        return absoluto(exato, aproximado).divide(aproximado.abs(), PRECISAO);
    }
}
