package nucleo;

import java.util.List;

/**
 * Classe de demonstração e teste rápido do pacote {@code nucleo}.
 *
 * Para que serve esta classe?
 * Ela não é a interface final do usuário (que fica em {@code gui.Janela} e {@code commandLineInteface.Cli}),
 * mas sim um roteiro automático criado durante o desenvolvimento do núcleo para testar e validar
 * rapidamente no console se as contas exigidas no documento da disciplina estão batendo com a teoria:
 * - Exemplo 2 do projeto: Cancelamento Subtrativo (0,76545 - 0,76541 com t = 4 dígitos);
 * - Exemplo 3 do projeto: Propagação de Erro em 10 somas seguidas de 0,56786 com t = 4 dígitos;
 * - Casos especiais de "vai-um" (quando dígitos 9 são arredondados para cima e mudam o expoente).
 *
 * @author VitorWs
 */
public class DemoNucleo {

    /**
     * Executa a bateria de testes automáticos no terminal.
     *
     * @param args Argumentos de linha de comando (não utilizados).
     */
    public static void main(String[] args) {
        // Quantidade de dígitos significativos exigida nos exemplos de validação do projeto (t = 4).
        int t = 4;

        // ------------------------------------------------------------------
        // TESTE 1: Exemplo 2 do edital (Cancelamento Subtrativo)
        // Subtrai dois números muito próximos que só diferem na 5ª casa decimal.
        // ------------------------------------------------------------------
        System.out.println("=== Exemplo 2: cancelamento subtrativo (0,76545 - 0,76541) ===");
        for (ModoCorte m : ModoCorte.values()) {
            imprimir(m, Simulador.executar("0,76545", "0,76541", Operacao.SUBTRACAO, t, m));
        }

        // ------------------------------------------------------------------
        // TESTE 2: Exemplo 3 do edital (Propagação de Erro em Somas Sucessivas)
        // Soma 0,56786 dez vezes seguidas mostrando a evolução do erro a cada passo.
        // ------------------------------------------------------------------
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

        // ------------------------------------------------------------------
        // TESTE 3: Casos de borda na normalização e no arredondamento ("vai-um")
        // Verifica se números cheios de '9', negativos, zero ou inteiros grandes
        // são normalizados e renormalizados corretamente.
        // ------------------------------------------------------------------
        System.out.println("\n=== Casos de borda (vai-um) ===");
        // Todos os dígitos viram 9 -> ao arredondar, vira +0,1000 x 10^1:
        System.out.println(NumeroPontoFlutuante.de("0,99996", 4, ModoCorte.ARREDONDAMENTO));
        // Propaga o vai-um nos últimos dígitos: 129,96 -> +0,1300 x 10^3:
        System.out.println(NumeroPontoFlutuante.de("129,96", 4, ModoCorte.ARREDONDAMENTO));
        // Número pequeno negativo com expoente negativo (-2):
        System.out.println(NumeroPontoFlutuante.de("-0,00123456", 4, ModoCorte.TRUNCAMENTO));
        // Caso especial do zero:
        System.out.println(NumeroPontoFlutuante.de("0", 4, ModoCorte.TRUNCAMENTO));
        // Número inteiro com menos de 4 dígitos significativos (completa mantissa com zeros):
        System.out.println(NumeroPontoFlutuante.de("1500", 4, ModoCorte.ARREDONDAMENTO));
    }

    /**
     * Método auxiliar para exibir no console o resumo de uma operação única formatado em uma linha.
     */
    private static void imprimir(ModoCorte m, ResultadoOperacao r) {
        System.out.printf("%s: x=%s y=%s -> %s | exato=%.8f aprox=%.8f Ea=%.8f Er=%.2f%%%n",
                m.getDescricao(), r.x(), r.y(), r.resultado(),
                r.exato(), r.aproximado(), r.erroAbsoluto(), r.erroRelativo() * 100);
    }
}
