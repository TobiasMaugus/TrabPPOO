import java.util.List;
import java.util.ArrayList;

/**
 * Representa a configuração de simulação da aplicação
 */
public class SimulationConfig{
    /** A instância única da classe (Singleton). */
    private static SimulationConfig instanciaUnica;

    /** Largura do grid de simulação. */
    private int gridWidth;
    
    /** Altura do grid de simulação. */
    private int gridHeight;
    
    /** Número máximo de passos da simulação. */
    private int maxSteps;
    
    /** Lista de fases de estação configuradas. */
    private List<SeasonPhase> seasons = new ArrayList<SeasonPhase>();
    
    /** Lista de configurações de lagos. */
    private List<LakeConfig> lakes = new ArrayList<LakeConfig>();

    /**
     * Cria uma configuração de Simulação com tamanho do grid e máximo de steps padrões
     */
    private SimulationConfig(){
        gridHeight = 50;
        gridWidth = 50;
        maxSteps = 500;
    }

    /**
     * Cria uma única configuração de Simulação para todo o sistema. 
     * Utiliza o padrão Singleton
     * @return A instância única da configuração.
     */
    public static SimulationConfig getInstance(){
        if(instanciaUnica == null){
            instanciaUnica = new SimulationConfig();
        }
        return instanciaUnica;
    }

    /** Getters e setters básicos */
    
    /**
     * @return A largura do grid.
     */
    public int getGridWidth(){ 
        return gridWidth; 
    }

    /**
     * @return A altura do grid.
     */
    public int getGridHeight(){ 
        return gridHeight; 
    }

    /**
     * @return O número máximo de passos.
     */
    public int getMaxSteps(){ 
        return maxSteps; 
    }

    /**
     * @return A lista de estações configuradas.
     */
    public List<SeasonPhase> getSeasons(){ 
        return seasons; 
    }

    /**
     * @return A lista de configurações de lagos.
     */
    public List<LakeConfig> getLakes(){ 
        return lakes; 
    }
    
    /**
     * Define a largura do grid.
     * @param width A nova largura.
     */
    public void setGridWidth(int width){ 
        gridWidth = width; 
    }

    /**
     * Define a altura do grid.
     * @param height A nova altura.
     */
    public void setGridHeight(int height){ 
        gridHeight = height; 
    }

    /**
     * Define o número máximo de passos da simulação.
     * @param steps O novo limite de passos.
     */
    public void setMaxSteps(int steps){ 
        maxSteps = steps; 
    }

    /**
     * Adiciona uma nova estação à lista.
     * @param season A estação a ser adicionada.
     */
    public void addSeason(SeasonPhase season){ 
        seasons.add(season); 
    }

    /**
     * Adiciona uma nova configuração de lago à lista.
     * @param lake A configuração de lago a ser adicionada.
     */
    public void addLake(LakeConfig lake){ 
        lakes.add(lake); 
    }
}