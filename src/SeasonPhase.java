import java.awt.Color;

/**
 * Representa uma fase da estação, com duração e cores do ambiente.
 */
public class SeasonPhase {
    private final String name;        
    private final int durationSteps;  
    private final Color emptyColor;   
    private final Color waterColor;   

    /**
     * Cria uma estação.
     *
     * @param name Nome da estação.
     * @param durationSteps Duração em quantidade de steps.
     * @param emptyColor Cor do vazio.
     * @param waterColor Cor da água.
     */
    public SeasonPhase(String name, int durationSteps, Color emptyColor, Color waterColor){
        this.name = name;
        this.durationSteps = durationSteps;
        this.emptyColor = emptyColor;
        this.waterColor = waterColor;
    }

    /** Getters básicos. */

    public String getName(){ 
        return name; 
    }
    
    public int getDurationSteps(){
        return durationSteps; 
    }

    public Color getEmptyColor(){ 
        return emptyColor; 
    }

    public Color getWaterColor(){ 
        return waterColor; 
    }
}



