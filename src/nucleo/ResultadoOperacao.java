package nucleo;

/**
 * Resultado de uma operacao simulada, comparado com a referencia em double.
 *
 * @param x          primeiro operando (ja cortado em n digitos)
 * @param y          segundo operando (ja cortado em n digitos)
 * @param resultado  resultado em precisao finita
 * @param exato      valor de referencia calculado em double com as entradas originais
 * @param aproximado resultado em precisao finita convertido para double
 * @param erroAbsoluto Ea = |exato - aproximado|
 * @param erroRelativo Er = Ea / |exato| (NaN se exato = 0)
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
                                NumeroPontoFlutuante resultado, double exato) {
        double aprox = resultado.toDouble();
        return new ResultadoOperacao(op, x, y, resultado, exato, aprox,
                Erros.absoluto(exato, aprox), Erros.relativo(exato, aprox));
    }
}
