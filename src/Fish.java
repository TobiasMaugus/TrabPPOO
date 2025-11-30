import java.util.List;

/**
 * Representa um peixe que vive exclusivamente em áreas de água (lago).
 * Peixes não caçam outros animais — apenas envelhecem, se reproduzem
 * com baixa probabilidade e se movem por regiões aquáticas.
 */
public class Fish extends Animal {
    /** Idade mínima para reprodução. */
    private static final int BREEDING_AGE = 6;

    /** Idade máxima antes da morte natural. */
    private static final int MAX_AGE = 60;

    /** Probabilidade de um peixe se reproduzir a cada ciclo. */
    private static final double BREEDING_PROBABILITY = 0.10;

    /** Quantidade máxima de filhotes gerados por reprodução. */
    private static final int MAX_LITTER_SIZE = 4;

    /** Probabilidade de criação inicial de um peixe no campo. */
    private static final double CREATION_PROBABILITY = 0.3;

    /**
     * Cria um peixe, possivelmente com idade aleatória.
     * @param randomAge Se true, o peixe nasce com idade aleatória.
     */
    public Fish(boolean randomAge) {
        super(randomAge);
    }

    /**
     * Ações realizadas por um peixe a cada passo da simulação:
     * - envelhecer
     * - possivelmente gerar novos peixes
     * - tentar mover-se para uma célula adjacente de água
     * - morrer em caso de superlotação
     * * @param context Contexto da simulação (estação, configurações, etc).
     * @param currentField O campo atual (estado imutável neste turno).
     * @param updatedField O campo atualizado para onde os animais se movem.
     * @param newborns Lista onde os novos filhotes gerados serão adicionados.
     */
    @Override
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns) {
        incrementAge();

        if (isAlive()) {
            // Reprodução
            int births = breed(context);
            for (int b = 0; b < births; b++) {
                Fish newFish = new Fish(false);
                newborns.add(newFish);
                Location loc = updatedField.randomAdjacentWaterLocation(getLocation());
                newFish.setLocation(loc);
                updatedField.place(newFish, loc);
            }

            // Movimento
            Location newLocation = updatedField.freeAdjacentWaterLocation(getLocation());
            if (newLocation != null) {
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            }
            else {
                // Morte por superlotação em área aquática
                setDead();
            }
        }
    }

    /**
     * @return A idade máxima permitida para esta espécie.
     */
    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * @return A idade mínima para começar a reproduzir.
     */
    @Override
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    /**
     * @return A probabilidade de reprodução desta espécie.
     */
    @Override
    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    /**
     * @return O tamanho máximo da ninhada.
     */
    @Override
    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    /**
     * Probabilidade de criação inicial de peixes no campo.
     * @return O valor da probabilidade de criação.
     */
    public static double getCreationProbability() {
        return CREATION_PROBABILITY;
    }
}