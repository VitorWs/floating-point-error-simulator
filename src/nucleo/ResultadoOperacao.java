package nucleo;

import java.math.BigDecimal;

/**
 * Estrutura imutável (record) que funciona como um "pacote de resultados" de uma operação realizada
 * pelo simulador, comparado com o valor exato (calculado em {@code BigDecimal}).
 *
 * Para que serve esta classe?
 * Quando pedimos ao simulador para calcular uma operação (por exemplo, x + y), não queremos
 * receber de volta apenas o número final. Para estudar como o erro surgiu, precisamos saber:
 * - Como os números originais 'x' e 'y' ficaram guardados na máquina após o corte para 'n' dígitos;
 * - Qual foi o resultado aproximado na notação científica normalizada da máquina;
 * - Qual seria o resultado Exato (sem os cortes de 'n' dígitos);
 * - Qual foi o Erro Absoluto (Ea) e o Erro Relativo (Er) dessa conta.
 *
 * Este 'record' reúne todas essas informações em um único objeto organizado para que a interface
 * (seja a tela gráfica ou o terminal de texto) possa montar as tabelas facilmente.
 *
 * @param operacao     Qual foi a operação matemática feita (+, -, *, /).
 * @param x            Primeiro número (operando) já normalizado e cortado em 'n' dígitos significativos.
 * @param y            Segundo número (operando) já normalizado e cortado em 'n' dígitos significativos.
 * @param resultado    Resultado da operação em ponto flutuante simulado (também cortado em 'n' dígitos).
 * @param exato        Valor de referência ("exato"), calculado em {@code BigDecimal} com as entradas originais e convertido para {@code double} para exibição.
 * @param aproximado   Valor numérico (em {@code double}) correspondente ao 'resultado' de precisão finita.
 * @param erroAbsoluto Erro Absoluto da operação: Ea = |exato - aproximado|.
 * @param erroRelativo Erro Relativo da operação: Er = Ea / |aproximado| (ou {@code Double.NaN} se aproximado = 0).
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

    /**
     * Método auxiliar de fábrica que cria um {@code ResultadoOperacao} completo,
     * calculando automaticamente os erros absoluto e relativo em {@code BigDecimal} (sem ruído de ponto
     * flutuante binário) a partir do resultado aproximado e do valor exato informado.
     *
     * @param op        A operação aritmética realizada.
     * @param x         Primeiro operando (já cortado em 'n' dígitos).
     * @param y         Segundo operando (já cortado em 'n' dígitos).
     * @param resultado O resultado obtido na máquina hipotética de 'n' dígitos.
     * @param exato     O valor exato de referência em {@code BigDecimal}.
     * @return Um objeto {@code ResultadoOperacao} preenchido com todos os valores e erros.
     */
    static ResultadoOperacao de(Operacao op, NumeroPontoFlutuante x, NumeroPontoFlutuante y,
                                NumeroPontoFlutuante resultado, BigDecimal exato) {
        // Obtém o valor exato representado pela máquina após o corte em 'n' dígitos.
        BigDecimal aprox = resultado.getValor();

        // Calcula os erros Absoluto e Relativo em BigDecimal para evitar imprecisões do tipo double.
        BigDecimal ea = Erros.absoluto(exato, aprox);
        BigDecimal er = Erros.relativo(exato, aprox);   // retorna null quando aprox == 0 (erro relativo indefinido)

        // Converte os valores finais para double para facilitar a formatação nas tabelas da interface.
        return new ResultadoOperacao(op, x, y, resultado,
                exato.doubleValue(), aprox.doubleValue(), ea.doubleValue(),
                er == null ? Double.NaN : er.doubleValue());
    }
}
