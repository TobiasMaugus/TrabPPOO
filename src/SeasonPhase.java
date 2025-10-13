import java.awt.Color;

/**
 * Representa uma estação com duração e estética (cores do ambiente).
 */
public class SeasonPhase {
    private final String name;
    private final int durationSteps;
    private final Color emptyColor;
    private final Color waterColor;

    public SeasonPhase(String name, int durationSteps, Color emptyColor, Color waterColor) {
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


