import java.util.List;
import java.util.ArrayList;
/**
 * Configuração de simulação carregada de arquivo .txt
 */
public class SimulationConfig {
    private static SimulationConfig instanciaUnica;

    private int gridWidth;
    private int gridHeight;
    private int maxSteps;
    private List<SeasonPhase> seasons = new ArrayList<SeasonPhase>();
    private List<LakeConfig> lakes = new ArrayList<LakeConfig>();

    private SimulationConfig(){
        gridHeight = 50;
        gridWidth = 50;
        maxSteps = 500;
    }

    public static SimulationConfig getInstance(){
        if(instanciaUnica == null){
            instanciaUnica = new SimulationConfig();
        }
        return instanciaUnica;
    }
    
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getMaxSteps() { return maxSteps; }
    public List<SeasonPhase> getSeasons() { return seasons; }
    public List<LakeConfig> getLakes() { return lakes; }
    
    public void setGridWidth(int width) { gridWidth = width; }
    public void setGridHeight(int height) { gridHeight = height; }
    public void setMaxSteps(int steps) { maxSteps = steps; }
    public void addSeason(SeasonPhase season) { seasons.add(season); }
    public void addLake(LakeConfig lake) { lakes.add(lake); }
}
