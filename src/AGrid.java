public class AGrid {
  
  private int width;
  private int height;
  private int[][] grid;
  
  public AGrid(int width, int height) {
    this.width = width;
    this.height = height;
    updateSize();
  }
  
  public void setHeight(int height) {
    this.height = height;
    updateSize();
  }
  
  public void setWidth(int width) {
    this.width = width;
    updateSize();
  }
  
  public int getWidth() {
    return width;
  }
  
  public int getHeight() {
    return height;
  }
  
  public void updateSize() {
    this.grid = new int[width][height];
  }
  
  public int getDataAtPosition(int x, int y) {
    try {
      return grid[x][y];
    } catch (Exception e) {
      System.out.println("impossible!");
      System.exit(2);
      return 0;
    }
  }
  
  public void setDataAtPosition(int x, int y, int value){
    try {
      grid[x][y] = value;
    } catch (Exception e) {
      return;
    }
  }
  
  public int[][] getGrid() {
    return grid;
  }
  
}