import java.util.Iterator;

/**
 * Representa uma grade retangular do campo de simulação,
 * onde cada posição pode armazenar um único animal.
 * Também mantém um mapa de células de água, impedindo
 * que animais ocupem esses espaços.
 */
public class Field {
    /** Gerador de números aleatórios usado para posições adjacentes. */
    private static final java.util.Random rand = new java.util.Random();

    /** Profundidade (número de linhas) e largura (número de colunas) do campo. */
    private int depth, width;

    /** Matriz que armazena os animais presentes no campo. */
    private Animal[][] field;

    /** Máscara que indica quais posições são água (true = água/lago). */
    private boolean[][] water;

    /**
     * Constrói um campo com dimensões especificadas.
     * @param depth Número de linhas.
     * @param width Número de colunas.
     */
    public Field(int depth, int width) {
        this.depth = depth;
        this.width = width;
        field = new Animal[depth][width];
        water = new boolean[depth][width];
    }

    /**
     * Esvazia o campo, removendo todos os animais.
     */
    public void clear() {
        for (int row = 0; row < depth; row++) {
            for (int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }

    /**
     * Coloca um animal em uma posição específica.
     * @param animal Animal a ser posicionado.
     * @param location Local onde será colocado.
     */
    public void place(Animal animal, Location location) {
        place(animal, location.getRow(), location.getCol());
    }

    /**
     * Coloca um animal em uma posição (linha, coluna).
     * @param animal Animal a ser posicionado.
     * @param row Linha.
     * @param col Coluna.
     */
    public void place(Animal animal, int row, int col) {
        field[row][col] = animal;
    }

    /**
     * Retorna o animal na posição especificada, caso exista.
     * @param location Posição desejada.
     * @return Animal encontrado ou null.
     */
    public Animal getObjectAt(Location location) {
        return getObjectAt(location.getRow(), location.getCol());
    }

    /**
     * Retorna o animal na posição (linha, coluna), caso exista.
     */
    public Animal getObjectAt(int row, int col) {
        return field[row][col];
    }

    /**
     * Marca uma região retangular como lago, impedindo presença de animais.
     * @param centerRow Linha central.
     * @param centerCol Coluna central.
     * @param lakeHeight Altura do lago.
     * @param lakeWidth Largura do lago.
     */
    public void addLake(int centerRow, int centerCol, int lakeHeight, int lakeWidth) {
        if (lakeHeight <= 0 || lakeWidth <= 0) return;
        int halfH = lakeHeight / 2;
        int halfW = lakeWidth / 2;
        int startRow = Math.max(0, centerRow - halfH);
        int endRow = Math.min(depth - 1, centerRow + halfH);
        int startCol = Math.max(0, centerCol - halfW);
        int endCol = Math.min(width - 1, centerCol + halfW);

        for (int r = startRow; r <= endRow; r++) {
            for (int c = startCol; c <= endCol; c++) {
                water[r][c] = true;
                field[r][c] = null;
            }
        }
    }

    /** Verifica se a posição informada é água. */
    public boolean isWater(Location location) {
        return isWater(location.getRow(), location.getCol());
    }

    /** Verifica se a posição (linha, coluna) é água. */
    public boolean isWater(int row, int col) {
        return water[row][col];
    }

    /**
     * Gera uma posição aleatória adjacente à informada, dentro dos limites.
     * Pode retornar a própria posição original.
     */
    public Location randomAdjacentLocation(Location location) {
        int row = location.getRow();
        int col = location.getCol();

        int nextRow = row + rand.nextInt(3) - 1;
        int nextCol = col + rand.nextInt(3) - 1;

        if (nextRow < 0 || nextRow >= depth || nextCol < 0 || nextCol >= width) {
            return location;
        }
        else if (nextRow != row || nextCol != col) {
            if (!isWater(nextRow, nextCol)) {
                return new Location(nextRow, nextCol);
            } else {
                return location;
            }
        }
        else {
            return location;
        }
    }

    /**
     * Encontra uma posição livre adjacente (não água). Caso não haja,
     * retorna a própria posição se estiver livre; senão, null.
     */
    public Location freeAdjacentLocation(Location location) {
        Iterator<Location> adjacent = adjacentLocations(location);
        while (adjacent.hasNext()) {
            Location next = adjacent.next();
            if (!isWater(next) && field[next.getRow()][next.getCol()] == null) {
                return next;
            }
        }
        if (!isWater(location) && field[location.getRow()][location.getCol()] == null) {
            return location;
        } else {
            return null;
        }
    }

    /**
     * Encontra posição livre adjacente que seja água.
     */
    public Location freeAdjacentWaterLocation(Location location) {
        Iterator<Location> adjacent = adjacentLocations(location);
        while (adjacent.hasNext()) {
            Location next = adjacent.next();
            if (isWater(next) && field[next.getRow()][next.getCol()] == null) {
                return next;
            }
        }
        if (isWater(location) && field[location.getRow()][location.getCol()] == null) {
            return location;
        }
        return null;
    }

    /**
     * Retorna localização adjacente aleatória que seja água,
     * ou a atual caso nenhuma exista.
     */
    public Location randomAdjacentWaterLocation(Location location) {
        java.util.Iterator<Location> adjacent = adjacentLocations(location);
        while (adjacent.hasNext()) {
            Location next = adjacent.next();
            if (isWater(next)) {
                return next;
            }
        }
        return location;
    }

    /**
     * Gera um iterador para posições adjacentes embaralhadas.
     */
    public java.util.Iterator<Location> adjacentLocations(Location location) {
        int row = location.getRow();
        int col = location.getCol();
        java.util.ArrayList<Location> locations = new java.util.ArrayList<>();

        for (int roffset = -1; roffset <= 1; roffset++) {
            int nextRow = row + roffset;
            if (nextRow >= 0 && nextRow < depth) {
                for (int coffset = -1; coffset <= 1; coffset++) {
                    int nextCol = col + coffset;
                    if (nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }

        java.util.Collections.shuffle(locations, rand);
        return locations.iterator();
    }

    /** @return Profundidade do campo. */
    public int getDepth() {
        return depth;
    }

    /** @return Largura do campo. */
    public int getWidth() {
        return width;
    }
}
