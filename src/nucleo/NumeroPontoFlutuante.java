package nucleo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Classe principal do núcleo: representa um número em ponto flutuante normalizado com precisão
 * finita controlada de 'n' dígitos significativos, funcionando exatamente como a memória de uma
 * calculadora ou computador limitado.
 *
 * O que é a representação normalizada?
 * Qualquer número real (exceto o zero) pode ser escrito no formato científico padronizado:
 *    +- 0,d1 d2 d3 ... dn x 10^e
 * onde:
 * - O primeiro dígito depois da vírgula (d1) é obrigatoriamente diferente de zero (entre 1 e 9);
 * - Os dígitos "d1 d2 ... dn" formam a chamada MANTISSA (os 'n' dígitos significativos guardados);
 * - O número 'e' é o EXPOENTE na base 10 (indica quantas casas a vírgula andou).
 *
 * Exemplo prático:
 * O número 123,45 escrito na forma normalizada vira +0,12345 x 10^3 (pois 0,12345 * 1000 = 123,45).
 * Se a nossa máquina hipotética só puder guardar n = 4 dígitos significativos:
 * - Por TRUNCAMENTO, ela guarda:    +0,1234 x 10^3 (que vale 123,4)
 * - Por ARREDONDAMENTO, ela guarda: +0,1235 x 10^3 (que vale 123,5, pois o 5º dígito era 5)
 *
 * Como as contas são feitas?
 * Toda operação aritmética (+, -, *, /) é realizada sobre os números já cortados em 'n' dígitos,
 * e o resultado final da conta passa novamente pelo corte para 'n' dígitos — simulando fielmente
 * o que acontece dentro de um processador real a cada instrução matemática.
 *
 * @author VitorWs
 */
public final class NumeroPontoFlutuante {

    /**
     * Precisão interna altíssima (60 dígitos) usada apenas durante a operação de divisão exata
     * intermediária, antes de cortarmos o resultado final para os 'n' dígitos do usuário.
     * Isso é necessário porque divisões como 1 / 3 geram dízimas infinitas (0,333333...),
     * e o BigDecimal do Java precisa saber até quantas casas calcular antes do nosso corte.
     */
    private static final MathContext PRECISAO_DIVISAO = new MathContext(60, RoundingMode.DOWN);

    /** Sinal do número: -1 para negativo, 0 para zero, ou +1 para positivo. */
    private final int sinal;

    /** Vetor com os 'n' dígitos significativos da mantissa: d1, d2, ..., dn (cada um de 0 a 9). */
    private final int[] mantissa;

    /** Expoente 'e' da base 10 na forma normalizada (0,d1d2...dn x 10^e). */
    private final int expoente;

    /** Quantidade máxima de dígitos significativos que esta máquina hipotética consegue guardar. */
    private final int n;

    /** Regra de corte escolhida (TRUNCAMENTO ou ARREDONDAMENTO). */
    private final ModoCorte modo;

    /** Valor numérico representado (já cortado), guardado em BigDecimal para evitar problemas de arredondamento binário do double. */
    private final BigDecimal valor;

    /**
     * Construtor interno privado. Para criar um número, usam-se os métodos estáticos {@code de(...)},
     * que fazem toda a normalização e o corte antes de montar o objeto.
     */
    private NumeroPontoFlutuante(int sinal, int[] mantissa, int expoente, int n,
                                 ModoCorte modo, BigDecimal valor) {
        this.sinal = sinal;
        this.mantissa = mantissa;
        this.expoente = expoente;
        this.n = n;
        this.modo = modo;
        this.valor = valor;
    }

    // ------------------------------------------------------------------
    // Construção, Normalização e Corte
    // ------------------------------------------------------------------

    /**
     * Cria um número em ponto flutuante de precisão finita a partir de um texto digitado pelo usuário.
     * Aceita tanto vírgula quanto ponto decimal (ex: "0,76545" ou "0.76545").
     *
     * Por que criar a partir de String (texto) em vez de 'double'?
     * Porque o tipo 'double' do computador usa base binária (base 2), onde números simples como
     * 0,1 viram dízimas binárias aproximadas (0.10000000000000000555...). Lendo direto do texto
     * para {@code BigDecimal}, garantimos que o número decimal digitado pelo usuário entre puro,
     * sem nenhuma distorção antes do nosso corte.
     *
     * @param texto Número em formato de texto (ex: "123,45").
     * @param n     Quantidade de dígitos significativos a manter.
     * @param modo  Método de ajuste de precisão (TRUNCAMENTO ou ARREDONDAMENTO).
     * @return O número já normalizado e cortado em 'n' dígitos.
     */
    public static NumeroPontoFlutuante de(String texto, int n, ModoCorte modo) {
        Objects.requireNonNull(texto, "texto");
        // Troca vírgula por ponto (padrão que o BigDecimal entende) e remove espaços extras.
        return de(new BigDecimal(texto.trim().replace(',', '.')), n, modo);
    }

    /**
     * Cria um número em ponto flutuante de precisão finita a partir de um valor {@code double}.
     *
     * @param valor Número em formato double.
     * @param n     Quantidade de dígitos significativos a manter.
     * @param modo  Método de ajuste de precisão (TRUNCAMENTO ou ARREDONDAMENTO).
     * @return O número já normalizado e cortado em 'n' dígitos.
     */
    public static NumeroPontoFlutuante de(double valor, int n, ModoCorte modo) {
        return de(BigDecimal.valueOf(valor), n, modo);
    }

    /**
     * Coração do simulador: recebe um valor decimal exato ({@code BigDecimal}), coloca-o na
     * notação científica normalizada (0,d1d2... x 10^e) e aplica o corte para manter apenas
     * 'n' dígitos significativos, usando Truncamento ou Arredondamento Simétrico.
     *
     * @param v    Valor exato de entrada.
     * @param n    Número de dígitos significativos a serem mantidos (deve ser >= 1).
     * @param modo Estratégia de corte (TRUNCAMENTO ou ARREDONDAMENTO).
     * @return Objeto {@code NumeroPontoFlutuante} representando o número armazenado na máquina.
     */
    public static NumeroPontoFlutuante de(BigDecimal v, int n, ModoCorte modo) {
        Objects.requireNonNull(v, "valor");
        Objects.requireNonNull(modo, "modo");
        if (n < 1) {
            throw new IllegalArgumentException("n deve ser >= 1");
        }

        // Caso especial: se o número for zero (0), a mantissa é toda de zeros (0,000... x 10^0).
        if (v.signum() == 0) {
            return new NumeroPontoFlutuante(0, new int[n], 0, n, modo, BigDecimal.ZERO);
        }

        // 1º PASSO: Separar o sinal (+1 ou -1) e trabalhar apenas com o valor positivo (absoluto).
        // Fazemos isso para que o arredondamento seja SIMÉTRICO (ou seja, -0,76545 arredonda
        // da mesma forma que +0,76545, apenas devolvendo o sinal de menos no final).
        int sinal = v.signum();
        BigDecimal abs = v.abs().stripTrailingZeros(); // remove o sinal e zeros à direita para normalizar

        // Extrai apenas a sequência pura de dígitos significativos sem vírgula e sem zeros à esquerda.
        // Exemplo: se abs = 0,0012345, então digitos = "12345" e abs.scale() = 7.
        // Exemplo: se abs = 123,45,    então digitos = "12345" e abs.scale() = 2.
        String digitos = abs.unscaledValue().toString();

        // 2º PASSO: Calcular o expoente 'e' da forma normalizada 0,d1d2... x 10^e.
        // A fórmula matemática é: e = (quantidade de dígitos) - (escala de casas decimais).
        // Testando nos exemplos acima:
        // - Para 123,45:    5 dígitos - escala 2 = expoente 3  -> 0,12345 x 10^3.
        // - Para 0,76545:   5 dígitos - escala 5 = expoente 0  -> 0,76545 x 10^0.
        // - Para 0,0012345: 5 dígitos - escala 7 = expoente -2 -> 0,12345 x 10^-2.
        int e = digitos.length() - abs.scale();

        // 3º PASSO: Aplicar o TRUNCAMENTO inicial.
        // Criamos uma gaveta (vetor 'd') com espaço para exatamente 'n' dígitos e copiamos
        // apenas os 'n' primeiros dígitos do número. Se o número original tiver menos que 'n'
        // dígitos (ex: 1,5 com n = 4), as posições restantes do vetor já ficam com zero (1500).
        int[] d = new int[n];
        for (int i = 0; i < n && i < digitos.length(); i++) {
            // Subtrair o caractere '0' converte o caractere numérico (ex: '7') no número inteiro 7.
            d[i] = digitos.charAt(i) - '0';
        }

        // 4º PASSO: Se o modo for ARREDONDAMENTO SIMÉTRICO, verificamos o primeiro dígito descartado.
        // O primeiro dígito que ficou de fora está na posição 'n' da string 'digitos' (pois começa em 0).
        // Se ele existir e for maior ou igual a '5', precisamos somar 1 ao último dígito guardado!
        if (modo == ModoCorte.ARREDONDAMENTO && digitos.length() > n && digitos.charAt(n) >= '5') {
            int i = n - 1; // Começa olhando para o último dígito guardado na mantissa.

            // Efeito "vai-um" (carry): se o último dígito for 9, ao somar 1 ele vira 0 e passa +1
            // para o dígito da esquerda, repetindo enquanto houver noves seguidos.
            // Exemplo: arredondar 0,12996 para 4 dígitos -> os dois '9' viram '0' e o '2' vira '3' (0,1300).
            while (i >= 0 && d[i] == 9) {
                d[i] = 0;
                i--;
            }

            if (i >= 0) {
                // Caso normal: incrementa o dígito que não era 9.
                d[i]++;
            } else {
                // Caso extremo de transbordo: todos os dígitos eram 9 (ex: 0,99996 com n = 4)!
                // Todos os dígitos viraram 0, e o "vai-um" escapou pela esquerda, formando 1,0000 x 10^e.
                // Para manter o formato normalizado (0,d1d2...), renormalizamos para 0,1000 x 10^(e + 1).
                d[0] = 1;
                e++;
            }
        }

        // 5º PASSO: Reconstruir o valor numérico (BigDecimal) a partir dos 'n' dígitos já cortados.
        // Juntamos os 'n' dígitos como um número inteiro (ex: [7, 6, 5, 4] -> 7654) e deslocamos
        // a vírgula por 10^(e - n) para obter o valor final exato representado na máquina (0,7654).
        StringBuilder sb = new StringBuilder();
        for (int x : d) {
            sb.append(x);
        }
        BigDecimal valorCortado = new BigDecimal(new BigInteger(sb.toString()))
                .scaleByPowerOfTen(e - n);          // faz 0,d1d2...dn x 10^e

        // Devolve o sinal negativo, caso o número original fosse menor que zero.
        if (sinal < 0) {
            valorCortado = valorCortado.negate();   // restaura o sinal
        }

        return new NumeroPontoFlutuante(sinal, d, e, n, modo, valorCortado);
    }

    // ------------------------------------------------------------------
    // Operadores Aritméticos (em Precisão Finita)
    // ------------------------------------------------------------------

    /**
     * Soma este número com outro número em ponto flutuante (ambos já cortados em 'n' dígitos)
     * e corta o resultado novamente para 'n' dígitos significativos.
     *
     * @param o Segundo operando da soma.
     * @return Resultado da soma normalizado e cortado em 'n' dígitos.
     */
    public NumeroPontoFlutuante somar(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        return de(valor.add(o.valor), n, modo);
    }

    /**
     * Subtrai outro número deste número (ambos já cortados em 'n' dígitos) e corta o resultado
     * novamente para 'n' dígitos significativos.
     * Quando os dois números são muito parecidos, ocorre aqui o famoso "Cancelamento Subtrativo".
     *
     * @param o Segundo operando (subtraendo).
     * @return Resultado da subtração normalizado e cortado em 'n' dígitos.
     */
    public NumeroPontoFlutuante subtrair(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        return de(valor.subtract(o.valor), n, modo);
    }

    /**
     * Multiplica este número por outro (ambos já cortados em 'n' dígitos) e corta o produto
     * resultante para 'n' dígitos significativos.
     *
     * @param o Segundo operando (multiplicador).
     * @return Resultado da multiplicação normalizado e cortado em 'n' dígitos.
     */
    public NumeroPontoFlutuante multiplicar(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        return de(valor.multiply(o.valor), n, modo);
    }

    /**
     * Divide este número por outro (ambos já cortados em 'n' dígitos) e corta o quociente
     * resultante para 'n' dígitos significativos.
     *
     * @param o Divisor (não pode ser zero).
     * @return Resultado da divisão normalizado e cortado em 'n' dígitos.
     * @throws ArithmeticException Se o divisor for igual a zero.
     */
    public NumeroPontoFlutuante dividir(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        if (o.valor.signum() == 0) {
            throw new ArithmeticException("Divisão por zero");
        }
        // Faz a divisão com 60 dígitos de precisão intermediária e em seguida corta para 'n' dígitos.
        return de(valor.divide(o.valor, PRECISAO_DIVISAO), n, modo);
    }

    /**
     * Método prático que recebe qual operação deve ser feita (+, -, * ou /) e chama
     * automaticamente a função correspondente ({@code somar}, {@code subtrair}, etc.).
     *
     * @param op Operação aritmética desejada.
     * @param o  Segundo operando da conta.
     * @return Resultado da operação cortado em 'n' dígitos.
     */
    public NumeroPontoFlutuante aplicar(Operacao op, NumeroPontoFlutuante o) {
        return switch (op) {
            case SOMA -> somar(o);
            case SUBTRACAO -> subtrair(o);
            case MULTIPLICACAO -> multiplicar(o);
            case DIVISAO -> dividir(o);
        };
    }

    /**
     * Garante que não estamos tentando misturar na mesma conta números de máquinas diferentes
     * (por exemplo, um número de 4 dígitos com outro de 6 dígitos, ou um truncado com um arredondado).
     */
    private void verificarCompatibilidade(NumeroPontoFlutuante o) {
        if (o.n != n || o.modo != modo) {
            throw new IllegalArgumentException(
                    "Operandos devem ter o mesmo n e o mesmo modo de corte");
        }
    }

    // ------------------------------------------------------------------
    // Métodos de Acesso (Getters) e Formatação
    // ------------------------------------------------------------------

    /** @return O sinal do número (-1, 0 ou +1). */
    public int getSinal() { return sinal; }

    /** @return Uma cópia dos 'n' dígitos da mantissa (copiado com clone() para ninguém alterar por fora). */
    public int[] getMantissa() { return mantissa.clone(); }

    /** @return O expoente 'e' da base 10. */
    public int getExpoente() { return expoente; }

    /** @return A quantidade 'n' de dígitos significativos configurada. */
    public int getN() { return n; }

    /** @return O modo de corte (TRUNCAMENTO ou ARREDONDAMENTO) utilizado. */
    public ModoCorte getModo() { return modo; }

    /** @return O valor numérico exato (após o corte) em formato {@code BigDecimal}. */
    public BigDecimal getValor() { return valor; }

    /** @return O valor numérico convertido para {@code double}. */
    public double toDouble() { return valor.doubleValue(); }

    /**
     * Devolve a representação textual do número no formato científico normalizado exigido
     * pelo trabalho, por exemplo: {@code +0,7654 x 10^0} ou {@code -0,1234 x 10^3}.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(sinal < 0 ? "-" : "+").append("0,");
        for (int d : mantissa) {
            sb.append(d);
        }
        return sb.append(" x 10^").append(expoente).toString();
    }
}
