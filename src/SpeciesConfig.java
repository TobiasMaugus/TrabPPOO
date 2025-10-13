import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração de espécies (probabilidades de criação inicial, cores, etc.).
 */
public class SpeciesConfig {
    // Probabilidades de criação inicial
    private double foxCreationProbability;
    private double rabbitCreationProbability;
    private double fishCreationProbability;

    // Mapa de cor por classe concreta
    private Map<Class<?>, Color> colors;
    // multiplicadores sazonais por espécie (nome simples lower -> fator)
    private Map<String, Double> breedingMultiplierBySeasonName;
    // multiplicadores sazonais por espécie específica: key = season:species
    private Map<String, Double> breedingMultiplierBySeasonAndSpecies;
    // suscetibilidade à predação por espécie e estação: probabilidade de sucesso [0..1]
    private Map<String, Double> predationSusceptibilityBySeasonAndSpecies;
    // disponibilidade de alimento para predadores (ex.: raposa) por estação (fator >= 0, >1 pior)
    private Map<String, Double> foodAvailabilityFactorBySeasonAndSpecies;

    public SpeciesConfig() {
        colors = new HashMap<Class<?>, Color>();
        breedingMultiplierBySeasonName = new HashMap<String, Double>();
        breedingMultiplierBySeasonAndSpecies = new HashMap<String, Double>();
        predationSusceptibilityBySeasonAndSpecies = new HashMap<String, Double>();
        foodAvailabilityFactorBySeasonAndSpecies = new HashMap<String, Double>();
    }

    public double getFoxCreationProbability() {
        return foxCreationProbability;
    }

    public void setFoxCreationProbability(double foxCreationProbability) {
        this.foxCreationProbability = foxCreationProbability;
    }

    public double getRabbitCreationProbability() {
        return rabbitCreationProbability;
    }

    public void setRabbitCreationProbability(double rabbitCreationProbability) {
        this.rabbitCreationProbability = rabbitCreationProbability;
    }

    public double getFishCreationProbability() {
        return fishCreationProbability;
    }

    public void setFishCreationProbability(double fishCreationProbability) {
        this.fishCreationProbability = fishCreationProbability;
    }

    public void setColor(Class<?> animalClass, Color color) {
        colors.put(animalClass, color);
    }

    public Color getColor(Class<?> animalClass) {
        Color color = colors.get(animalClass);
        return color != null ? color : Color.gray;
    }

    public void setBreedingMultiplier(String seasonName, double multiplier) {
        breedingMultiplierBySeasonName.put(seasonName.toLowerCase(), multiplier);
    }

    public double getBreedingMultiplier(String seasonName) {
        Double d = breedingMultiplierBySeasonName.get(seasonName.toLowerCase());
        return d != null ? d.doubleValue() : 1.0;
    }

    private static String key(String seasonName, Class<?> species) {
        return seasonName.toLowerCase() + ":" + species.getSimpleName().toLowerCase();
    }

    public void setBreedingMultiplierFor(String seasonName, Class<?> species, double multiplier) {
        breedingMultiplierBySeasonAndSpecies.put(key(seasonName, species), multiplier);
    }

    public double getBreedingMultiplierFor(String seasonName, Class<?> species) {
        Double d = breedingMultiplierBySeasonAndSpecies.get(key(seasonName, species));
        if(d != null) return d.doubleValue();
        return getBreedingMultiplier(seasonName);
    }

    public void setPredationSusceptibility(String seasonName, Class<?> preySpecies, double probability) {
        predationSusceptibilityBySeasonAndSpecies.put(key(seasonName, preySpecies), probability);
    }

    public double getPredationSusceptibility(String seasonName, Class<?> preySpecies) {
        Double d = predationSusceptibilityBySeasonAndSpecies.get(key(seasonName, preySpecies));
        return d != null ? d.doubleValue() : 1.0;
    }

    public void setFoodAvailabilityFactor(String seasonName, Class<?> predatorSpecies, double factor) {
        foodAvailabilityFactorBySeasonAndSpecies.put(key(seasonName, predatorSpecies), factor);
    }

    public double getFoodAvailabilityFactor(String seasonName, Class<?> predatorSpecies) {
        Double d = foodAvailabilityFactorBySeasonAndSpecies.get(key(seasonName, predatorSpecies));
        return d != null ? d.doubleValue() : 1.0;
    }
}


