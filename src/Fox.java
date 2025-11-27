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
    private static final double CREATION_PROBABILITY = 0.02;
    private static final int RABBIT_FOOD_VALUE = 4;
    private static final int FISH_FOOD_VALUE = 2;
    
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
        super(randomAge);
        if(randomAge){
            foodLevel = getRand().nextInt(RABBIT_FOOD_VALUE);
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
    @Override
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns)
    {
        incrementAge();
        incrementHunger();
        if(isAlive()) {
            int births = breed(context);
            for(int b = 0; b < births; b++) {
                Fox newFox = new Fox(false);
                newborns.add(newFox);
                Location loc = updatedField.randomAdjacentLocation(getLocation());
                newFox.setLocation(loc);
                updatedField.place(newFox, loc);
            }
            Location newLocation = findFood(currentField, getLocation());
            if(newLocation == null) {
                newLocation = updatedField.freeAdjacentLocation(getLocation());
            }
            if(newLocation != null) {
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            }
            else {
                setDead();
            }
        }
    }
    
    /**
     * Increase the age. This could result in the fox's death.
     */
    // incrementAge herdado de Animal

    @Override
    protected int getMaxAge() {
        return MAX_AGE;
    }

    @Override
    protected int getBreedingAge() {
        return BREEDING_AGE;
    }

    @Override
    protected double getBreedingProbability() {
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize() {
        return MAX_LITTER_SIZE;
    }

    public static double getCreationProbability() {
        return CREATION_PROBABILITY;
    }
    
    /**
     * Incrementa a fome da raposa. Isso pode levá-la a morte
     */
    private void incrementHunger()
    {
        int dec = 1;
        foodLevel -= dec;
        if(foodLevel <= 0) {
            setDead();
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
        Iterator<Location> adjacentLocations = field.adjacentLocations(location);

        while(adjacentLocations.hasNext()) {
            Location where = adjacentLocations.next();
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
                        fish.setDead();
                        foodLevel = FISH_FOOD_VALUE;
                        return where;
                    }
                }
            }
        }
        return null;
    }
}
