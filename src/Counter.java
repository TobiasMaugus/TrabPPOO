/**
 * Classe responsável por manter um contador de participantes de um tipo específico
 * dentro da simulação. Armazena um nome identificador e a quantidade atual
 * de instâncias existentes.
 */
public class Counter {
    /** Nome que identifica o tipo de participante monitorado pelo contador. */
    private String name;

    /** Quantidade atual de participantes desse tipo na simulação. */
    private int count;

    /**
     * Constrói um contador para um tipo específico de participante.
     * @param name Nome representando o tipo (ex.: "Raposa").
     */
    public Counter(String name) {
        this.name = name;
        count = 0;
    }

    /** @return O nome associado a este tipo de participante. */
    public String getName() {
        return name;
    }

    /** @return A contagem atual de participantes desse tipo. */
    public int getCount() {
        return count;
    }

    /**
     * Incrementa a contagem em uma unidade.
     */
    public void increment() {
        count++;
    }

    /**
     * Reinicia a contagem para zero.
     */
    public void reset() {
        count = 0;
    }
}