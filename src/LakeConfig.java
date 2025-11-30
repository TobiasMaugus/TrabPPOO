/**
 * Representa a configuração de um lago dentro do campo de simulação.
 * Armazena posição central e dimensões do lago, permitindo que o
 * sistema determine sua área e localização.
 */
public class LakeConfig{
    
    /** A linha central da posição do lago. */
    private int centerRow;
    
    /** A coluna central da posição do lago. */
    private int centerCol;
    
    /** A altura (extensão vertical) do lago. */
    private int height;
    
    /** A largura (extensão horizontal) do lago. */
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

    /**
     * @return A linha central definida para este lago.
     */
    public int getCenterRow(){ 
        return centerRow; 
    }
    
    /**
     * @return A coluna central definida para este lago.
     */
    public int getCenterCol(){ 
        return centerCol; 
    }

    /**
     * @return A altura do lago.
     */
    public int getHeight(){ 
        return height; 
    }

    /**
     * @return A largura do lago.
     */
    public int getWidth(){ 
        return width; 
    }
}