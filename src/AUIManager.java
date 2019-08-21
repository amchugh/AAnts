import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferStrategy;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

class AUIManager implements KeyListener {
  
  private ACanvas canvas;
  private JFrame window;
  private Dimension window_size; // The size of the window to generate
  private Dimension camera_size; // The size of the image to draw on the window
                                 // Having an image size smaller than the window
                                 // Allows for more detail to be visible
  private Dimension simulation_size;
  private ACamera camera;
  
  private boolean[] movements;
  
//  private static final double speed = 300000;
  private static final double speed = 300d;
  
  private float[] camera_sizes = {0.1f, 0.25f, 0.4f, 0.5f, 0.75f, 0.8f, 0.9f, 1f, 1.1f, 1.25f, 1.5f, 1.75f, 2f, 3f, 4f, 5f, 10f, 20f, 30f, 40f};
  private int current_camera_size_index = 7;
  
  private float[] update_speeds = {1, 50, 100, 200, 400, 500, 1000, 2000, 5000, 10000, 100000, 500000};
  private int current_update_speed_index = 5;
  private double new_update_speed = -1d;
  
  private ArrayList<AAnt> recent_ants;
  private AGrid recent_grid;
  
  public AUIManager(Dimension window_size, Dimension camera_size, Dimension simulation_size) {
    this.window_size = window_size;
    this.camera_size = camera_size;
    this.simulation_size = simulation_size;
    
    // -------------------
    // -- Setup Display --
    // -------------------
    
    // Create a window!
    window = new JFrame();
    // Let's set that window up
    window.setTitle("Aidan's Ants");
    window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    window.setResizable(false);
    
    // Let's create a canvas that we can draw to
    canvas = new ACanvas(this, this.window_size);
    canvas.setFocusable(false);
    canvas.setPreferredSize(this.window_size);
  
    // Add the canvas to our window
    window.add(canvas);
    // Prepare for display
    window.pack();
    // Can use the following line to set the default location
    //window.setLocation(0, 0);
    // Set the window visible!
    window.setVisible(true);
  
    // Attach our keylistener
    window.addKeyListener(this);
    
    // ------------------
    // -- Setup Camera --
    // ------------------
    int[] size = getCameraSize();
    camera = new ACamera(0,0, size[0], size[1], simulation_size);
    movements = new boolean[4];
  }
  
  private float getCameraMultiplier() {
    return camera_sizes[current_camera_size_index];
  }
  
  private int[] getCameraSize() {
    int[] i = new int[2];
    i[0] = (int)Math.floor(camera_size.width * getCameraMultiplier());
    if (i[0] > simulation_size.width) {
      i[0] = simulation_size.width;
    }
    i[1] = (int)Math.floor(camera_size.height * getCameraMultiplier());
    if (i[1] > simulation_size.height) {
      i[1] = simulation_size.height;
    }
    return i;
  }
  
  private void resizeCamera() {
    int[] size = getCameraSize();
    camera.resize(size[0], size[1]);
  }
  
  public void draw() {
    canvas.repaint();
  }
  
  public void update(double simTime, ArrayList<AAnt> new_ants, AGrid new_grid) {
    
    this.recent_ants = new_ants;
    this.recent_grid = new_grid;
    
    double xVel = 0;
    double yVel = 0;
    if (movements[0]) {
      yVel -= 1;
    }
    if (movements[1]) {
      yVel += 1;
    }
    if (movements[2]) {
      xVel -= 1;
    }
    if (movements[3]) {
      xVel += 1;
    }
    
    camera.move(xVel / 1e9 * simTime * speed, yVel / 1e9 * simTime * speed);
  }
  
  public void handleKey(KeyEvent e, boolean pressed) {
    int keyCode = e.getKeyCode();
    
    switch( keyCode ) {
      case KeyEvent.VK_UP:
        // handle up
        movements[0] = pressed;
        break;
      case KeyEvent.VK_DOWN:
        // handle down
        movements[1] = pressed;
        break;
      case KeyEvent.VK_LEFT:
        // handle left
        movements[2] = pressed;
        break;
      case KeyEvent.VK_RIGHT :
        // handle right
        movements[3] = pressed;
        break;
      case KeyEvent.VK_W:
        // handle up
        movements[0] = pressed;
        break;
      case KeyEvent.VK_S:
        // handle down
        movements[1] = pressed;
        break;
      case KeyEvent.VK_A:
        // handle left
        movements[2] = pressed;
        break;
      case KeyEvent.VK_D :
        // handle right
        movements[3] = pressed;
        break;
      case KeyEvent.VK_MINUS :
        if (pressed) {
          current_camera_size_index--;
          if (current_camera_size_index < 0) {
            current_camera_size_index = 0;
          }
          resizeCamera();
        }
        break;
      case KeyEvent.VK_EQUALS :
        if (pressed) {
          current_camera_size_index++;
          if (current_camera_size_index == camera_sizes.length) {
            current_camera_size_index = camera_sizes.length - 1;
          }
          resizeCamera();
        }
        break;
      case KeyEvent.VK_Q :
        if (pressed) {
          current_camera_size_index--;
          if (current_camera_size_index < 0) {
            current_camera_size_index = 0;
          }
          resizeCamera();
        }
        break;
      case KeyEvent.VK_E :
        if (pressed) {
          current_camera_size_index++;
          if (current_camera_size_index == camera_sizes.length) {
            current_camera_size_index = camera_sizes.length - 1;
          }
          resizeCamera();
        }
        break;
      case KeyEvent.VK_R :
        if (pressed) {
          current_update_speed_index++;
          if (current_update_speed_index == update_speeds.length) {
            current_update_speed_index = update_speeds.length - 1;
          }
          new_update_speed = update_speeds[current_update_speed_index];
        }
        break;
      case KeyEvent.VK_F :
        if (pressed) {
          current_update_speed_index--;
          if (current_update_speed_index < 0) {
            current_update_speed_index = 0;
          }
          new_update_speed = update_speeds[current_update_speed_index];
        }
        break;
    }
  }
  
  public void keyPressed(KeyEvent e) {
    handleKey(e, true);
  }
  
  @Override
  public void keyReleased(KeyEvent e) {
    handleKey(e, false);
  }
  @Override
  public void keyTyped(KeyEvent e) {}
  
  public double getNewSpeed() {
    double d = new_update_speed;
    new_update_speed = -1d;
    return d;
  }
  
  public ACamera getCamera() {
    return camera;
  }
  
  public AGrid getRecentGrid() {
    return recent_grid;
  }
  
  public ArrayList<AAnt> getRecentAnts() {
    return recent_ants;
  }
  
}

class ACanvas extends JPanel {
  
  private AUIManager manager;
  private Dimension window_size;
  
  public ACanvas (AUIManager manager, Dimension window_size) {
    this.manager = manager;
    this.window_size = window_size;
  }
  
  protected void paintComponent (Graphics g) {
    ACamera camera = manager.getCamera();
    AGrid grid = manager.getRecentGrid();
    ArrayList<AAnt> ants = manager.getRecentAnts();
    
    BufferedImage image = camera.generateImage(grid, ants);
    Color fontColor = new Color(255,255,255);
    Font font = new Font("Serif", Font.PLAIN, 16);
    String text = "Test";
  
    /*
    BufferStrategy b = this.getBufferStrategy();
    if (b == null) {
      this.createBufferStrategy(1);
      b = this.getBufferStrategy();
    }
  
    Graphics g = b.getDrawGraphics();
    */
  
    g.drawImage(image, 0, 0, window_size.width, window_size.height, null);
  
    g.setColor(fontColor);
    g.setFont(font);
    g.drawString(text, 100, 100);
  
    //g.dispose();
    //b.show();
    
  }
  
}