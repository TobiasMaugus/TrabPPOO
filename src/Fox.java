import java.util.Iterator;
import java.util.List;

/**
 * Representa uma raposa na simulação, responsável por envelhecer,
 * mover-se pelo campo, caçar presas (coelhos e peixes) e se reproduzir.
 * A classe define características específicas da espécie e o
 * comportamento por turno.
 */
public class Fox extends Animal{
    // Parâmetros específicos da espécie Raposa
    private static final int BREEDING_AGE = 10;
    private static final int MAX_AGE = 150;
    private static final double BREEDING_PROBABILITY = 0.09;
    private static final int MAX_LITTER_SIZE = 3;
    private static final double CREATION_PROBABILITY = 0.02;
    private static final int RABBIT_FOOD_VALUE = 4;
    private static final int FISH_FOOD_VALUE = 2;

    // Nível de comida da raposa, reduzido a cada turno
    private int foodLevel;

    /**
     * Cria uma raposa, podendo iniciar com idade e fome aleatórias
     * ou como recém-nascida, com nível de fome cheio.
     * @param randomAge Se true, a raposa nasce com idade e fome aleatórias
     */
    public Fox(boolean randomAge){
        super(randomAge);
        if(randomAge){
            foodLevel = getRand().nextInt(RABBIT_FOOD_VALUE);
        }
        else{
            foodLevel = RABBIT_FOOD_VALUE;
        }
    }

    /**
     * Define as ações executadas pela raposa em um turno da simulação:
     * - envelhecer, 
     * - ficar com mais fome, 
     * - tentar se reproduzir, 
     * - caçar coelhos ou peixes
     * - mover-se para um local adjacente livre.
     * Caso não encontre comida e nem espaço para se mover, pode morrer.
     */
    @Override
    public void act(SimulationContext context, Field currentField, Field updatedField, List<Animal> newborns){
        incrementAge();
        incrementHunger();
        if(isAlive()){
            int births = breed(context);
            for(int b = 0; b < births; b++){
                Fox newFox = new Fox(false);
                newborns.add(newFox);
                Location loc = updatedField.randomAdjacentLocation(getLocation());
                newFox.setLocation(loc);
                updatedField.place(newFox, loc);
            }

            // Caça ou movimentação
            Location newLocation = findFood(currentField, getLocation());
            if(newLocation == null){
                newLocation = updatedField.freeAdjacentLocation(getLocation());
            }
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
    protected int getMaxAge(){ return MAX_AGE; }

    @Override
    protected int getBreedingAge(){ return BREEDING_AGE; }

    @Override
    protected double getBreedingProbability(){ return BREEDING_PROBABILITY; }

    @Override
    protected int getMaxLitterSize(){ return MAX_LITTER_SIZE; }

    /**
     * Retorna a probabilidade inicial de criação de raposas no campo.
     */
    public static double getCreationProbability(){ return CREATION_PROBABILITY; }

    /**
     * Incrementa a fome da raposa e verifica se ela morre de fome.
     */
    private void incrementHunger(){
        foodLevel--;
        if(foodLevel <= 0){
            setDead();
        }
    }

    /**
     * Procura alimento em locais adjacentes. A raposa prioriza coelhos,
     * mas também pode comer peixes caso esteja em terra e o peixe em água.
     * @return a localização onde encontrou comida, ou null caso contrário.
     */
    private Location findFood(Field field, Location location){
        Iterator<Location> adjacentLocations = field.adjacentLocations(location);

        while(adjacentLocations.hasNext()){
            Location where = adjacentLocations.next();
            Object animal = field.getObjectAt(where);

            if(animal instanceof Rabbit){
                Rabbit rabbit = (Rabbit) animal;
                if(rabbit.isAlive()){
                    rabbit.setEaten();
                    foodLevel = RABBIT_FOOD_VALUE;
                    return where;
                }
            }
            else if(animal instanceof Fish){
                if(!field.isWater(location) && field.isWater(where)){
                    Fish fish = (Fish) animal;
                    if(fish.isAlive()){
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