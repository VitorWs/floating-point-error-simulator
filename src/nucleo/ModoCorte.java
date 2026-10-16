package nucleo;

/**
 * Enumeração que define as duas estratégias (modos) possíveis para limitar a precisão
 * de um número quando um computador ou calculadora possui memória finita (apenas 'n' dígitos).
 *
 * Por que isso existe?
 * Na vida real, números podem ter infinitas casas decimais (como 1/3 = 0,333333...),
 * mas um computador tem espaço limitado para guardar cada número. Quando um número
 * tem mais dígitos do que cabem na memória (mais do que 'n' dígitos significativos),
 * precisamos decidir o que fazer com os dígitos que sobram. Existem duas formas clássicas:
 *
 * 1) TRUNCAMENTO ("cortar a seco"):
 *    Simplesmente jogamos fora todos os dígitos que passam do limite 'n', sem nos
 *    importarmos com o valor deles.
 *    Exemplo (guardando n = 4 dígitos): o número 0,76548 vira 0,7654 (o '8' é descartado).
 *
 * 2) ARREDONDAMENTO SIMÉTRICO ("arredondar para o mais próximo"):
 *    Olhamos para o primeiro dígito que será jogado fora (o dígito na posição n + 1).
 *    - Se ele for menor que 5 (0, 1, 2, 3 ou 4), mantemos o último dígito como está.
 *    - Se ele for maior ou igual a 5 (5, 6, 7, 8 ou 9), somamos 1 ao último dígito guardado,
 *      pois o número original estava mais perto do valor de cima do que do valor de baixo.
 *    Exemplo (guardando n = 4 dígitos): o número 0,76548 vira 0,7655 (como o 5º dígito é 8 >= 5,
 *    o 4 sobe para 5).
 *
 * @author VitorWs
 */
public enum ModoCorte {

    /**
     * Modo de Truncamento: descarta diretamente todos os dígitos após o n-ésimo dígito,
     * sem fazer nenhum ajuste no último dígito mantido.
     */
    TRUNCAMENTO("Truncamento"),

    /**
     * Modo de Arredondamento Simétrico: verifica o (n+1)-ésimo dígito; se for >= 5,
     * incrementa em 1 o n-ésimo dígito (propagando o "vai-um" se necessário).
     */
    ARREDONDAMENTO("Arredondamento simétrico");

    /** Nome legível do modo de corte, usado para exibição nas tabelas e interfaces. */
    private final String descricao;

    /**
     * Construtor interno da enumeração que associa um texto amigável a cada modo.
     *
     * @param descricao Texto descritivo do modo (ex: "Truncamento").
     */
    ModoCorte(String descricao) {
        this.descricao = descricao;
    }

    /**
     * Retorna o nome legível deste modo de corte para mostrar ao usuário na tela.
     *
     * @return Descrição textual do modo de corte.
     */
    public String getDescricao() {
        return descricao;
    }
}
