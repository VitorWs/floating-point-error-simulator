package nucleo;

/**
 * Enum para representar os modos de corte da mantissa: truncamento ou arredondamento simétrico.
 *
 * @author VitorWs
 */

public enum ModoCorte {
    TRUNCAMENTO("Truncamento"),
    ARREDONDAMENTO("Arredondamento simétrico");

    private final String descricao;

    ModoCorte(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
