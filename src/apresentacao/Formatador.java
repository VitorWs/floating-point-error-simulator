/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package apresentacao;

import java.util.Locale;

/**
 * Classe utilitária responsável por transformar números do tipo {@code double} em textos
 * limpos e fáceis de ler para mostrar ao usuário nas tabelas (tanto na janela gráfica quanto no terminal).
 *
 * Por que precisamos de um formatador próprio?
 * 1) Padronização visual (pt-BR): Em português usamos vírgula decimal (ex: 0,00004) em vez de ponto.
 * 2) Esconder o "ruído binário" do tipo double: No computador, números decimais guardados em
 *    {@code double} (base 2) frequentemente apresentam pequenas sobras na 16ª casa decimal,
 *    como {@code 0.00006000000000000001} quando o valor deveria ser {@code 0.00006}. Ao formatar
 *    com 6 dígitos significativos usando {@code "%.6g"}, eliminamos essa sujeira visual.
 * 3) Tratamento de valores especiais: Quando o Erro Relativo não existe (por exemplo, divisão
 *    por zero quando o valor aproximado dá 0), o Java guarda o código especial {@code Double.NaN}
 *    ("Not a Number"). Esta classe traduz isso para a palavra amigável "Indefinido".
 *
 * @author laeds
 */
public final class Formatador {

    /**
     * Define o idioma/região como Português do Brasil (pt-BR), garantindo que o separador
     * decimal exibido na tela seja sempre a vírgula (',') e não o ponto ('.').
     */
    private static final Locale LOCALE = Locale.forLanguageTag("pt-BR");

    /**
     * Quantidade padrão de dígitos significativos mostrados na exibição dos resultados e erros.
     */
    private static final int DIGITOS = 6;

    /**
     * Construtor privado para impedir a criação de instâncias ("new Formatador()"),
     * pois a classe possui apenas o método estático {@code formatar}.
     */
    private Formatador() {
    }

    /**
     * Converte um número {@code double} em uma String formatada pronta para ser exibida na tabela.
     *
     * Como funciona cada caso:
     * - Se o valor for {@code NaN} (valor indefinido matematicamente, como 0/0), retorna "Indefinido".
     * - Se o valor for exatamente zero, retorna simplesmente "0" (evitando exibir "0,00000").
     * - Para qualquer outro número, usa o especificador {@code "%.6g"}, que escolhe automaticamente
     *   entre notação comum (ex: "0,802450") e notação científica (ex: "4,00000e-05") dependendo
     *   do tamanho do número, sempre mantendo 6 dígitos significativos.
     *
     * @param valor Número a ser formatado para exibição.
     * @return Texto legível representando o número ("Indefinido", "0" ou o valor com 6 dígitos significativos).
     */
    public static String formatar(double valor) {
        // Caso 1: Erro relativo quando o aproximado é zero gera NaN -> mostramos "Indefinido"
        if (Double.isNaN(valor)) {
            return "Indefinido";
        }
        // Caso 2: Zero exato -> mostramos apenas "0" para ficar mais limpo na tabela
        if (valor == 0.0) {
            return "0";
        }
        // Caso 3: Formatação geral com vírgula decimal (LOCALE pt-BR) e 6 dígitos significativos (%g)
        return String.format(LOCALE, "%." + DIGITOS + "g", valor);
    }

}
