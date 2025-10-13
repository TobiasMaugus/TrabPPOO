import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.awt.Color;

/**
 * A simple predator-prey simulator, based on a field containing
 * rabbits and foxes.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-09
 */
public class Simulator
{
    // The private static final variables represent 
    // configuration information for the simulation.
    // The default width for the grid.
    private static final int DEFAULT_WIDTH = 50;
    // The default depth of the grid.
    private static final int DEFAULT_DEPTH = 50;
    // The probability that a fox will be created in any given grid position.
    // Probabilidades passam a vir de SpeciesConfig

    // The list of animals in the field
    private List<Animal> animals;
    // The list of animals just born
    private List<Animal> newAnimals;
    // The current state of the field.
    private Field field;
    // A second field, used to build the next stage of the simulation.
    private Field updatedField;
    // The current step of the simulation.
    private int step;
    // A graphical view of the simulation.
    private SimulatorView view;
    // Contexto de simulação (Random e SpeciesConfig)
    private final SimulationContext context;
    private final SpeciesConfig speciesConfig;
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(DEFAULT_DEPTH, DEFAULT_WIDTH, new Random(), defaultSpeciesConfig());
    }
    
    /**
     * Create a simulation field with the given size.
     * @param depth Depth of the field. Must be greater than zero.
     * @param width Width of the field. Must be greater than zero.
     */
    public Simulator(int depth, int width)
    {
        this(depth, width, new Random(), defaultSpeciesConfig());
    }

    public Simulator(int depth, int width, Random random, SpeciesConfig config)
    {
        if(width <= 0 || depth <= 0) {
            System.out.println("The dimensions must be greater than zero.");
            System.out.println("Using default values.");
            depth = DEFAULT_DEPTH;
            width = DEFAULT_WIDTH;
        }
        animals = new ArrayList<Animal>();
        newAnimals = new ArrayList<Animal>();
        field = new Field(depth, width);
        updatedField = new Field(depth, width);

        // Create a view of the state of each location in the field.
        speciesConfig = config;
        context = new SimulationContext(random, speciesConfig);
        view = new SimulatorView(depth, width, speciesConfig);
        
        // Setup a valid starting point.
        reset();
    }
    
    /**
     * Run the simulation from its current state for a reasonably long period,
     * e.g. 500 steps.
     */
    public void runLongSimulation()
    {
        simulate(500);
    }
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     */
    public void simulate(int numSteps)
    {
        for(int step = 1; step <= numSteps && view.isViable(field); step++) {
            simulateOneStep();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        newAnimals.clear();
        
        // let all animals act
        for(Iterator<Animal> iter = animals.iterator(); iter.hasNext(); ) {
            Animal animal = iter.next();
            if(animal.isAlive()) {
                animal.act(context, field, updatedField, newAnimals);
            } else {
                iter.remove();
            }
        }
        // add new born animals to the list of animals
        animals.addAll(newAnimals);
        
        // Swap the field and updatedField at the end of the step.
        Field temp = field;
        field = updatedField;
        updatedField = temp;
        updatedField.clear();

        // display the new field on screen
        view.showStatus(step, field);
    }
        
    /**
     * Reset the simulation to a starting position.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        field.clear();
        updatedField.clear();
        // aplica lago configurado, se houver
        if(lakeConfigured) {
            field.setLake(lakeCenterRow, lakeCenterCol, lakeHeight, lakeWidth);
            updatedField.setLake(lakeCenterRow, lakeCenterCol, lakeHeight, lakeWidth);
        }
        populate(field);
        
        // Show the starting state in the view.
        view.showStatus(step, field);
    }

    // configuração opcional de lago
    private boolean lakeConfigured = false;
    private int lakeCenterRow;
    private int lakeCenterCol;
    private int lakeHeight;
    private int lakeWidth;

    public void configureLake(int centerRow, int centerCol, int height, int width) {
        this.lakeConfigured = true;
        this.lakeCenterRow = centerRow;
        this.lakeCenterCol = centerCol;
        this.lakeHeight = height;
        this.lakeWidth = width;
        // aplica imediatamente ao estado atual
        if(field != null && updatedField != null) {
            field.setLake(lakeCenterRow, lakeCenterCol, lakeHeight, lakeWidth);
            updatedField.setLake(lakeCenterRow, lakeCenterCol, lakeHeight, lakeWidth);
            // remover quaisquer animais que porventura estivessem sobre água já é garantido por Field.setLake
            seedFishInLake();
            view.showStatus(step, field);
        }
    }

    private void seedFishInLake() {
        Random rand = context.getRandom();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(field.isWater(row, col) && field.getObjectAt(row, col) == null) {
                    if(rand.nextDouble() <= speciesConfig.getFishCreationProbability()) {
                        Fish fish = new Fish(true);
                        animals.add(fish);
                        fish.setLocation(row, col);
                        field.place(fish, row, col);
                    }
                }
            }
        }
    }
    
    /**
     * Populate the field with foxes and rabbits.
     */
    private void populate(Field field)
    {
        Random rand = context.getRandom();
        field.clear();
        for(int row = 0; row < field.getDepth(); row++) {
            for(int col = 0; col < field.getWidth(); col++) {
                if(!field.isWater(row, col) && rand.nextDouble() <= speciesConfig.getFoxCreationProbability()) {
                    Fox fox = new Fox(true);
                    animals.add(fox);
                    fox.setLocation(row, col);
                    field.place(fox, row, col);
                }
                else if(!field.isWater(row, col) && rand.nextDouble() <= speciesConfig.getRabbitCreationProbability()) {
                    Rabbit rabbit = new Rabbit(true);
                    animals.add(rabbit);
                    rabbit.setLocation(row, col);
                    field.place(rabbit, row, col);
                }
                else if(field.isWater(row, col) && rand.nextDouble() <= speciesConfig.getFishCreationProbability()) {
                    Fish fish = new Fish(true);
                    animals.add(fish);
                    fish.setLocation(row, col);
                    field.place(fish, row, col);
                }
                // else leave the location empty.
            }
        }
        Collections.shuffle(animals);
    }

    private static SpeciesConfig defaultSpeciesConfig() {
        SpeciesConfig config = new SpeciesConfig();
        config.setFoxCreationProbability(0.02);
        config.setRabbitCreationProbability(0.08);
        config.setColor(Fox.class, Color.blue);
        config.setColor(Rabbit.class, Color.orange);
        config.setColor(Fish.class, new Color(230, 0, 0));
        config.setFishCreationProbability(0.09);
        return config;
    }
}
