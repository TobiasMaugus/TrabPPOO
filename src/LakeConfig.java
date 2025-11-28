/**
 * Representa a configuração de um lago dentro do campo de simulação.
 * Armazena posição central e dimensões do lago, permitindo que o
 * sistema determine sua área e localização.
 */
public class LakeConfig{
    
    private int centerRow;
    private int centerCol;
    private int height;
    private int width;
    
    /**
     * Cria uma configuração de lago com posição central e dimensões informadas.
     *
     * @param centerRow Linha do centro do lago.
     * @param centerCol Coluna do centro do lago.
     * @param height Altura do lago.
     * @param width Largura do lago.
     */
    public LakeConfig(int centerRow, int centerCol, int height, int width){
        this.centerRow = centerRow;
        this.centerCol = centerCol;
        this.height = height;
        this.width = width;
    }

    /** Getters básicos. */

    public int getCenterRow(){ 
        return centerRow; 
    }
    
    public int getCenterCol(){ 
        return centerCol; 
    }

    public int getHeight(){ 
        return height; 
    }

    public int getWidth(){ 
        return width; 
    }
}