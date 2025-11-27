import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Configuração de espécies (probabilidades de criação inicial, cores, etc.).
 */
public class SpeciesConfigLoader {
    // Mapa de cor por classe concreta
    private Map<Animal, Color> colors;
    // multiplicadores sazonais por espécie (nome simples lower -> fator)
    private Map<String, Double> breedingMultiplierBySeasonName;
    // multiplicadores sazonais por espécie específica: key = season:species
    private Map<String, Double> breedingMultiplierBySeasonAndSpecies;
    // suscetibilidade à predação por espécie e estação: probabilidade de sucesso [0..1]
    private Map<String, Double> predationSusceptibilityBySeasonAndSpecies;
    // disponibilidade de alimento para predadores (ex.: raposa) por estação (fator >= 0, >1 pior)
    private Map<String, Double> foodAvailabilityFactorBySeasonAndSpecies;

    public SpeciesConfigLoader() {
        colors = new HashMap<Animal, Color>();
        breedingMultiplierBySeasonName = new HashMap<String, Double>();
        breedingMultiplierBySeasonAndSpecies = new HashMap<String, Double>();
        predationSusceptibilityBySeasonAndSpecies = new HashMap<String, Double>();
        foodAvailabilityFactorBySeasonAndSpecies = new HashMap<String, Double>();
    }

    public Color getColor(Animal animalClass) {
        Color color = colors.get(animalClass);
        return color != null ? color : Color.gray;
    }

    public void setColor(Animal animalClass, Color color) {
        colors.put(animalClass, color);
    }

    private double getBreedingMultiplier(String seasonName) {
        Double d = breedingMultiplierBySeasonName.get(seasonName.toLowerCase());
        return d != null ? d.doubleValue() : 1.0;
    }

    public void setBreedingMultiplier(String seasonName, double multiplier) {
        breedingMultiplierBySeasonName.put(seasonName.toLowerCase(), multiplier);
    }


    private static String key(String seasonName, Animal species) {
        return key(seasonName, species.getClass().getSimpleName());
    }

    private static String key(String seasonName, String speciesName) {
        return seasonName.toLowerCase() + ":" + speciesName.toLowerCase();
    }

    public void setBreedingMultiplierFor(String seasonName, Animal species, double multiplier) {
        breedingMultiplierBySeasonAndSpecies.put(key(seasonName, species), multiplier);
    }

    public void setBreedingMultiplierFor(String seasonName, String speciesName, double multiplier) {
        breedingMultiplierBySeasonAndSpecies.put(key(seasonName, speciesName), multiplier);
    }

    public double getBreedingMultiplierFor(String seasonName, Animal species) {
        Double d = breedingMultiplierBySeasonAndSpecies.get(key(seasonName, species));
        if(d != null) return d.doubleValue();
        return getBreedingMultiplier(seasonName);
    }

    public void setPredationSusceptibility(String seasonName, Animal preySpecies, double probability) {
        predationSusceptibilityBySeasonAndSpecies.put(key(seasonName, preySpecies), probability);
    }

    public void setPredationSusceptibility(String seasonName, String speciesName, double probability) {
        predationSusceptibilityBySeasonAndSpecies.put(key(seasonName, speciesName), probability);
    }

    public double getPredationSusceptibility(String seasonName, Animal preySpecies) {
        Double d = predationSusceptibilityBySeasonAndSpecies.get(key(seasonName, preySpecies));
        return d != null ? d.doubleValue() : 1.0;
    }

    public void setFoodAvailabilityFactor(String seasonName, Animal predatorSpecies, double factor) {
        foodAvailabilityFactorBySeasonAndSpecies.put(key(seasonName, predatorSpecies), factor);
    }

    public void setFoodAvailabilityFactor(String seasonName, String speciesName, double factor) {
        foodAvailabilityFactorBySeasonAndSpecies.put(key(seasonName, speciesName), factor);
    }

    public double getFoodAvailabilityFactor(String seasonName, Animal predatorSpecies) {
        Double d = foodAvailabilityFactorBySeasonAndSpecies.get(key(seasonName, predatorSpecies));
        return d != null ? d.doubleValue() : 1.0;
    }
}


