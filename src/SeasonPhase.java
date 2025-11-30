import java.awt.Color;

/**
 * Representa uma fase da estação, com duração e cores do ambiente.
 */
public class SeasonPhase {
    /** O nome identificador desta estação. */
    private final String name; 
    
    /** A duração desta estação em passos da simulação. */
    private final int durationSteps; 
    
    /** A cor de fundo para células vazias (terra) nesta estação. */
    private final Color emptyColor; 
    
    /** A cor de fundo para células de água nesta estação. */
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

    /**
     * @return O nome da estação.
     */
    public String getName(){ 
        return name; 
    }
    
    /**
     * @return A duração da estação em passos.
     */
    public int getDurationSteps(){
        return durationSteps; 
    }

    /**
     * @return A cor associada ao vazio (terra) nesta estação.
     */
    public Color getEmptyColor(){ 
        return emptyColor; 
    }

    /**
     * @return A cor associada à água nesta estação.
     */
    public Color getWaterColor(){ 
        return waterColor; 
    }
}