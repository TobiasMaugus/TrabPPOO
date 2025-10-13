import java.util.List;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-11
 */
public class Rabbit extends Animal
{
    // Características compartilhadas pelos coelhos
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 50;
    private static final double BREEDING_PROBABILITY = 0.15;
    private static final int MAX_LITTER_SIZE = 5;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     */
    public Rabbit(boolean randomAge)
    {
        super();
        if(randomAge) {
            // idade aleatória será definida durante a primeira ação usando o Random do contexto
        }
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     */
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns)
    {
        // Se criado com idade aleatória, inicializa na primeira ação
        if(age == 0 && location == null) {
            // nada a fazer aqui, a localização será definida pelo simulador ao criar
        }
        incrementAge(MAX_AGE);
        if(isAlive()) {
            int births = breed(context, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            for(int b = 0; b < births; b++) {
                Rabbit newRabbit = new Rabbit(false);
                newborns.add(newRabbit);
                Location loc = updatedField.randomAdjacentLocation(location);
                newRabbit.setLocation(loc);
                updatedField.place(newRabbit, loc);
            }
            Location newLocation = updatedField.freeAdjacentLocation(location);
            if(newLocation != null) {
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            }
            else {
                die();
            }
        }
    }
    
    /**
     * Increase the age.
     * This could result in the rabbit's death.
     */
    // incrementAge herdado de Animal
    
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    // breed herdado através de Animal.breed(context,...)

    /**
     * A rabbit can breed if it has reached the breeding age.
     */
    // canBreed herdado via Animal.canBreed(breedingAge)
    
    /**
     * Check whether the rabbit is alive or not.
     * @return True if the rabbit is still alive.
     */
    // isAlive herdado

    /**
     * Tell the rabbit that it's dead now :(
     */
    public void setEaten()
    {
        die();
    }
    
    /**
     * Set the animal's location.
     * @param row The vertical coordinate of the location.
     * @param col The horizontal coordinate of the location.
     */
    // setLocation herdado

    /**
     * Set the rabbit's location.
     * @param location The rabbit's location.
     */
    // setLocation herdado
}
