import java.util.List;

/**
 * Um peixe que vive apenas em regiões de água (lago).
 * Não caça outros animais e apenas se reproduz, com baixa taxa.
 */
public class Fish extends Animal {
    private static final int BREEDING_AGE = 6; 
    private static final int MAX_AGE = 60;
    private static final double BREEDING_PROBABILITY = 0.10; 
    private static final int MAX_LITTER_SIZE = 4; 
    private static final double CREATION_PROBABILITY = 0.3;

    public Fish(boolean randomAge) {
        super(randomAge);
    }

    @Override
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns){
        incrementAge();
        if(isAlive()){
            int births = breed(context);
            for(int b = 0; b < births; b++){
                Fish newFish = new Fish(false);
                newborns.add(newFish);
                Location loc = updatedField.randomAdjacentWaterLocation(getLocation());
                newFish.setLocation(loc);
                updatedField.place(newFish, loc);
            }
            Location newLocation = updatedField.freeAdjacentWaterLocation(getLocation());
            if(newLocation != null){
                setLocation(newLocation);
                updatedField.place(this, newLocation);
            } 
            else{
                // se não há água livre ao redor e a atual estiver ocupada, o peixe morre por superlotação
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
}


