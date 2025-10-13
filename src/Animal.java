import java.util.List;
import java.util.Random;

/**
 * Base abstrata para todos os animais. Centraliza estado e comportamentos comuns
 * como idade, vida e localização, além de definir o contrato de ação por turno.
 */
public abstract class Animal {
    // Estado comum
    protected int age;
    protected boolean alive;
    protected Location location;

    protected Animal() {
        this.age = 0;
        this.alive = true;
    }

    /**
     * Executa a ação do animal em um passo de simulação.
     */
    public abstract void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns);

    /**
     * Incrementa a idade e mata caso exceda a idade máxima.
     */
    protected void incrementAge(int maxAge) {
        age++;
        if(age > maxAge) {
            die();
        }
    }

    /**
     * Reproduz de acordo com probabilidade e limites, usando a fonte de aleatoriedade do contexto.
     */
    protected int breed(SimulationContext context, int breedingAge, double probability, int maxLitterSize) {
        int births = 0;
        if(canBreed(breedingAge) && context.getRandom().nextDouble() <= probability) {
            births = context.getRandom().nextInt(maxLitterSize) + 1;
        }
        return births;
    }

    protected boolean canBreed(int breedingAge) {
        return age >= breedingAge;
    }

    public boolean isAlive() {
        return alive;
    }

    public void die() {
        alive = false;
    }

    public void setLocation(int row, int col) {
        this.location = new Location(row, col);
    }

    public void setLocation(Location location) {
        this.location = location;
    }
}


