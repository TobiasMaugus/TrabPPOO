import java.util.List;
import java.util.ArrayList;
import java.awt.Color;

/**
 * Configuração de simulação carregada de arquivo .txt
 */
public class SimulationConfig {
    private static SimulationConfig instanciaUnica;

    private int gridWidth;
    private int gridHeight;
    private int maxSteps;
    private List<SeasonConfig> seasons = new ArrayList<SeasonConfig>();
    private List<LakeConfig> lakes = new ArrayList<LakeConfig>();
    private List<SpeciesRateConfig> speciesRates = new ArrayList<SpeciesRateConfig>();

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
    
    public static class SeasonConfig {
        private String name;
        private int durationSteps;
        private Color emptyColor;
        private Color waterColor;
        
        public SeasonConfig(String name, int durationSteps, Color emptyColor, Color waterColor) {
            this.name = name;
            this.durationSteps = durationSteps;
            this.emptyColor = emptyColor;
            this.waterColor = waterColor;
            
        }
        
        public String getName() { return name; }
        public int getDurationSteps() { return durationSteps; }
        public Color getEmptyColor() { return emptyColor; }
        public Color getWaterColor() { return waterColor; }
    }
    
    public static class LakeConfig {
        private int centerRow, centerCol, height, width;
        
        public LakeConfig(int centerRow, int centerCol, int height, int width) {
            this.centerRow = centerRow;
            this.centerCol = centerCol;
            this.height = height;
            this.width = width;
        }
        
        public int getCenterRow() { return centerRow; }
        public int getCenterCol() { return centerCol; }
        public int getHeight() { return height; }
        public int getWidth() { return width; }
    }
    
    public static class SpeciesRateConfig {
        private String speciesName;
        private String seasonName;
        private double breedingRate;
        private double predationSusceptibility;
        private double foodAvailability;
        
        public SpeciesRateConfig(String speciesName, String seasonName, double breedingRate, double predationSusceptibility, double foodAvailability) {
            this.speciesName = speciesName;
            this.seasonName = seasonName;
            this.breedingRate = breedingRate;
            this.predationSusceptibility = predationSusceptibility;
            this.foodAvailability = foodAvailability;
        }
        
        public String getSpeciesName() { return speciesName; }
        public String getSeasonName() { return seasonName; }
        public double getBreedingRate() { return breedingRate; }
        public double getPredationSusceptibility() { return predationSusceptibility; }
        public double getFoodAvailability() { return foodAvailability; }
    }
    
    // Getters
    public int getGridWidth() { return gridWidth; }
    public int getGridHeight() { return gridHeight; }
    public int getMaxSteps() { return maxSteps; }
    public List<SeasonConfig> getSeasons() { return seasons; }
    public List<LakeConfig> getLakes() { return lakes; }
    public List<SpeciesRateConfig> getSpeciesRates() { return speciesRates; }
    
    // Setters
    public void setGridWidth(int width) { gridWidth = width; }
    public void setGridHeight(int height) { gridHeight = height; }
    public void setMaxSteps(int steps) { maxSteps = steps; }
    public void addSeason(SeasonConfig season) { seasons.add(season); }
    public void addLake(LakeConfig lake) { lakes.add(lake); }
    public void addSpeciesRate(SpeciesRateConfig rate) { speciesRates.add(rate); }
}
