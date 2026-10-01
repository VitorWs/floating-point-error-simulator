package nucleo;

/**
 * Resultado de uma operação simulada, comparado com a referência em double.
 *
 * @param x          primeiro operando (já cortado em n dígitos)
 * @param y          segundo operando (já cortado em n dígitos)
 * @param resultado  resultado em precisão finita
 * @param exato      valor de referência calculado em double com as entradas originais
 * @param aproximado resultado em precisão finita convertido para double
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
