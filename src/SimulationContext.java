import java.util.Random;

/**
 * Contexto de simulação contendo dependências injetadas como Random e configuração de espécies.
 */
public class SimulationContext{
    private final Random random;
    private final SpeciesConfigLoader speciesConfig;
    private SeasonCycle seasonCycle;
    private int currentStep;

    public SimulationContext(Random random){
        this.random = random;
        this.speciesConfig = SpeciesConfigLoader.getInstance();
    }

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


