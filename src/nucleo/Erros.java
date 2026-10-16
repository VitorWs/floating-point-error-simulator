package nucleo;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Classe utilitária responsável por medir o tamanho do erro gerado pelas aproximações
 * da nossa calculadora de precisão limitada (usando {@code BigDecimal} para evitar ruído binário de {@code double}).
 *
 * Quando fazemos uma conta com poucos dígitos significativos (o "valor aproximado")
 * e comparamos com o resultado real da conta (o "valor exato"), quase sempre existe
 * uma diferença. Em Cálculo Numérico, medimos essa diferença de duas maneiras:
 *
 * 1) ERRO ABSOLUTO (Ea):
 *    É simplesmente a distância bruta (sem sinal negativo) entre o valor exato e o aproximado.
 *    Fórmula: Ea = |Valor Exato - Valor Aproximado|
 *    Limitação do Erro Absoluto: Sozinho, ele não diz se o erro foi grave ou pequeno!
 *    Por exemplo, errar por 1 centímetro ao medir a distância entre duas cidades é irrelevante,
 *    mas errar por 1 centímetro em uma cirurgia no olho é gravíssimo.
 *
 * 2) ERRO RELATIVO (Er):
 *    Para saber o peso real do erro em proporção ao tamanho do resultado obtido, dividimos
 *    o Erro Absoluto pelo módulo do Valor Aproximado (conforme especificado no projeto).
 *    Fórmula: Er = |Valor Exato - Valor Aproximado| / |Valor Aproximado|
 *    (Se multiplicarmos por 100, temos o erro em porcentagem: por exemplo, Er = 0,6 equivale a 60% de erro).
 *
 * @author VitorWs
 */
public final class Erros {

    /**
     * Precisão usada na divisão do cálculo do erro relativo (DECIMAL128 = 34 dígitos significativos),
     * necessária caso a divisão Ea / |Aproximado| gere uma dízima periódica.
     */
    private static final MathContext PRECISAO = MathContext.DECIMAL128;

    /**
     * Construtor privado para impedir que alguém crie objetos desta classe ("new Erros()"),
     * já que ela serve apenas para agrupar funções matemáticas estáticas.
     */
    private Erros() {
    }

    /**
     * Calcula o Erro Absoluto (Ea) entre o valor exato e o valor aproximado em precisão decimal exata.
     *
     * Fórmula: Ea = |exato - aproximado|
     * Usamos {@code .abs()} (módulo/valor absoluto) porque o erro mede apenas a distância
     * entre os dois valores, sendo sempre um número positivo ou zero.
     *
     * @param exato      Valor de referência (calculado em {@code BigDecimal} a partir das entradas originais).
     * @param aproximado Valor obtido pelo simulador após aplicar o corte de 'n' dígitos.
     * @return O erro absoluto em {@code BigDecimal} (sempre >= 0).
     */
    public static BigDecimal absoluto(BigDecimal exato, BigDecimal aproximado) {
        return exato.subtract(aproximado).abs();
    }

    /**
     * Calcula o Erro Relativo (Er), que mostra o tamanho do erro em proporção ao valor aproximado.
     *
     * Fórmula: Er = Ea / |aproximado|
     * Atenção: Como não existe divisão por zero na matemática, se o valor aproximado der 0
     * (o que acontece, por exemplo, quando dois números muito próximos viram iguais após o corte
     * e são subtraídos), o método retorna {@code null} para indicar que o erro relativo é indefinido.
     *
     * @param exato      Valor de referência (em {@code BigDecimal}).
     * @param aproximado Valor calculado pelo simulador com precisão limitada.
     * @return O erro relativo em {@code BigDecimal}, ou {@code null} caso o valor aproximado seja zero.
     */
    public static BigDecimal relativo(BigDecimal exato, BigDecimal aproximado) {
        // Se o resultado aproximado for zero, retorna null (que depois é exibido como "Indefinido" na tela).
        if (aproximado.signum() == 0) {
            return null;
        }
        // Divide o erro absoluto pelo tamanho (módulo) do valor aproximado com 34 dígitos de precisão.
        return absoluto(exato, aproximado).divide(aproximado.abs(), PRECISAO);
    }
}
