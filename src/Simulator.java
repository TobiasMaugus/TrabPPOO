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
public class Simulator{
    // The private static final variables represent 
    // configuration information for the simulation.
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
    private volatile boolean running = false;
    private Thread simThread;
    private volatile int speedLevel = 1; // 1-15, onde sleep = 10 + (level-1) * 66ms
    // Contexto de simulação (Random e SpeciesConfig)
    private final SimulationContext context;
    private final SpeciesConfigLoader speciesConfig;
    private static SimulationConfig simulationConfig = SimulationConfig.getInstance();;
    private SeasonCycle seasonCycle;
        // configuração de múltiplos lagos
    private List<int[]> lakes = new ArrayList<int[]>(); // each: {centerRow, centerCol, height, width}
    
    /**
     * Construct a simulation field with default size.
     */
    public Simulator()
    {
        this(simulationConfig.getGridHeight(), simulationConfig.getGridWidth(), new Random(), defaultSpeciesConfig());
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

    public Simulator(int depth, int width, Random random, SpeciesConfigLoader config){
        animals = new ArrayList<Animal>();
        newAnimals = new ArrayList<Animal>();
        field = new Field(depth, width);
        updatedField = new Field(depth, width);

        // Create a view of the state of each location in the field.
        speciesConfig = config;
        context = new SimulationContext(random, speciesConfig);
        view = new SimulatorView(depth, width, speciesConfig, this);
        // ciclo sazonal padrão
        seasonCycle = defaultSeasonCycle();
        context.setSeasonCycle(seasonCycle);
        
        // Setup a valid starting point.
        reset();
    }
    
    
    /**
     * Run the simulation from its current state for the given number of steps.
     * Stop before the given number of steps if it ceases to be viable.
     */
    public void simulate(int numSteps)
    {
        // Mantido para compatibilidade, mas controle preferido é Play/Pause.
        for(int step = 1; step <= numSteps && view.isViable(field); step++) simulateOneStep();
    }
    
    /**
     * Run the simulation from its current state for a single step.
     * Iterate over the whole field updating the state of each
     * fox and rabbit.
     */
    public void simulateOneStep()
    {
        step++;
        context.setCurrentStep(step);
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
        SeasonPhase phase = context.getCurrentSeason();
        view.setSeasonPhase(phase);
        view.showStatus(step, field, phase != null ? phase.getName() : null);
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
        // aplica lagos configurados
        for(int[] lake : lakes) {
            field.addLake(lake[0], lake[1], lake[2], lake[3]);
            updatedField.addLake(lake[0], lake[1], lake[2], lake[3]);
        }
        populate(field);
        
        // Show the starting state in the view.
        SeasonPhase phase2 = context.getCurrentSeason();
        view.setSeasonPhase(phase2);
        view.showStatus(step, field, phase2 != null ? phase2.getName() : null);
    }

    public synchronized void startSimulation() {
        if(running) return;
        running = true;
        simThread = new Thread(new Runnable(){
            public void run() {
                while(running && view.isViable(field) && step<simulationConfig.getMaxSteps()) {
                    simulateOneStep();
                    try { 
                        int sleepTime = 10 + (speedLevel - 1) * 66; // 10ms + (level-1) * 66ms
                        Thread.sleep(sleepTime); 
                    } catch (InterruptedException e) { 
                        Thread.currentThread().interrupt(); 
                        break; 
                    }
                }
                running = false;
            }
        }, "SimLoop");
        simThread.setDaemon(true);
        simThread.start();
    }

    public synchronized void pauseSimulation() {
        running = false;
    }

    public synchronized boolean isRunning() {
        return running;
    }
    
    public synchronized void increaseSpeed() {
        if (speedLevel < 15) speedLevel++;
    }
    
    public synchronized void increaseSpeedBy5() {
        speedLevel = Math.min(15, speedLevel + 5);
    }
    
    public synchronized void decreaseSpeed() {
        if (speedLevel > 1) speedLevel--;
    }
    
    public synchronized void decreaseSpeedBy5() {
        speedLevel = Math.max(1, speedLevel - 5);
    }
    
    public synchronized int getSpeedLevel() {
        return speedLevel;
    }
    
    public void loadConfiguration(String configFilePath) throws Exception {
        SimulationConfig config = SimulationConfigLoader.loadFromFile(configFilePath, speciesConfig);
        
        // Pausa simulação atual
        pauseSimulation();
        
        // Recria o simulador com novas dimensões
        animals.clear();
        field = new Field(config.getGridHeight(), config.getGridWidth());
        updatedField = new Field(config.getGridHeight(), config.getGridWidth());
        
        // Recria ciclo sazonal se configurado
        if (!config.getSeasons().isEmpty()) {
            SeasonPhase[] phases = new SeasonPhase[config.getSeasons().size()];
            for (int i = 0; i < config.getSeasons().size(); i++) {
                SeasonPhase sc = config.getSeasons().get(i);
                phases[i] = new SeasonPhase(sc.getName(), sc.getDurationSteps(), sc.getEmptyColor(), sc.getWaterColor());
            }
            seasonCycle = new SeasonCycle(phases);
            context.setSeasonCycle(seasonCycle);
        }
        
        // Limpa lagos antigos e aplica novos do arquivo
        lakes.clear();
        for (LakeConfig lakeConfig : config.getLakes()) {
            lakes.add(new int[]{lakeConfig.getCenterRow(), lakeConfig.getCenterCol(), lakeConfig.getHeight(), lakeConfig.getWidth()});
            field.addLake(lakeConfig.getCenterRow(), lakeConfig.getCenterCol(), lakeConfig.getHeight(), lakeConfig.getWidth());
            updatedField.addLake(lakeConfig.getCenterRow(), lakeConfig.getCenterCol(), lakeConfig.getHeight(), lakeConfig.getWidth());
        }
        
        // Reinicia
        reset();
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
                if(!field.isWater(row, col) && rand.nextDouble() <= Fox.getCreationProbability()) {
                    placeAnimal(new Fox(true), row, col);
                }
                else if(!field.isWater(row, col) && rand.nextDouble() <= Rabbit.getCreationProbability()) {
                    placeAnimal(new Rabbit(true), row, col);
                }
                else if(field.isWater(row, col) && rand.nextDouble() <= Fish.getCreationProbability()) {
                    placeAnimal(new Fish(true), row, col);
                }
                // else leave the location empty.
            }
        }
        Collections.shuffle(animals);
    }

    private void placeAnimal(Animal animal, int row, int col){
        animals.add(animal);
        animal.setLocation(row, col);
        field.place(animal, row, col);
    }

    private static SpeciesConfigLoader defaultSpeciesConfig() {
        SpeciesConfigLoader config = new SpeciesConfigLoader();
        config.setColor(new Fox(false), new Color(255,153,51));
        config.setColor(new Rabbit(false), Color.white);
        config.setColor(new Fish(false), new Color(230, 0, 0));
        // multiplicadores sazonais defaults (ex.: primavera=1.2, inverno=0.7)
        config.setBreedingMultiplier("spring", 1.2);
        config.setBreedingMultiplier("summer", 1.0);
        config.setBreedingMultiplier("autumn", 0.9);
        config.setBreedingMultiplier("winter", 0.7);
        // Peixes não reproduzem no inverno
        config.setBreedingMultiplierFor("winter", new Fish(false), 0.0);
        return config;
    }

    private static SeasonCycle defaultSeasonCycle() {
        SeasonPhase spring = new SeasonPhase("spring", 100, new Color(141,182,0), new Color(42,157,244));
        SeasonPhase summer = new SeasonPhase("summer", 100, new Color(141,182,0), new Color(42,157,244));
        SeasonPhase autumn = new SeasonPhase("autumn", 100, new Color(141,182,0), new Color(42,157,244));
        SeasonPhase winter = new SeasonPhase("winter", 100, new Color(58,86,3), new Color(208,239,255));
        return new SeasonCycle(new SeasonPhase[]{spring, summer, autumn, winter});
    }
}
