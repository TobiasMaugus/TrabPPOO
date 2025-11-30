import java.util.List;

/**
 * Modelo simples de um coelho.
 * Coelhos envelhecem, se movem, procriam e morrem.
 */
public class Rabbit extends Animal {
    /** Idade mínima para reprodução. */
    private static final int BREEDING_AGE = 5;

    /** Idade máxima antes de morrer naturalmente. */
    private static final int MAX_AGE = 50;

    /** Probabilidade de se reproduzir a cada passo da simulação. */
    private static final double BREEDING_PROBABILITY = 0.15;

    /** Número máximo de filhotes por reprodução. */
    private static final int MAX_LITTER_SIZE = 5;

    /** Probabilidade de criação inicial de coelhos no campo. */
    private static final double CREATION_PROBABILITY = 0.08;


    /**
     * Construtor de um novo coelho.
     * O coelho pode nascer com idade 0 (recém-nascido)
     * ou receber uma idade aleatória caso randomAge = true.
     *
     * @param randomAge Se true, gera idade inicial aleatória.
     */
    public Rabbit(boolean randomAge) {
        super(randomAge); // delega inicialização à classe Animal
    }


    /**
     * Ações realizadas por coelhos a cada ciclo da simulação.
     * - Envelhece
     * - Verifica se ainda está vivo
     * - Pode se reproduzir
     * - Tenta se mover para uma posição adjacente
     * - Caso não haja para onde ir, morre por superlotação
     * @param context Contexto da simulação (estação, etc).
     * @param currentField O campo no estado atual.
     * @param updatedField O campo onde as atualizações serão aplicadas.
     * @param newborns Lista onde os novos filhotes serão adicionados.
     */
    @Override
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns) {
        incrementAge();
        
        if (isAlive()) {
            // Reprodução
            int births = breed(context);
            for (int b = 0; b < births; b++) {
                Rabbit newRabbit = new Rabbit(false); // filhote nasce com idade 0
                newborns.add(newRabbit);

                // Localização aleatória adjacente onde o filhote será colocado
                Location loc = updatedField.randomAdjacentLocation(getLocation());

                newRabbit.setLocation(loc);
                updatedField.place(newRabbit, loc);
            }

            // Tenta encontrar espaço livre adjacente
            Location newLocation = updatedField.freeAdjacentLocation(getLocation());
            
            if (newLocation != null) {
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            }
            else {
                // Sem espaço = morre por superlotação
                setDead();
            }
        }
    }

    /**
     * @return A idade máxima desta espécie.
     */
    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    /**
     * @return A idade mínima para reprodução.
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

    /** * Probabilidade usada somente na criação inicial do campo.
     * @return O valor da probabilidade de criação.
     */
    public static double getCreationProbability() {
        return CREATION_PROBABILITY;
    }


    /**
     * Marca o coelho como morto por ter sido comido por um predador.
     */
    public void setEaten() {
        setDead();
    }
}