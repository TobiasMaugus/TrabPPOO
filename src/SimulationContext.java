import java.util.Random;

/**
 * Contexto de simulação contendo dependências como configuração de espécies.
 */
public class SimulationContext{
    /** Gerador de números aleatórios para eventos estocásticos. */
    private final Random random;
    
    /** Carregador de configurações específicas das espécies (taxas, modificadores, etc). */
    private final SpeciesConfigLoader speciesConfig;
    
    /** Ciclo que gerencia a transição das estações na simulação. */
    private SeasonCycle seasonCycle;
    
    /** O passo atual (tempo) da simulação. */
    private int currentStep;

    /**
     * Cria um contexto de Simulação com uma configuração de espécies
     */
    public SimulationContext(){
        this.random = new Random();
        this.speciesConfig = SpeciesConfigLoader.getInstance();
    }

    /** Getters e setters básicos */

    /**
     * @return O gerador de números aleatórios utilizado na simulação.
     */
    public Random getRandom(){
        return random;
    }

    /**
     * @return A instância do carregador de configurações de espécies.
     */
    public SpeciesConfigLoader getSpeciesConfig(){
        return speciesConfig;
    }

    /**
     * Define o ciclo de estações a ser utilizado.
     * @param cycle O ciclo de estações.
     */
    public void setSeasonCycle(SeasonCycle cycle){
        seasonCycle = cycle;
    }

    /**
     * Atualiza o passo atual da simulação.
     * @param step O número do passo atual.
     */
    public void setCurrentStep(int step){
        currentStep = step;
    }

    /**
     * Retorna a estação atual com base no passo da simulação.
     * @return A fase da estação atual ou null se o ciclo não estiver definido.
     */
    public SeasonPhase getCurrentSeason(){
        if(seasonCycle == null) 
            return null;
        return seasonCycle.getPhaseAtStep(currentStep);
    }
}