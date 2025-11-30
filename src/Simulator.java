import java.util.Random;
import java.util.List;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Collections;
import java.awt.Color;

/**
 * Um simulador simples de predador–presa, baseado em um campo contendo
 * coelhos, raposas e peixes, com suporte a configuração externa e ciclos sazonais.
 * * Controla o estado da simulação, a progressão dos passos, as listas de animais,
 * e a interação com a interface gráfica.
 * * @author João Gabriel Salomão Baldim
 * @author Luis Kennedy Gervásio Turola
 * @author Thaís Giovanna Lopes
 * @author Tobias Maugus Bueno Cougo
 * * Adaptado de: David J. Barnes e Michael Kolling (2002).
 */
public class Simulator{
    /** Lista de todos os animais vivos no campo. */
    private List<Animal> animals;
    
    /** Lista de animais nascidos durante o passo atual. */
    private List<Animal> newAnimals;
    
    /** Campo atual da simulação. */
    private Field field;
    
    /** Segundo campo usado para calcular o próximo estado. */
    private Field updatedField;
    
    /** Contador de passos da simulação. */
    private int step;
    
    /** Interface gráfica da simulação. */
    private SimulatorView view;
    
    /** Estado da execução (rodando / pausado). */
    private volatile boolean running = false;
    
    /** Thread responsável pelo loop de execução da simulação. */
    private Thread simThread;
    
    /** Velocidade da simulação (1–15). */
    private volatile int speedLevel = 1;

    /** Contexto da simulação com Random e configurações. */
    private static SimulationContext context;
    
    /** Carregador singleton de configurações de espécies. */
    private static SpeciesConfigLoader speciesConfig = SpeciesConfigLoader.getInstance();
    
    /** Configuração geral da simulação (singleton). */
    private static SimulationConfig simulationConfig = SimulationConfig.getInstance();

    /** Ciclo sazonal atual. */
    private SeasonCycle seasonCycle;
    
    /** Configuração de múltiplos lagos (cada item: {linha, coluna, altura, largura}). */
    private List<int[]> lakes = new ArrayList<int[]>();

    /**
     * Construtor do simulador.
     * Inicializa campo, lista de animais, ciclo sazonal padrão, configurações
     * e prepara a interface gráfica.
     */
    public Simulator(){
        int depth = simulationConfig.getGridHeight();
        int width = simulationConfig.getGridWidth();

        animals = new ArrayList<Animal>();
        newAnimals = new ArrayList<Animal>();
        field = new Field(depth, width);
        updatedField = new Field(depth, width);

        context = new SimulationContext();
        defaultSpeciesConfig();
        view = new SimulatorView(depth, width, this);

        seasonCycle = defaultSeasonCycle();
        context.setSeasonCycle(seasonCycle);
        
        reset();
    }
    
    
    /**
     * Executa um único passo da simulação.
     * Atualiza o passo global, permite que cada animal aja
     * e atualiza o campo para a próxima geração.
     */
    public void simulateOneStep(){
        step++;
        context.setCurrentStep(step);
        newAnimals.clear();
        
        // Executa as ações de cada animal
        for(Iterator<Animal> iter = animals.iterator(); iter.hasNext(); ) {
            Animal animal = iter.next();
            if(animal.isAlive()) {
                animal.act(context, field, updatedField, newAnimals);
            } else {
                iter.remove();
            }
        }

        animals.addAll(newAnimals);

        // Troca o campo pelo atualizado
        Field temp = field;
        field = updatedField;
        updatedField = temp;
        updatedField.clear();

        // Atualiza exibição gráfica
        SeasonPhase phase = context.getCurrentSeason();
        view.setSeasonPhase(phase);
        view.showStatus(step, field, phase != null ? phase.getName() : null);
    }
        
    /**
     * Reinicia a simulação para o estado inicial.
     * Limpa animais, campo e reaplica lagos e população inicial.
     */
    public void reset()
    {
        step = 0;
        animals.clear();
        field.clear();
        updatedField.clear();

        for(int[] lake : lakes) {
            field.addLake(lake[0], lake[1], lake[2], lake[3]);
            updatedField.addLake(lake[0], lake[1], lake[2], lake[3]);
        }

        populate(field);

        SeasonPhase phase2 = context.getCurrentSeason();
        view.setSeasonPhase(phase2);
        view.showStatus(step, field, phase2 != null ? phase2.getName() : null);
    }

    /**
     * Inicia a execução contínua da simulação em uma thread separada,
     * respeitando a velocidade configurada.
     */
    public synchronized void startSimulation() {
        if(running) return;
        running = true;
        simThread = new Thread(new Runnable(){
            public void run() {
                while(running && view.isViable(field) && step<simulationConfig.getMaxSteps()) {
                    simulateOneStep();
                    try { 
                        int sleepTime = 10 + (speedLevel - 1) * 66;
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

    /**
     * Pausa a simulação.
     */
    public synchronized void pauseSimulation() {
        running = false;
    }

    /**
     * Verifica se a simulação está em execução.
     * @return true se a simulação está em execução.
     */
    public synchronized boolean isRunning() {
        return running;
    }
    
    /**
     * Aumenta a velocidade da simulação em 1 nível.
     */
    public synchronized void increaseSpeed() {
        if (speedLevel < 15) 
            speedLevel++;
    }

    /**
     * Aumenta a velocidade da simulação em 5 níveis.
     */
    public synchronized void increaseSpeedBy5() {
        speedLevel = Math.min(15, speedLevel + 5);
    }
    
    /**
     * Reduz a velocidade da simulação em 1 nível.
     */
    public synchronized void decreaseSpeed() {
        if (speedLevel > 1) 
            speedLevel--;
    }
    
    /**
     * Reduz a velocidade da simulação em 5 níveis.
     */
    public synchronized void decreaseSpeedBy5() {
        speedLevel = Math.max(1, speedLevel - 5);
    }
    
    /**
     * Retorna o nível de velocidade atual.
     * @return o nível atual de velocidade da simulação.
     */
    public synchronized int getSpeedLevel() {
        return speedLevel;
    }
    
    /**
     * Carrega configurações completas de um arquivo externo,
     * incluindo dimensões, estações, lagos e parâmetros.
     * * @param configFilePath Caminho do arquivo JSON/YAML de configuração.
     * @throws Exception caso o arquivo seja inválido.
     */
    public void loadConfiguration(String configFilePath) throws Exception {
        SimulationConfig config = SimulationConfigLoader.loadFromFile(configFilePath);
        
        pauseSimulation();

        animals.clear();
        field = new Field(config.getGridHeight(), config.getGridWidth());
        updatedField = new Field(config.getGridHeight(), config.getGridWidth());
        
        if (!config.getSeasons().isEmpty()) {
            SeasonPhase[] phases = new SeasonPhase[config.getSeasons().size()];
            for (int i = 0; i < config.getSeasons().size(); i++) {
                SeasonPhase sc = config.getSeasons().get(i);
                phases[i] = new SeasonPhase(sc.getName(), sc.getDurationSteps(), sc.getEmptyColor(), sc.getWaterColor());
            }
            seasonCycle = new SeasonCycle(phases);
            context.setSeasonCycle(seasonCycle);
        }
        
        lakes.clear();
        for (LakeConfig lakeConfig : config.getLakes()) {
            lakes.add(new int[]{lakeConfig.getCenterRow(), lakeConfig.getCenterCol(), lakeConfig.getHeight(), lakeConfig.getWidth()});
            field.addLake(lakeConfig.getCenterRow(), lakeConfig.getCenterCol(), lakeConfig.getHeight(), lakeConfig.getWidth());
            updatedField.addLake(lakeConfig.getCenterRow(), lakeConfig.getCenterCol(), lakeConfig.getHeight(), lakeConfig.getWidth());
        }
        
        reset();
    }
    
    /**
     * Popula o campo inicial com raposas, coelhos e peixes,
     * seguindo as probabilidades definidas nas configurações.
     * @param field O campo a ser populado.
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
            }
        }
        Collections.shuffle(animals);
    }

    /**
     * Posiciona um animal em uma célula específica do campo.
     *
     * @param animal Animal a ser inserido
     * @param row Linha da posição
     * @param col Coluna da posição
     */
    private void placeAnimal(Animal animal, int row, int col){
        animals.add(animal);
        animal.setLocation(row, col);
        field.place(animal, row, col);
    }

    /**
     * Define cores e multiplicadores iniciais das espécies por padrão.
     */
    private static void defaultSpeciesConfig() {
        speciesConfig.setColor(new Fox(false), new Color(255,153,51));
        speciesConfig.setColor(new Rabbit(false), Color.white);
        speciesConfig.setColor(new Fish(false), new Color(230, 0, 0));
        speciesConfig.setBreedingMultiplier("spring", 1.2);
        speciesConfig.setBreedingMultiplier("summer", 1.0);
        speciesConfig.setBreedingMultiplier("autumn", 0.9);
        speciesConfig.setBreedingMultiplier("winter", 0.7);
    }

    /**
     * Define nome, duração e cores iniciais das estações por padrão.
     * @return Um ciclo de estações padrão.
     */
    private static SeasonCycle defaultSeasonCycle() {
        SeasonPhase spring = new SeasonPhase("spring", 100, new Color(141,182,0), new Color(42,157,244));
        SeasonPhase summer = new SeasonPhase("summer", 100, new Color(141,182,0), new Color(42,157,244));
        SeasonPhase autumn = new SeasonPhase("autumn", 100, new Color(141,182,0), new Color(42,157,244));
        SeasonPhase winter = new SeasonPhase("winter", 100, new Color(58,86,3), new Color(208,239,255));
        return new SeasonCycle(new SeasonPhase[]{spring, summer, autumn, winter});
    }
}