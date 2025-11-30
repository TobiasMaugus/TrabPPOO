/**
 * Representa um ciclo de estações configurável.
 * Um ciclo é composto por várias fases (SeasonPhase)
 */
public class SeasonCycle {

    /** * Vetor contendo as fases (estações) que compõem o ciclo. 
     * A ordem define a sequência do ciclo.
     */
    private final SeasonPhase[] phases;

    /**
     * Duração total em steps do ciclo completo.
     */
    private int totalDuration;


    /**
     * Construtor do ciclo de estações.
     *
     * @param phases Array de fases que compõem o ciclo.
     */
    public SeasonCycle(SeasonPhase[] phases) {
        this.phases = phases;

        // Calcula a soma das durações de todas as fases
        int sum = 0;
        for (SeasonPhase p : phases) {
            sum += p.getDurationSteps();
        }
        this.totalDuration = sum;
    }


    /**
     * Retorna qual é a fase (estação) correspondente a um determinado step global.
     *
     * A lógica funciona assim:
     * - Converte o step global em step relativo dentro do ciclo usando módulo.
     * - Percorre as fases acumulando suas durações.
     *
     * @param globalStep Step absoluto da simulação.
     * @return A fase correspondente dentro do ciclo, ou null se não houver fases.
     */
    public SeasonPhase getPhaseAtStep(int globalStep) {

        // Caso especial: ciclo vazio
        if (totalDuration == 0 || phases.length == 0)
            return null;

        // Step dentro do ciclo (reinicia quando chega ao fim)
        int stepInCycle = globalStep % totalDuration;

        int acc = 0; 

        // Percorre as fases até encontrar aquela cujo intervalo contém o step
        for (SeasonPhase p : phases) {
            acc += p.getDurationSteps();

            // Se o step relativo estiver dentro da faixa dessa fase, retorna ela
            if (stepInCycle < acc)
                return p;
        }

        // Caso o loop não tenha retornado (por segurança):
        return phases[phases.length - 1];
    }
}