package nucleo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Fachada do nucleo: eh por aqui que a CLI deve chamar o backend.
 *
 * @author VitorWs
 */

public final class Simulador {
 
    private static final MathContext PRECISAO_EXATA = MathContext.DECIMAL128;
 
    private Simulador() {
    }
 
    /** Executa x (op) y com n digitos e compara com o valor exato (BigDecimal). */
    public static ResultadoOperacao executar(String xTexto, String yTexto, Operacao op,
                                             int n, ModoCorte modo) {
        NumeroPontoFlutuante x = NumeroPontoFlutuante.de(xTexto, n, modo);
        NumeroPontoFlutuante y = NumeroPontoFlutuante.de(yTexto, n, modo);
        NumeroPontoFlutuante r = x.aplicar(op, y);
 
        BigDecimal xe = paraBigDecimal(xTexto);
        BigDecimal ye = paraBigDecimal(yTexto);
        BigDecimal exato = switch (op) {
            case SOMA -> xe.add(ye);
            case SUBTRACAO -> xe.subtract(ye);
            case MULTIPLICACAO -> xe.multiply(ye);
            case DIVISAO -> {
                if (ye.signum() == 0) {
                    throw new ArithmeticException("Divisão por zero");
                }
                yield xe.divide(ye, PRECISAO_EXATA);
            }
        };
        return ResultadoOperacao.de(op, x, y, r, exato);
    }
 
    /**
     * Soma 'vezes' vezes o mesmo valor, cortando o acumulado a cada passo.
     * Retorna um resultado por passo (para tabular o acumulo de erro).
     */
    public static List<ResultadoOperacao> somaSucessiva(String valorTexto, int vezes,
                                                        int n, ModoCorte modo) {
        if (vezes < 1) {
            throw new IllegalArgumentException("vezes deve ser >= 1");
        }
        NumeroPontoFlutuante parcela = NumeroPontoFlutuante.de(valorTexto, n, modo);
        NumeroPontoFlutuante acumulado = NumeroPontoFlutuante.de(0, n, modo);
        BigDecimal valorExato = paraBigDecimal(valorTexto);
        BigDecimal exato = BigDecimal.ZERO;
 
        List<ResultadoOperacao> passos = new ArrayList<>();
        for (int i = 0; i < vezes; i++) {
            NumeroPontoFlutuante anterior = acumulado;
            acumulado = acumulado.somar(parcela);   // corte a cada passo intermediario
            exato = exato.add(valorExato);
            passos.add(ResultadoOperacao.de(Operacao.SOMA, anterior, parcela, acumulado, exato));
        }
        return passos;
    }
 
    private static BigDecimal paraBigDecimal(String texto) {
        return new BigDecimal(texto.trim().replace(',', '.'));
    }
}
