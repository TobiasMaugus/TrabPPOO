import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;

/**
 * Represent a rectangular grid of field positions.
 * Each position is able to store a single animal.
 * 
 * @author David J. Barnes and Michael Kolling
 * @version 2002-04-09
 */
public class Field
{
    private static final Random rand = new Random();
    
    // The depth and width of the field.
    private int depth, width;
    // Storage for the animals.
    private Object[][] field;
    // Mask of water cells (true means water/lake)
    private boolean[][] water;

    /**
     * Represent a field of the given dimensions.
     * @param depth The depth of the field.
     * @param width The width of the field.
     */
    public Field(int depth, int width)
    {
        this.depth = depth;
        this.width = width;
        field = new Object[depth][width];
        water = new boolean[depth][width];
    }
    
    /**
     * Empty the field.
     */
    public void clear()
    {
        for(int row = 0; row < depth; row++) {
            for(int col = 0; col < width; col++) {
                field[row][col] = null;
            }
        }
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param row Row coordinate of the location.
     * @param col Column coordinate of the location.
     */
    public void place(Object animal, int row, int col)
    {
        place(animal, new Location(row, col));
    }
    
    /**
     * Place an animal at the given location.
     * If there is already an animal at the location it will
     * be lost.
     * @param animal The animal to be placed.
     * @param location Where to place the animal.
     */
    public void place(Object animal, Location location)
    {
        field[location.getRow()][location.getCol()] = animal;
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param location Where in the field.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(Location location)
    {
        return getObjectAt(location.getRow(), location.getCol());
    }
    
    /**
     * Return the animal at the given location, if any.
     * @param row The desired row.
     * @param col The desired column.
     * @return The animal at the given location, or null if there is none.
     */
    public Object getObjectAt(int row, int col)
    {
        return field[row][col];
    }

    /**
     * Define uma região retangular de lago centrada em (centerRow, centerCol)
     * com dimensões lakeHeight x lakeWidth. Valores fora dos limites são ignorados.
     */
    public void setLake(int centerRow, int centerCol, int lakeHeight, int lakeWidth)
    {
        if(lakeHeight <= 0 || lakeWidth <= 0) return;
        int halfH = lakeHeight / 2;
        int halfW = lakeWidth / 2;
        int startRow = Math.max(0, centerRow - halfH);
        int endRow = Math.min(depth - 1, centerRow + halfH);
        int startCol = Math.max(0, centerCol - halfW);
        int endCol = Math.min(width - 1, centerCol + halfW);
        for(int r = startRow; r <= endRow; r++) {
            for(int c = startCol; c <= endCol; c++) {
                water[r][c] = true;
                field[r][c] = null; // garante que não haja animais sobre água
            }
        }
    }

    public boolean isWater(int row, int col)
    {
        return water[row][col];
    }

    public boolean isWater(Location location)
    {
        return isWater(location.getRow(), location.getCol());
    }
    
    /**
     * Generate a random location that is adjacent to the
     * given location, or is the same location.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area. This
     *         may be the same object as the location parameter.
     */
    public Location randomAdjacentLocation(Location location)
    {
        int row = location.getRow();
        int col = location.getCol();
        // Generate an offset of -1, 0, or +1 for both the current row and col.
        int nextRow = row + rand.nextInt(3) - 1;
        int nextCol = col + rand.nextInt(3) - 1;
        // Check in case the new location is outside the bounds.
        if(nextRow < 0 || nextRow >= depth || nextCol < 0 || nextCol >= width) {
            return location;
        }
        else if(nextRow != row || nextCol != col) {
            // evita retornar água; se água, permanece
            if(!isWater(nextRow, nextCol)) {
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
     * Try to find a free location that is adjacent to the
     * given location. If there is none, then return the current
     * location if it is free. If not, return null.
     * The returned location will be within the valid bounds
     * of the field.
     * @param location The location from which to generate an adjacency.
     * @return A valid location within the grid area. This may be the
     *         same object as the location parameter, or null if all
     *         locations around are full.
     */
    public Location freeAdjacentLocation(Location location)
    {
        Iterator<Location> adjacent = adjacentLocations(location);
        while(adjacent.hasNext()) {
            Location next = adjacent.next();
            if(!isWater(next) && field[next.getRow()][next.getCol()] == null) {
                return next;
            }
        }
        // check whether current location is free
        if(!isWater(location) && field[location.getRow()][location.getCol()] == null) {
            return location;
        } 
        else {
            return null;
        }
    }

    /**
     * Tenta encontrar uma localização livre adjacente que seja água.
     * Caso não haja, retorna a atual se for água e estiver livre; senão null.
     */
    public Location freeAdjacentWaterLocation(Location location)
    {
        Iterator<Location> adjacent = adjacentLocations(location);
        while(adjacent.hasNext()) {
            Location next = adjacent.next();
            if(isWater(next) && field[next.getRow()][next.getCol()] == null) {
                return next;
            }
        }
        if(isWater(location) && field[location.getRow()][location.getCol()] == null) {
            return location;
        } else {
            return null;
        }
    }

    /**
     * Gera uma localização aleatória adjacente que seja água, ou retorna a atual se nenhuma.
     */
    public Location randomAdjacentWaterLocation(Location location)
    {
        Iterator<Location> adjacent = adjacentLocations(location);
        while(adjacent.hasNext()) {
            Location next = adjacent.next();
            if(isWater(next)) {
                return next;
            }
        }
        return location;
    }

    /**
     * Generate an iterator over a shuffled list of locations adjacent
     * to the given one. The list will not include the location itself.
     * All locations will lie within the grid.
     * @param location The location from which to generate adjacencies.
     * @return An iterator over locations adjacent to that given.
     */
    public Iterator<Location> adjacentLocations(Location location)
    {
        int row = location.getRow();
        int col = location.getCol();
        LinkedList<Location> locations = new LinkedList<Location>();
        for(int roffset = -1; roffset <= 1; roffset++) {
            int nextRow = row + roffset;
            if(nextRow >= 0 && nextRow < depth) {
                for(int coffset = -1; coffset <= 1; coffset++) {
                    int nextCol = col + coffset;
                    // Exclude invalid locations and the original location.
                    if(nextCol >= 0 && nextCol < width && (roffset != 0 || coffset != 0)) {
                        locations.add(new Location(nextRow, nextCol));
                    }
                }
            }
        }
        Collections.shuffle(locations,rand);
        return locations.iterator();
    }

    /**
     * @return The depth of the field.
     */
    public int getDepth()
    {
        return depth;
    }
    
    /**
     * @return The width of the field.
     */
    public int getWidth()
    {
        return width;
    }
}
