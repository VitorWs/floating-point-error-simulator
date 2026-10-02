package nucleo;

import java.util.List;

/** Verificacao rapida do nucleo com os casos do edital (t = 4). Nao e a CLI final. */
public class DemoNucleo {

    public static void main(String[] args) {
        int t = 4;

        System.out.println("=== Exemplo 2: cancelamento subtrativo (0,76545 - 0,76541) ===");
        for (ModoCorte m : ModoCorte.values()) {
            imprimir(m, Simulador.executar("0,76545", "0,76541", Operacao.SUBTRACAO, t, m));
        }

        System.out.println("\n=== Exemplo 3: 10 somas de 0,56786 ===");
        for (ModoCorte m : ModoCorte.values()) {
            System.out.println("-- " + m.getDescricao());
            List<ResultadoOperacao> passos = Simulador.somaSucessiva("0,56786", 10, t, m);
            for (int i = 0; i < passos.size(); i++) {
                ResultadoOperacao r = passos.get(i);
                System.out.printf("passo %2d: %s | exato=%.6f aprox=%.6f Ea=%.6f Er=%.4f%%%n",
                        i + 1, r.resultado(), r.exato(), r.aproximado(),
                        r.erroAbsoluto(), r.erroRelativo() * 100);
            }
        }

        System.out.println("\n=== Casos de borda (vai-um) ===");
        System.out.println(NumeroPontoFlutuante.de("0,99996", 4, ModoCorte.ARREDONDAMENTO));
        System.out.println(NumeroPontoFlutuante.de("129,96", 4, ModoCorte.ARREDONDAMENTO));
        System.out.println(NumeroPontoFlutuante.de("-0,00123456", 4, ModoCorte.TRUNCAMENTO));
        System.out.println(NumeroPontoFlutuante.de("0", 4, ModoCorte.TRUNCAMENTO));
        System.out.println(NumeroPontoFlutuante.de("1500", 4, ModoCorte.ARREDONDAMENTO));
    }

    private static void imprimir(ModoCorte m, ResultadoOperacao r) {
        System.out.printf("%s: x=%s y=%s -> %s | exato=%.8f aprox=%.8f Ea=%.8f Er=%.2f%%%n",
                m.getDescricao(), r.x(), r.y(), r.resultado(),
                r.exato(), r.aproximado(), r.erroAbsoluto(), r.erroRelativo() * 100);
    }
}
