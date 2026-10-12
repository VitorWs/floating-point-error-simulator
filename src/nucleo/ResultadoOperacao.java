package nucleo;

import java.math.BigDecimal;

/**
 * Resultado de uma operacao simulada, comparado com o valor exato (BigDecimal).
 *
 * @param x          primeiro operando (ja cortado em n digitos)
 * @param y          segundo operando (ja cortado em n digitos)
 * @param resultado  resultado em precisao finita
 * @param exato      valor de referencia calculado em BigDecimal com as entradas originais
 * @param aproximado resultado em precisao finita convertido para double
 * @param erroAbsoluto Ea = |exato - aproximado|
 * @param erroRelativo Er = Ea / |aproximado| (NaN se aproximado = 0)
 *
 * @author VitorWs
 */
public record ResultadoOperacao(
        Operacao operacao,
        NumeroPontoFlutuante x,
        NumeroPontoFlutuante y,
        NumeroPontoFlutuante resultado,
        double exato,
        double aproximado,
        double erroAbsoluto,
        double erroRelativo) {
 
    static ResultadoOperacao de(Operacao op, NumeroPontoFlutuante x, NumeroPontoFlutuante y,
                                NumeroPontoFlutuante resultado, BigDecimal exato) {
        BigDecimal aprox = resultado.getValor();
        BigDecimal ea = Erros.absoluto(exato, aprox);
        BigDecimal er = Erros.relativo(exato, aprox);   // null = indefinido
        return new ResultadoOperacao(op, x, y, resultado,
                exato.doubleValue(), aprox.doubleValue(), ea.doubleValue(),
                er == null ? Double.NaN : er.doubleValue());
    }
}
