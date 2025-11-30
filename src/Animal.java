import java.util.List;
import java.util.Random;

/**
 * Classe abstrata que representa a base para todos os animais do sistema.
 * Centraliza estado e comportamentos comuns, como idade, condição de vida e localização.
 * Também define o contrato para ações realizadas em cada turno da simulação.
 */
public abstract class Animal {
    /** Gerador de números aleatórios compartilhado entre todos os animais. */
    private static final Random rand = new Random();

    /** Idade atual do animal. */
    private int age;
    /** Indica se o animal ainda está vivo. */
    private boolean alive;
    /** Localização atual do animal no campo de simulação. */
    private Location location;

    /**
     * Construtor base para animais.
     * Pode iniciar com idade aleatória até o limite máximo da espécie, caso especificado.
     */
    public Animal(boolean randomAge) {
        age = 0;
        alive = true;
        if (randomAge) {
            age = rand.nextInt(getMaxAge());
        }
    }

    /**
     * Executa a ação específica da espécie em um passo da simulação.
     * O comportamento concreto deve ser implementado nas subclasses.
     */
    public abstract void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns);

    /**
     * Incrementa a idade do animal e o mata caso tenha ultrapassado a idade máxima.
     */
    protected void incrementAge() {
        age++;
        if (age > getMaxAge()) {
            setDead();
        }
    }

    /**
     * Controla o processo de reprodução da espécie, verificando probabilidade,
     * limites e modificadores de estação.
     * @return quantidade de novos filhotes gerados
     */
    protected int breed(SimulationContext context) {
        int births = 0;
        double prob = getBreedingProbability();
        SeasonPhase phase = context.getCurrentSeason();

        if (phase != null) {
            prob = getBreedingProbability() * context.getSpeciesConfig().getBreedingMultiplierFor(phase.getName(), this);
        }

        if (canBreed() && context.getRandom().nextDouble() <= prob) {
            births = context.getRandom().nextInt(getMaxLitterSize()) + 1;
        }
        return births;
    }

    /**
     * Verifica a igualdade entre este animal e outro objeto.
     * @param obj O objeto a ser comparado.
     * @return true se forem o mesmo objeto ou da mesma classe, false caso contrário.
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null || getClass() != obj.getClass())
            return false;
        return true;
    }

    /**
     * Retorna o código hash baseado na classe do animal.
     * @return O valor do hash code.
     */
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Verifica se o animal tem idade suficiente para se reproduzir.
     */
    protected boolean canBreed() {
        return age >= getBreedingAge();
    }

    /** Getters e setters básicos. */

    /**
     * Verifica se o animal está vivo.
     * @return true se o animal estiver vivo, false caso contrário.
     */
    public boolean isAlive() {
        return alive;
    }

    /**
     * Obtém a instância do gerador de números aleatórios.
     * @return O objeto Random compartilhado.
     */
    protected static Random getRand() {
        return rand;
    }

    /**
     * Obtém a idade atual do animal.
     * @return A idade em passos da simulação.
     */
    public int getAge() {
        return age;
    }

    /**
     * Obtém a localização atual do animal.
     * @return O objeto Location representando a posição no campo.
     */
    public Location getLocation() {
        return location;
    }

    /**
     * Define a localização do animal baseada em coordenadas de linha e coluna.
     * @param row A coordenada da linha.
     * @param col A coordenada da coluna.
     */
    protected void setLocation(int row, int col) {
        this.location = new Location(row, col);
    }

    /**
     * Define a localização do animal utilizando um objeto Location.
     * @param location A nova localização.
     */
    protected void setLocation(Location location) {
        this.location = location;
    }

    /**
     * Marca o animal como morto.
     * Define o estado 'alive' como false.
     */
    protected void setDead() {
        alive = false;
    }

    /**
     * Retorna a idade mínima necessária para que o animal possa reproduzir.
     * Deve ser implementado pelas subclasses.
     */
    protected abstract int getBreedingAge();

    /**
     * Retorna a idade máxima que a espécie pode atingir.
     * Deve ser implementado pelas subclasses.
     */
    protected abstract int getMaxAge();

    /** Probabilidade base de reprodução da espécie. */
    protected abstract double getBreedingProbability();

    /** Tamanho máximo da ninhada gerada pela espécie. */
    protected abstract int getMaxLitterSize();
}