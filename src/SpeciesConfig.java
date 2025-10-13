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

    public SpeciesConfig() {
        colors = new HashMap<Class<?>, Color>();
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
}


