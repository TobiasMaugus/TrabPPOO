/**
 * Representa uma posição em uma grade retangular.
 * Cada localização possui uma linha e uma coluna.
 */
public class Location{
    /** Linha da posição na grade. */
    private int row;
    /** Coluna da posição na grade. */
    private int col;

    /**
     * Cria uma nova localização especificando linha e coluna.
     * @param row A linha.
     * @param col A coluna.
     */
    public Location(int row, int col){
        this.row = row;
        this.col = col;
    }
    
    /**
     * Verifica igualdade de conteúdo entre duas localizações.
     * Duas Location são iguais se possuem mesma linha e coluna.
     * @param obj O objeto a ser comparado com esta localização.
     * @return true se os objetos representarem a mesma posição (linha e coluna iguais).
     */
    @Override
    public boolean equals(Object obj){
        if(obj instanceof Location){
            Location other = (Location) obj;
            return row == other.getRow() && col == other.getCol();
        }
        else{
            return false;
        }
    }
    
    /**
     * Retorna representação textual no formato "linha,coluna".
     * @return Uma string contendo as coordenadas separadas por vírgula.
     */
    @Override
    public String toString(){
        return row + "," + col;
    }
    
    /**
     * Gera um hash code baseado nos valores de linha e coluna.
     * Usa 16 bits superiores para a linha e 16 inferiores para a coluna.
     * @return O valor inteiro do hash code.
     */
    @Override
    public int hashCode(){
        return (row << 16) + col;
    }
    
    /** @return A linha da localização. */
    public int getRow(){
        return row;
    }
    
    /** @return A coluna da localização. */
    public int getCol(){
        return col;
    }
}