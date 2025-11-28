import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Representa configurações por espécie (cores, multiplicadores e fatores sazonais).
 */
public class SpeciesConfigLoader{
    private static SpeciesConfigLoader instanciaUnica;

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

    /**
     * Cria uma configuração de espécie
     */
    private SpeciesConfigLoader(){
        colors = new HashMap<Animal, Color>();
        breedingMultiplierBySeasonName = new HashMap<String, Double>();
        breedingMultiplierBySeasonAndSpecies = new HashMap<String, Double>();
        predationSusceptibilityBySeasonAndSpecies = new HashMap<String, Double>();
        foodAvailabilityFactorBySeasonAndSpecies = new HashMap<String, Double>();
    }

    /**
     * Cria uma única configuração de espécies para todo o sistema. 
     * Utiliza o padrão Singleton
     */
    public static SpeciesConfigLoader getInstance(){
        if(instanciaUnica == null){
            instanciaUnica = new SpeciesConfigLoader();
        }
        return instanciaUnica;
    }

    /**
     * Gera chave para season:specie, com base no nome da estação e nome da espécie
     *  
     */
    private static String key(String seasonName, Animal species){
        return key(seasonName, species.getClass().getSimpleName());
    }

    private static String key(String seasonName, String speciesName){
        return seasonName.toLowerCase() + ":" + speciesName.toLowerCase();
    }

    /**
     * Retorna a cor configurada para a espécie informada.
     *
     * @param animalClass Instância da espécie usada como chave.
     * @return A cor associada ou cinza caso não exista configuração.
     */
    public Color getColor(Animal animalClass){
        Color color = colors.get(animalClass);
        return color != null ? color : Color.gray;
    }

    /**
     * Define a cor usada para representar a espécie.
     *
     * @param animalClass Espécie associada à cor.
     * @param color Cor definida para exibição.
     */
    public void setColor(Animal animalClass, Color color){
        colors.put(animalClass, color);
    }

    /**
     * Retorna o multiplicador geral de reprodução para uma estação.
     * 
     * @param seasonName Nome da estação (case-insensitive).
     * @return Multiplicador configurado ou 1.0 caso não exista.
     */
    private double getBreedingMultiplier(String seasonName){
        Double d = breedingMultiplierBySeasonName.get(seasonName.toLowerCase());
        return d != null ? d : 1.0;
    }

    /**
     * Define um multiplicador geral de reprodução para uma estação.
     *
     * @param seasonName Nome da estação.
     * @param multiplier Fator aplicado a todas as espécies nessa estação.
     */
    public void setBreedingMultiplier(String seasonName, double multiplier){
        breedingMultiplierBySeasonName.put(seasonName.toLowerCase(), multiplier);
    }

    /**
     * Define um multiplicador de reprodução específico para uma estação e nome da espécie.
     *
     * @param seasonName Nome da estação.
     * @param speciesName Nome simples da espécie.
     * @param multiplier Fator aplicado somente à espécie indicada.
     */
    public void setBreedingMultiplierFor(String seasonName, String speciesName, double multiplier){
        breedingMultiplierBySeasonAndSpecies.put(key(seasonName, speciesName), multiplier);
    }

    /**
     * Retorna o multiplicador de reprodução para a espécie em uma estação.
     *
     * @param seasonName Nome da estação.
     * @param species Espécie consultada.
     * @return Multiplicador específico ou geral caso não configurado. Caso não exista configuração específica, retorna o multiplicador geral.
     */
    public double getBreedingMultiplierFor(String seasonName, Animal species){
        Double d = breedingMultiplierBySeasonAndSpecies.get(key(seasonName, species));
        return d != null ? d : getBreedingMultiplier(seasonName);
    }

    /**
     * Define a susceptibilidade à predação usando o nome de uma espécie.
     *
     * @param seasonName Nome da estação.
     * @param speciesName Nome simples da espécie presa.
     * @param probability Probabilidade de predação (0 a 1).
     */
    public void setPredationSusceptibility(String seasonName, String speciesName, double probability){
        predationSusceptibilityBySeasonAndSpecies.put(key(seasonName, speciesName), probability);
    }

    /**
     * Retorna a susceptibilidade à predação configurada para a espécie.
     *
     * @param seasonName Nome da estação.
     * @param preySpecies Espécie presa consultada.
     * @return Probabilidade de predação entre 0 e 1. Caso não exista configuração, retorna 1.0.
     */
    public double getPredationSusceptibility(String seasonName, Animal preySpecies){
        Double d = predationSusceptibilityBySeasonAndSpecies.get(key(seasonName, preySpecies));
        return d != null ? d : 1.0;
    }

    /**
     * Define o fator de disponibilidade de alimento usando apenas o nome da espécie.
     *
     * @param seasonName Nome da estação.
     * @param speciesName Nome simples da espécie predadora.
     * @param factor Fator de disponibilidade (>= 0).
     */
    public void setFoodAvailabilityFactor(String seasonName, String speciesName, double factor){
        foodAvailabilityFactorBySeasonAndSpecies.put(key(seasonName, speciesName), factor);
    }

    /**
     * Retorna o fator de disponibilidade de alimento para o predador na estação.
     *
     * @param seasonName Nome da estação.
     * @param predatorSpecies Espécie predadora.
     * @return Fator de disponibilidade de alimento. Retorna 1.0 caso não exista configuração.
     */
    public double getFoodAvailabilityFactor(String seasonName, Animal predatorSpecies){
        Double d = foodAvailabilityFactorBySeasonAndSpecies.get(key(seasonName, predatorSpecies));
        return d != null ? d : 1.0;
    }

}


