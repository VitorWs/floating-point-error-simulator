package nucleo;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Objects;

/**
 * Número de ponto flutuante normalizado com precisão finita:
 * +-0,d1d2...dn x 10^e, com d1 != 0 (exceto o zero).
 *
 * A mantissa é cortada em n dígitos significativos por truncamento ou
 * arredondamento simétrico. Toda operação aritmética é feita sobre os
 * operandos já cortados e o resultado é cortado novamente em n dígitos,
 * como faria uma máquina de precisão finita.
 */
public final class NumeroPontoFlutuante {

    /** Precisão interna usada só para dividir (bem maior que qualquer n razoável). */
    private static final MathContext PRECISAO_DIVISAO = new MathContext(60, RoundingMode.DOWN);

    private final int sinal;          // -1, 0 ou +1
    private final int[] mantissa;     // d1 ... dn
    private final int expoente;       // e
    private final int n;              // dígitos significativos
    private final ModoCorte modo;
    private final BigDecimal valor;   // valor representado (já cortado)

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
    // Construção
    // ------------------------------------------------------------------

    /** Cria a partir de texto (aceita vírgula ou ponto decimal). */
    public static NumeroPontoFlutuante de(String texto, int n, ModoCorte modo) {
        Objects.requireNonNull(texto, "texto");
        return de(new BigDecimal(texto.trim().replace(',', '.')), n, modo);
    }

    public static NumeroPontoFlutuante de(double valor, int n, ModoCorte modo) {
        return de(BigDecimal.valueOf(valor), n, modo);
    }

    /** Normaliza o valor e corta a mantissa em n dígitos conforme o modo. */
    public static NumeroPontoFlutuante de(BigDecimal v, int n, ModoCorte modo) {
        Objects.requireNonNull(v, "valor");
        Objects.requireNonNull(modo, "modo");
        if (n < 1) {
            throw new IllegalArgumentException("n deve ser >= 1");
        }

        if (v.signum() == 0) {
            return new NumeroPontoFlutuante(0, new int[n], 0, n, modo, BigDecimal.ZERO);
        }

        int sinal = v.signum();
        BigDecimal abs = v.abs().stripTrailingZeros();
        String digitos = abs.unscaledValue().toString();

        // 0,d1d2... x 10^e  ->  e = (qtd. de dígitos) - escala
        int e = digitos.length() - abs.scale();

        // Corte: copia os n primeiros dígitos (completa com zeros se faltar)
        int[] d = new int[n];
        for (int i = 0; i < n && i < digitos.length(); i++) {
            d[i] = digitos.charAt(i) - '0';
        }

        // Arredondamento simétrico: olha o dígito d(n+1)
        if (modo == ModoCorte.ARREDONDAMENTO && digitos.length() > n && digitos.charAt(n) >= '5') {
            int i = n - 1;
            while (i >= 0 && d[i] == 9) {   // propagação do "vai-um"
                d[i] = 0;
                i--;
            }
            if (i >= 0) {
                d[i]++;
            } else {                         // 0,999... virou 1,000...
                d[0] = 1;                    // renormaliza: 0,100... x 10^(e+1)
                e++;
            }
        }

        StringBuilder sb = new StringBuilder();
        for (int x : d) {
            sb.append(x);
        }
        BigDecimal valorCortado = new BigDecimal(new BigInteger(sb.toString()))
                .scaleByPowerOfTen(e - n);
        if (sinal < 0) {
            valorCortado = valorCortado.negate();
        }
        return new NumeroPontoFlutuante(sinal, d, e, n, modo, valorCortado);
    }

    // ------------------------------------------------------------------
    // Operadores aritméticos (precisão finita)
    // ------------------------------------------------------------------

    public NumeroPontoFlutuante somar(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        return de(valor.add(o.valor), n, modo);
    }

    public NumeroPontoFlutuante subtrair(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        return de(valor.subtract(o.valor), n, modo);
    }

    public NumeroPontoFlutuante multiplicar(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        return de(valor.multiply(o.valor), n, modo);
    }

    public NumeroPontoFlutuante dividir(NumeroPontoFlutuante o) {
        verificarCompatibilidade(o);
        if (o.valor.signum() == 0) {
            throw new ArithmeticException("Divisão por zero");
        }
        return de(valor.divide(o.valor, PRECISAO_DIVISAO), n, modo);
    }

    public NumeroPontoFlutuante aplicar(Operacao op, NumeroPontoFlutuante o) {
        return switch (op) {
            case SOMA -> somar(o);
            case SUBTRACAO -> subtrair(o);
            case MULTIPLICACAO -> multiplicar(o);
            case DIVISAO -> dividir(o);
        };
    }

    private void verificarCompatibilidade(NumeroPontoFlutuante o) {
        if (o.n != n || o.modo != modo) {
            throw new IllegalArgumentException(
                    "Operandos devem ter o mesmo n e o mesmo modo de corte");
        }
    }

    // ------------------------------------------------------------------
    // Acesso
    // ------------------------------------------------------------------

    public int getSinal() { return sinal; }
    public int[] getMantissa() { return mantissa.clone(); }
    public int getExpoente() { return expoente; }
    public int getN() { return n; }
    public ModoCorte getModo() { return modo; }
    public BigDecimal getValor() { return valor; }
    public double toDouble() { return valor.doubleValue(); }

    /** Ex.: +0,7654 x 10^0 */
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
