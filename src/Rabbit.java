import java.util.List;

/**
 * A simple model of a rabbit.
 * Rabbits age, move, breed, and die.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-11
 */
public class Rabbit extends Animal{
    // Características compartilhadas pelos coelhos
    private static final int BREEDING_AGE = 5;
    private static final int MAX_AGE = 50;
    private static final double BREEDING_PROBABILITY = 0.15;
    private static final int MAX_LITTER_SIZE = 5;
    private static final double CREATION_PROBABILITY = 0.08;

    /**
     * Create a new rabbit. A rabbit may be created with age
     * zero (a new born) or with a random age.
     * 
     * @param randomAge If true, the rabbit will have a random age.
     */
    public Rabbit(boolean randomAge){
        super(randomAge);
    }
    
    /**
     * This is what the rabbit does most of the time - it runs 
     * around. Sometimes it will breed or die of old age.
     */
    @Override
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns){
        incrementAge();
        if(isAlive()){
            int births = breed(context);
            for(int b = 0; b < births; b++){
                Rabbit newRabbit = new Rabbit(false);
                newborns.add(newRabbit);
                Location loc = updatedField.randomAdjacentLocation(getLocation());
                newRabbit.setLocation(loc);
                updatedField.place(newRabbit, loc);
            }
            Location newLocation = updatedField.freeAdjacentLocation(getLocation());
            if(newLocation != null){
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            }
            else{
                setDead();
            }
        }
    }

    @Override
    protected int getMaxAge(){
        return MAX_AGE;
    }

    @Override
    protected int getBreedingAge(){
        return BREEDING_AGE;
    }

    @Override
    protected double getBreedingProbability(){
        return BREEDING_PROBABILITY;
    }

    @Override
    protected int getMaxLitterSize(){
        return MAX_LITTER_SIZE;
    }
    
    public static double getCreationProbability(){
        return CREATION_PROBABILITY;
    }

    /**
     * Tell the rabbit that it's dead now :(
     */
    public void setEaten(){
        setDead();
    }
}
