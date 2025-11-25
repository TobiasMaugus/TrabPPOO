import java.util.List;
import java.util.Random;

/**
 * Base abstrata para todos os animais. Centraliza estado e comportamentos comuns
 * como idade, vida e localização, além de definir o contrato de ação por turno.
 * 
 * @version 1.0
 */
public abstract class Animal {
    // Número aleatório
    private static final Random rand = new Random();

    // Estado comum
    private int age;
    private boolean alive;
    private Location location;

    protected Animal(boolean randomAge) {
        age = 0;
        alive = true;
        if(randomAge) {
            age = rand.nextInt(getMaxAge());
        }
    }

    /**
     * Executa a ação do animal em um passo de simulação. Como não é possível generalizar as ações do animal, 
     * esse método é implementado nas subclasses.
     */
    public abstract void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns);

    /**
     * Incrementa a idade e mata caso exceda a idade máxima.
     */
    protected void incrementAge() {
        age++;
        if(age > getMaxAge()) {
            die();
        }
    }

    /**
     * Reproduz de acordo com probabilidade e limites, usando a fonte de aleatoriedade do contexto.
     */
    protected int breed(SimulationContext context, double probability, int maxLitterSize) {
        int births = 0;
        double prob = probability;
        SeasonPhase phase = context.getCurrentSeason();
        if(phase != null) {
            prob = probability * context.getSpeciesConfig().getBreedingMultiplierFor(phase.getName(), this.getClass());
        }
        if(canBreed() && context.getRandom().nextDouble() <= prob) {
            births = context.getRandom().nextInt(maxLitterSize) + 1;
        }
        return births;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        return true; // todos os animais da mesma classe são iguais
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    /**
     * Verifica se o animal tem idade suficiente para reproduzir
     */
    protected boolean canBreed() {
        return age >= getBreedingAge();
    }

    public boolean isAlive() {
        return alive;
    }

    public static Random getRand() {
        return rand;
    }

    public int getAge() {
        return age;
    }

     public Location getLocation() {
        return location;
    }

    public void setLocation(int row, int col) {
        this.location = new Location(row, col);
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public void die() {
        alive = false;
    }

    /**
     * Retorna a idade mínima de reprodução do animal. Como essa idade não pode ser generalizada,
     * esse método é implementado nas subclasses.
     */
    protected abstract int getBreedingAge();

    /**
     * Retorna a idade máximo do animal. Como essa idade não pode ser generalizada,
     * esse método é implementado nas subclasses.
     */
    protected abstract int getMaxAge();
}


