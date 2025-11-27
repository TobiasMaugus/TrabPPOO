public class SpeciesRateConfig {
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