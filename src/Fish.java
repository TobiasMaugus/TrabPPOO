import java.util.List;

/**
 * Um peixe que vive apenas em regiões de água (lago).
 * Não caça outros animais e apenas se reproduz, com taxa mais baixa.
 */
public class Fish extends Animal {
    private static final int BREEDING_AGE = 6; // um pouco mais tarde que coelhos
    private static final int MAX_AGE = 60;
    private static final double BREEDING_PROBABILITY = 0.10; // menor que coelho (0.15)
    private static final int MAX_LITTER_SIZE = 4; // um pouco menor que coelhos

    public Fish(boolean randomAge) {
        super();
        if(randomAge) {
            // idade aleatória será tratada no fluxo de ação a partir do contexto se necessário
        }
    }

    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns)
    {
        incrementAge(MAX_AGE);
        if(isAlive()) {
            int births = breed(context, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            for(int b = 0; b < births; b++) {
                Fish newFish = new Fish(false);
                newborns.add(newFish);
                Location loc = updatedField.randomAdjacentWaterLocation(location);
                newFish.setLocation(loc);
                updatedField.place(newFish, loc);
            }
            Location newLocation = updatedField.freeAdjacentWaterLocation(location);
            if(newLocation != null) {
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            } else {
                // se não há água livre ao redor e a atual estiver ocupada, o peixe morre por superlotação
                die();
            }
        }
    }
}


