package nucleo;

import java.util.ArrayList;
import java.util.List;

/** Fachada do nucleo: eh por aqui que a CLI deve chamar o backend. */
public final class Simulador {

    private Simulador() {
    }

    /** Executa x (op) y com n digitos e compara com a referencia em double. */
    public static ResultadoOperacao executar(String xTexto, String yTexto, Operacao op,
                                             int n, ModoCorte modo) {
        NumeroPontoFlutuante x = NumeroPontoFlutuante.de(xTexto, n, modo);
        NumeroPontoFlutuante y = NumeroPontoFlutuante.de(yTexto, n, modo);
        NumeroPontoFlutuante r = x.aplicar(op, y);

        double xd = parseDouble(xTexto);
        double yd = parseDouble(yTexto);
        double exato = switch (op) {
            case SOMA -> xd + yd;
            case SUBTRACAO -> xd - yd;
            case MULTIPLICACAO -> xd * yd;
            case DIVISAO -> {
                if (yd == 0.0) {
                    throw new ArithmeticException("Divisão por zero");
                }
                yield xd / yd;
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
        double valorDouble = parseDouble(valorTexto);
        double exato = 0.0;

        List<ResultadoOperacao> passos = new ArrayList<>();
        for (int i = 0; i < vezes; i++) {
            NumeroPontoFlutuante anterior = acumulado;
            acumulado = acumulado.somar(parcela);   // corte a cada passo intermediario
            exato += valorDouble;
            passos.add(ResultadoOperacao.de(Operacao.SOMA, anterior, parcela, acumulado, exato));
        }
        return passos;
    }

    private static double parseDouble(String texto) {
        return Double.parseDouble(texto.trim().replace(',', '.'));
    }
}
