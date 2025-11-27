public class LakeConfig {
    private int centerRow, centerCol, height, width;
    
    public LakeConfig(int centerRow, int centerCol, int height, int width) {
        this.centerRow = centerRow;
        this.centerCol = centerCol;
        this.height = height;
        this.width = width;
    }
    
    public int getCenterRow() { return centerRow; }
    public int getCenterCol() { return centerCol; }
    public int getHeight() { return height; }
    public int getWidth() { return width; }
}