package nucleo;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.List;

/**
 * Classe "Fachada" (ponto de entrada principal) do núcleo de cálculo do simulador.
 *
 * Para que serve esta classe?
 * As interfaces com o usuário (tanto a janela gráfica quanto o menu de linha de comando)
 * não precisam saber como funciona a matemática interna de normalizar números ou calcular
 * erros passo a passo. Elas apenas chamam os dois métodos principais desta classe:
 *
 * 1) {@code executar(...)}: faz uma conta única (x + y, x - y, x * y ou x / y) simulando
 *    uma máquina de 'n' dígitos e já compara com o resultado exato (em {@code BigDecimal}).
 * 2) {@code somaSucessiva(...)}: soma um mesmo número várias vezes seguidas, cortando o
 *    resultado em 'n' dígitos a cada passo, para demonstrar como pequenos erros se acumulam
 *    ao longo do tempo (propagação de erros).
 *
 * @author VitorWs
 */
public final class Simulador {

    /**
     * Precisão usada para calcular o valor "exato" de referência nas divisões (DECIMAL128 = 34 dígitos),
     * evitando erro de dízima infinita no BigDecimal.
     */
    private static final MathContext PRECISAO_EXATA = MathContext.DECIMAL128;

    /**
     * Construtor privado para evitar instanciação desnecessária, pois todos os métodos são estáticos.
     */
    private Simulador() {
    }

    /**
     * Realiza uma operação aritmética única entre dois números {@code x} e {@code y}, mantendo
     * apenas {@code n} dígitos significativos (por truncamento ou arredondamento), e compara
     * o resultado obtido com o valor exato (calculado com {@code BigDecimal}).
     *
     * Como funciona o processo:
     * 1. Primeiro, {@code x} e {@code y} são armazenados na máquina hipotética com {@code n} dígitos;
     * 2. Depois, a operação escolhida (+, -, *, /) é realizada entre esses dois números já cortados,
     *    e o resultado também é cortado em {@code n} dígitos;
     * 3. Em paralelo, calculamos o valor "exato" de referência usando {@code BigDecimal} direto a partir
     *    dos textos originais digitados (assim o erro medido vem apenas do corte em {@code n} dígitos,
     *    e não das limitações binárias do tipo {@code double});
     * 4. Por fim, calculamos os erros absoluto (Ea) e relativo (Er) entre o valor exato e o aproximado.
     *
     * @param xTexto Texto digitado pelo usuário para o primeiro número (ex: "0,76545").
     * @param yTexto Texto digitado pelo usuário para o segundo número (ex: "0,76541").
     * @param op     Operação aritmética desejada (SOMA, SUBTRACAO, MULTIPLICACAO ou DIVISAO).
     * @param n      Quantidade de dígitos significativos a manter na máquina simulada.
     * @param modo   Regra de corte (TRUNCAMENTO ou ARREDONDAMENTO).
     * @return Objeto {@link ResultadoOperacao} contendo operandos cortados, resultado aproximado, valor exato e erros.
     */
    public static ResultadoOperacao executar(String xTexto, String yTexto, Operacao op,
                                             int n, ModoCorte modo) {
        // 1) Armazena os números 'x' e 'y' na máquina simulada (normalizando e cortando para 'n' dígitos).
        NumeroPontoFlutuante x = NumeroPontoFlutuante.de(xTexto, n, modo);
        NumeroPontoFlutuante y = NumeroPontoFlutuante.de(yTexto, n, modo);

        // 2) Executa a operação matemática na máquina simulada (o resultado 'r' também sai com 'n' dígitos).
        NumeroPontoFlutuante r = x.aplicar(op, y);

        // 3) Calcula o valor "exato" de referência usando BigDecimal sobre os textos originais antes do corte.
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

        // 4) Empacota tudo e já calcula o Erro Absoluto e o Erro Relativo.
        return ResultadoOperacao.de(op, x, y, r, exato);
    }

    /**
     * Simula a propagação e o acúmulo de erros em uma sequência de somas repetidas
     * (como pedido no Exemplo 3 do projeto: somar 0,56786 dez vezes seguidas).
     *
     * Por que o erro cresce a cada passo?
     * Porque, em uma máquina de precisão limitada, a cada nova soma realizada o resultado
     * intermediário (acumulado) precisa ser cortado novamente para caber nos {@code n} dígitos.
     * Assim, o pequeno erro cometido na 1ª soma entra na 2ª soma, soma-se a um novo erro de corte,
     * entra na 3ª soma, e assim por diante — virando uma "bola de neve" (especialmente no truncamento,
     * onde todos os cortes perdem valor na mesma direção).
     *
     * @param valorTexto Número que será somado repetidamente (em formato de texto, ex: "0,56786").
     * @param vezes      Quantidade de vezes que o número será somado a si mesmo (ex: 10 vezes).
     * @param n          Quantidade de dígitos significativos mantidos em cada passo.
     * @param modo       Regra de corte usada a cada soma (TRUNCAMENTO ou ARREDONDAMENTO).
     * @return Lista com o resultado e os erros de cada passo (do passo 1 até o último), ideal para montar tabelas.
     */
    public static List<ResultadoOperacao> somaSucessiva(String valorTexto, int vezes,
                                                        int n, ModoCorte modo) {
        if (vezes < 1) {
            throw new IllegalArgumentException("vezes deve ser >= 1");
        }

        // Converte a parcela que será somada para a representação de 'n' dígitos da máquina.
        NumeroPontoFlutuante parcela = NumeroPontoFlutuante.de(valorTexto, n, modo);

        // O acumulador da máquina começa em zero (0,000... x 10^0).
        NumeroPontoFlutuante acumulado = NumeroPontoFlutuante.de(0, n, modo);

        // Também guardamos o valor exato em BigDecimal para acompanhar qual seria a soma exata sem cortes.
        BigDecimal valorExato = paraBigDecimal(valorTexto);
        BigDecimal exato = BigDecimal.ZERO;

        // Lista que vai guardar a "fotografia" de cada passo da soma para exibirmos na tabela.
        List<ResultadoOperacao> passos = new ArrayList<>();
        for (int i = 0; i < vezes; i++) {
            NumeroPontoFlutuante anterior = acumulado;

            // Soma o valor acumulado até agora com a nova parcela e corta o resultado a cada passo intermediário!
            acumulado = acumulado.somar(parcela);

            // Atualiza a soma exata de referência.
            exato = exato.add(valorExato);

            // Registra o estado deste passo (acumulado aproximado vs. exato, Ea e Er).
            passos.add(ResultadoOperacao.de(Operacao.SOMA, anterior, parcela, acumulado, exato));
        }
        return passos;
    }

    /**
     * Converte um texto numérico digitado pelo usuário (com vírgula ou ponto) para {@code BigDecimal} exato.
     */
    private static BigDecimal paraBigDecimal(String texto) {
        return new BigDecimal(texto.trim().replace(',', '.'));
    }
}
