import java.awt.*;
import java.util.ArrayList;
import java.util.Random;

public class AAntSimulation implements Runnable {
  
  public static void main(String[] args) {
    
    try {
      int ssize = Integer.parseInt(args[1]);
      simulation_size = new Dimension(ssize, ssize);
      starting_camera_size = new Dimension(ssize/2, ssize/2);
    } catch (Exception e) {
    
    }
    try {
      int wsize = Integer.parseInt(args[0]);
      window_size = new Dimension(wsize, wsize);
    } catch (Exception e) {
    
    }
    int numants = 30;
    try {
      numants = Integer.parseInt(args[2]);
    } catch (Exception e) {
    
    }
    new AAntSimulation(numants);
  }
  
  private double delayTime;
  private static Dimension simulation_size = new Dimension(1000,1000);
  private static Dimension window_size = new Dimension(1000,1000);
  private static Dimension starting_camera_size = new Dimension(500,500);
  
  private ArrayList<AAnt> ants;
  
  public static int wall_blank = 0;
  public static int wall_black = 1;
  public static int wall_perm  = 2;
  
  private AUIManager ui;
  private AGrid grid;
  
  private long updateNumber;
  
  public AAntSimulation(int number_of_ants) {
    // Create the UI Manager
    ui = new AUIManager(window_size, starting_camera_size, simulation_size);
  
    // ------------------------------------
    // -- Setup stuff for the simulation --
    // ------------------------------------
    grid = new AGrid(simulation_size.width, simulation_size.height);
    delayTime = calculateDelayTime(500);
    ants = new ArrayList<AAnt>();
    
    Random r = new Random();
    
    for (int i = number_of_ants; i > 0; i--) {
      addAntAtRandomPositon(r);
    }
    
    new Thread(this).start();
  }
  
  public void addAntAtRandomPositon(Random r) {
    int x = randomInRange(r, 0, simulation_size.width);
    int y = randomInRange(r, 0, simulation_size.height);
    int wpt = (r.nextBoolean()?wall_black:wall_perm);
    wpt = wall_black;
    int lives = randomInRange(r, 10000, 100000);
    lives = -1;
    addAnt(x,y, r.nextBoolean(), wpt, r, lives);
  }
  
  public void addAnt(int x, int y, boolean isReversed, int wall_place_type, Random r, int lives) {
    AAnt a = new AAnt(x, y, isReversed, simulation_size, wall_place_type, lives);
    a.setRandomRotation(r);
    ants.add(a);
  }
  
  public void run() {
    long pastUpdateTime = System.nanoTime();
    long lastUIUpdate = System.nanoTime();
    
    while (true) {
    
      long time = System.nanoTime();
      
      if (time - pastUpdateTime > delayTime) {
        pastUpdateTime += delayTime;
        tick();
        
        double new_speed = ui.getNewSpeed();
        if (new_speed != -1d) {
          delayTime = calculateDelayTime(new_speed);
        }
      }
      
      ui.update(time - lastUIUpdate, ants, grid, updateNumber);
      lastUIUpdate = time;
      
    }
  }
  
  public static int randomInRange(Random r, int min, int max) {
    return r.nextInt((max - min) + 1) + min;
  }
  
  private void tick() {
  
    ArrayList<AAnt> survivors = new ArrayList<AAnt>();
  
    // We need to iterate over every existing AAnt
    if (ants.size() == 0) return;
    for (AAnt ant : ants) {
      // Move the ant, rotate the ant, then update block pos
      ant.move();
      
      int d = grid.getDataAtPosition(ant.getX(), ant.getY());

      if (d == wall_blank) {
        ant.rotate(false);
        grid.setDataAtPosition(ant.getX(), ant.getY(), ant.getWallPlaceType());
      }

      if (d == wall_black) {
        ant.rotate(true);
        grid.setDataAtPosition(ant.getX(), ant.getY(), wall_blank);
      }

      if (d == wall_perm) {
        ant.rotate(true);
        if (ant.getWallPlaceType() == wall_perm) {
          grid.setDataAtPosition(ant.getX(), ant.getY(), wall_blank);
        }
      }
      
      if (ant.isQueen) {
        AAnt n = ant.queenTick();
        if (n != null) {
          survivors.add(n);
        }
      }
      
    }
    
    for (AAnt ant: ants) {
      if (ant.isAntAlive()) {
        survivors.add(ant);
      }
    }
    
    if (ants.size() > 0 && survivors.size() == 0) {
      System.out.println("All my ants are dead. push me to the edge");
    }
    
    ants = survivors;
    updateNumber++;
    
  }
  
//  private BufferedImage generateImage() {
//    BufferedImage image = new BufferedImage(
//      simulation_size.width * pixel_size,
//      simulation_size.height * pixel_size,
//      BufferedImage.TYPE_INT_RGB);
//    int[] pixels = ((DataBufferInt)image.getRaster().getDataBuffer()).getData();
//
//    int[][] c_grid = grid.getGrid();
//
//    for (int pos_x = 0; pos_x < simulation_size.width; pos_x++) {
//      for (int pos_y = 0; pos_y < simulation_size.height; pos_y++) {
//        int c;
//        if (c_grid[pos_x][pos_y] == wall_black) {
//          c = 0x696969;
//        } else if (c_grid[pos_x][pos_y] == wall_perm) {
//          c = 0x2b5743;
//        } else {
//          c = 0x0;
//        }
//        for (int i1 = 0; i1 < pixel_size; i1++) {
//          for (int i2 = 0; i2 < pixel_size; i2++) {
//            pixels[((pos_x) * pixel_size) + i1 + ((pos_y * pixel_size + i2) * simulation_size.width * pixel_size)] = c;
//          }
//        }
//      }
//    }
//
//    for (AAnt ant : ants) {
//      int pos_x = ant.getX();
//      int pos_y = ant.getY();
//      int c = 0xff0000;
//      if ( ant.isReversed()) c = 0x0000ff;
//      if ( ant.isQueen) c = 0xeaff00;
//      try {
//        for (int i1 = 0; i1 < pixel_size; i1++) {
//          for (int i2 = 0; i2 < pixel_size; i2++) {
//            pixels[((pos_x) * pixel_size) + i1 + ((pos_y * pixel_size + i2) * simulation_size.width * pixel_size)] = c;
//          }
//        }
//      } catch (Exception e) {
//      }
//    }
//
//    return image;
//  }
  
  private void draw() {
  
  }
  
  private double calculateDelayTime(double updates_per_second) {
    return 1e9/updates_per_second;
  }
  
  
  
}

class AAnt {
  
  public int x;
  public int y;
  public enum Direction {UP, DOWN, LEFT, RIGHT};
  public Direction facing;
  private boolean reversed = false;
  private Dimension sim_size;
  private int wall_place_type;
  
  public boolean isQueen;
  private int queenSpawnCooldown;
  private static final int queenSpawnCooldownTime = 1;
  
  private int life;
  
  public AAnt(int x, int y, boolean reversed, Dimension sim_size, int wall_place_type, int lives) {
    this.x = x;
    this.y = y;
    this.reversed = reversed;
    this.sim_size = sim_size;
    this.wall_place_type = wall_place_type;
    this.life = lives;
    facing = Direction.UP;
    queenSpawnCooldown = queenSpawnCooldownTime;
  }
  
  public AAnt queenTick() {
    queenSpawnCooldown--;
    if (queenSpawnCooldown <= 0) {
      queenSpawnCooldown = queenSpawnCooldownTime;
      return createChildAnt(new Random(), 100);
    }
    return null;
  }
  
  public AAnt createChildAnt(Random r, int lives) {
    AAnt ant = new AAnt(x,y,reversed,sim_size,wall_place_type,lives);
    ant.setRandomRotation(r);
    return ant;
  }
  
  public void setRandomRotation(Random r) {
    int d = AAntSimulation.randomInRange(r, 0, 3);
    facing = Direction.values()[d];
  }
  
  public void move() {
    switch(facing) {
      case UP:
        this.y++;
        break;
      case DOWN:
        this.y--;
        break;
      case LEFT:
        this.x--;
        break;
      case RIGHT:
        this.x++;
        break;
    }
    if (x < 0) {
      x = sim_size.width - 1;
    } else if (x >= sim_size.width) {
      x = 0;
    }
    if (y < 0) {
      y = sim_size.height - 1;
    } else if (y >= sim_size.height) {
      y = 0;
    }
    if (isQueen) return;
    life--;
  }
  
  public int getX() {
    return x;
  }
  
  public int getY() {
    return y;
  }
  
  public void rotate(boolean doLeft) {
    if (reversed) {
      if (doLeft) {
        rotateRight();
        return;
      }
      rotateLeft();
      return;
    }
    
    if (doLeft) {
      rotateLeft();
      return;
    }
    rotateRight();
    return;
  }
  
  public void rotateLeft() {
    switch(facing) {
      case UP:
        facing = Direction.LEFT;
        return;
      case DOWN:
        facing = Direction.RIGHT;
        return;
      case LEFT:
        facing = Direction.DOWN;
        return;
      case RIGHT:
        facing = Direction.UP;
        return;
    }
  }
  
  public void rotateRight() {
    switch(facing) {
      case UP:
        facing = Direction.RIGHT;
        return;
      case DOWN:
        facing = Direction.LEFT;
        return;
      case LEFT:
        facing = Direction.UP;
        return;
      case RIGHT:
        facing = Direction.DOWN;
        return;
    }
  }
  
  public boolean isReversed() {
    return reversed;
  }
  
  public int getWallPlaceType() {
    return wall_place_type;
  }
  
  public boolean isAntAlive() {
    if (life == 0)
      return false;
    return true;
  }
  
}