import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.util.ArrayList;

public class ACamera {
  
  public double pos_x;
  public double pos_y;
  private int width;
  private int height;
  public Dimension simulation_size;
  
  private int new_width;
  private int new_height;
  private boolean do_update_size;
  
  public ACamera(double x, double y, int width, int height, Dimension simulation_size) {
    this.pos_x = x;
    this.pos_y = y;
    this.width = width;
    this.height = height;
    this.simulation_size = simulation_size;
  }
  
  public void resize(int new_width, int new_height) {
    this.new_height = new_height;
    this.new_width = new_width;
    do_update_size = true;
  }
  
  private void performResize() {
    // Before we just set the new width and height, we need to get the camera position change
    int delta_x = width - new_width;
    int delta_y = height - new_height;
    // Now move the camera. We use this function to respect the boundaries
    move(delta_x/2d, delta_y/2d);
    // Finally we can set the new width and height
    width = new_width;
    height = new_height;
    do_update_size = false;
  }
  
  public BufferedImage generateImage(AGrid grid, ArrayList<AAnt> ants) {
    BufferedImage image = new BufferedImage(
      (int)width,
      (int)height,
      BufferedImage.TYPE_INT_RGB);
    
    int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
  
    int[][] c_grid = grid.getGrid();
    assert (pixels.length == c_grid[0].length * c_grid.length);
    
//    int grid_width = c_grid[0].length;
    
    for (int dx = 0; dx < width; dx++) {
      for (int dy = 0; dy < height; dy++) {
        int c;
        int val = -1;
        try {
          val = c_grid[dx + (int) pos_x][dy + (int) pos_y];
        } catch (Exception e) {
          System.out.println("Grid failed : " +
            Integer.toString(dx + (int) pos_x) + " " + Integer.toString(dy + (int) pos_y));
        }
        if (val == AAntSimulation.wall_black) {
          c = 0x696969;
        } else if (val == AAntSimulation.wall_perm) {
          c = 0x2b5743;
        } else {
          c = 0x0;
        }
        try {
          pixels[dx + (dy * width)] = c;
        } catch (Exception e) {
          System.out.println("Failed : " +
            Integer.toString(dx) + " " + Integer.toString(dy));
          System.out.println("Index: " + Integer.toString(dx + (dy * width)));
        }
      }
    }
  
    for (AAnt ant : ants) {
      int ax = ant.getX();
      int ay = ant.getY();
      if (isPointVisible(ax, ay)) {
        int c = 0xff0000;
        if (ant.isReversed()) c = 0x0000ff;
        if (ant.isQueen) c = 0xeaff00;
        try {
          pixels[(ax - (int) pos_x) + ((ay - (int) pos_y) * width)] = c;
        } catch (Exception e) {
          System.out.println("Failed to draw ant");
        }
      } else {
//        System.out.println("Ant offscreen");
      }
    }
  
    // Check to see if image size is to be updated
    if (do_update_size) {
      performResize();
    }
    
    return image;
  }
  
  public void move(double xmov, double ymov) {
    pos_x += xmov;
    if (pos_x < 0) {
      pos_x = 0;
    } else if (pos_x > simulation_size.width - width) {
      pos_x = simulation_size.width - width;
    }
    pos_y += ymov;
    if (pos_y < 0) {
      pos_y = 0;
    } else if (pos_y > simulation_size.height - height) {
      pos_y = simulation_size.height - height;
    }
  }
  
  private boolean isPointVisible(int px, int py) {
    if (px < pos_x) {
      return false;
    }
    if (px - (int)pos_x >= width) {
      return false;
    }
    if (py < pos_y) {
      return false;
    }
    if (py - (int)pos_y >= height) {
      return false;
    }
    return true;
  }
  
}
