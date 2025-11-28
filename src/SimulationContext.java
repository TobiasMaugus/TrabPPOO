import java.util.Random;

/**
 * Contexto de simulação contendo dependências como configuração de espécies.
 */
public class SimulationContext{
    private final Random random;
    private final SpeciesConfigLoader speciesConfig;
    private SeasonCycle seasonCycle;
    private int currentStep;

    /**
     * Cria um contexto de Simulação com uma configuração de espécies
     */
    public SimulationContext(){
        this.random = new Random();
        this.speciesConfig = SpeciesConfigLoader.getInstance();
    }

    /** Getters e setters básicos */

    public Random getRandom(){
        return random;
    }

    public SpeciesConfigLoader getSpeciesConfig(){
        return speciesConfig;
    }

    public void setSeasonCycle(SeasonCycle cycle){
        seasonCycle = cycle;
    }

    public void setCurrentStep(int step){
        currentStep = step;
    }

    public SeasonPhase getCurrentSeason(){
        if(seasonCycle == null) 
            return null;
        return seasonCycle.getPhaseAtStep(currentStep);
    }
}


