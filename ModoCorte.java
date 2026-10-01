package nucleo;

/** Estratégia usada para cortar a mantissa em n dígitos significativos. */
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
