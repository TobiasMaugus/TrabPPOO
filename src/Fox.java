import java.util.List;
import java.util.Iterator;

/**
 * A simple model of a fox.
 * Foxes age, move, eat rabbits, and die.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-11
 */
public class Fox extends Animal
{
    // Characteristics shared by all foxes (static fields).
    
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.09;
    private static final int MAX_LITTER_SIZE = 3;
    private static final int RABBIT_FOOD_VALUE = 4;
    
    // Individual characteristics (instance fields).

    // The fox's food level, which is increased by eating rabbits.
    private int foodLevel;

    /**
     * Create a fox. A fox can be created as a new born (age zero
     * and not hungry) or with random age.
     * 
     * @param randomAge If true, the fox will have random age and hunger level.
     */
    public Fox(boolean randomAge)
    {
        super();
        if(randomAge) {
            // idade e fome aleatórias serão determinadas usando Random do contexto quando necessário
            foodLevel = RABBIT_FOOD_VALUE / 2; // inicialização segura
        }
        else {
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }
    
    /**
     * This is what the fox does most of the time: it hunts for
     * rabbits. In the process, it might breed, die of hunger,
     * or die of old age.
     */
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns)
    {
        incrementAge(MAX_AGE);
        incrementHunger();
        if(isAlive()) {
            int births = breed(context, BREEDING_AGE, BREEDING_PROBABILITY, MAX_LITTER_SIZE);
            for(int b = 0; b < births; b++) {
                Fox newFox = new Fox(false);
                newborns.add(newFox);
                Location loc = updatedField.randomAdjacentLocation(location);
                newFox.setLocation(loc);
                updatedField.place(newFox, loc);
            }
            Location newLocation = findFood(currentField, location);
            if(newLocation == null) {
                newLocation = updatedField.freeAdjacentLocation(location);
            }
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
     * Increase the age. This could result in the fox's death.
     */
    // incrementAge herdado de Animal
    
    /**
     * Make this fox more hungry. This could result in the fox's death.
     */
    private void incrementHunger()
    {
        foodLevel--;
        if(foodLevel <= 0) {
            die();
        }
    }
    
    /**
     * Tell the fox to look for rabbits adjacent to its current location.
     * @param field The field in which it must look.
     * @param location Where in the field it is located.
     * @return Where food was found, or null if it wasn't.
     */
    private Location findFood(Field field, Location location)
    {
        Iterator adjacentLocations =
                          field.adjacentLocations(location);
        while(adjacentLocations.hasNext()) {
            Location where = (Location) adjacentLocations.next();
            Object animal = field.getObjectAt(where);
            if(animal instanceof Rabbit) {
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()) { 
                    rabbit.setEaten();
                    foodLevel = RABBIT_FOOD_VALUE;
                    return where;
                }
            } else if(animal instanceof Fish) {
                // Raposa na borda externa (terra) e peixe na borda interna (água)
                if(!field.isWater(location) && field.isWater(where)) {
                    Fish fish = (Fish) animal;
                    if(fish.isAlive()) {
                        fish.die();
                        foodLevel = RABBIT_FOOD_VALUE; // mesmo valor nutricional para simplificar
                        return where;
                    }
                }
            }
        }
        return null;
    }
        
    /**
     * Generate a number representing the number of births,
     * if it can breed.
     * @return The number of births (may be zero).
     */
    // breed herdado via Animal.breed(context,...)

    /**
     * A fox can breed if it has reached the breeding age.
     */
    // canBreed herdado via Animal
    
    /**
     * Check whether the fox is alive or not.
     * @return True if the fox is still alive.
     */
    // isAlive herdado

    /**
     * Set the animal's location.
     * @param row The vertical coordinate of the location.
     * @param col The horizontal coordinate of the location.
     */
    // setLocation herdado

    /**
     * Set the fox's location.
     * @param location The fox's location.
     */
    // setLocation herdado
}
