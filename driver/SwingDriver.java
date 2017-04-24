package driver;

import java.io.*;
import java.util.*;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import javax.swing.*;
import board.*;
import game.Backgammon;
import player.*;
import move.*;

public class SwingDriver extends JFrame {
  
  // the Backgammon used to run this driver
  protected Backgammon backgammon;

  // the main panel, holding everything else
  protected JPanel panel;
  
  // the panel housing the board
  protected BoardPanel board;
  
  // the status panel
  protected StatusPanel status;
  
  // the layout manager
  protected GridBagLayout layout;
  
  // the human player
  protected SwingHumanPlayer player;
  
  // the human player's in-progress move
  protected Move move;
  
  // the game-thread
  protected Thread game;
  
  public SwingDriver() {
    setTitle("Comp440 Backgammon");
    setResizable(false);
    
    this.player = new SwingHumanPlayer();
    this.backgammon = new Backgammon(player, new SwingRandomPlayer()); //NeuralNetworkPlayer(this, "neural.net"));
    this.panel = new JPanel();
    this.board = new BoardPanel();
    this.status = new StatusPanel();
    this.layout = new GridBagLayout();
    
    game = new Thread("Game Thread") {
      public void run() {
        while (true) {
          try {
            backgammon.run();
          } catch (NewGameException e) {
          }
          
          backgammon.reset();
          SwingDriver.this.repaint();
        }
      }
    };
    
    game.start();
    
    JMenuBar menuBar;
    JMenu menu, submenu;
    JMenuItem menuItem;
    JRadioButtonMenuItem rbMenuItem;
    JCheckBoxMenuItem cbMenuItem;
    
    //Create the menu bar.
    menuBar = new JMenuBar();
    
    //Build the first menu.
    menu = new JMenu("File");
    menu.setMnemonic(KeyEvent.VK_Q);
    menuBar.add(menu);
    
    //a group of JMenuItems
    menuItem = new JMenuItem("Quit", KeyEvent.VK_T);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { System.exit(2); }
    });
    menu.add(menuItem);
    
    //Build second menu in the menu bar.
    menu = new JMenu("Game");
    menu.setMnemonic(KeyEvent.VK_2);
    menuBar.add(menu);
    
    menuItem = new JMenuItem("Undo Move", KeyEvent.VK_Z);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { 
        if (move != null) 
          move = new Move(backgammon.getDice(), backgammon.getCurrentBoard(), backgammon.getCurrentPlayer()); 
        
        SwingDriver.this.repaint();
      }
    });
    menu.add(menuItem);
    
    menuItem = new JMenuItem("Reset", KeyEvent.VK_T);
    menuItem.addActionListener(new ActionListener() {
      public void actionPerformed(ActionEvent e) { game.interrupt(); }
    });
    menu.add(menuItem);
    
    setJMenuBar(menuBar);
  
    GridBagConstraints d = new GridBagConstraints();
    d.gridx = 0;
    d.gridy = 0;
    
    layout.setConstraints(board, d);
    
    GridBagConstraints c = new GridBagConstraints();
    c.gridx = 0;
    c.gridy = 1;
    
    layout.setConstraints(status, c);
    
    panel.setLayout(layout);
    panel.add(board);
    panel.add(status);
    
    getContentPane().add(panel);
    pack();
    show();
  }
  
  public static void main(String[] args) throws IOException {
    System.setProperty("apple.laf.useScreenMenuBar", "true");
    System.setProperty("com.apple.mrj.application.apple.menu.about.name", "Comp440 Backgammon");
    
    SwingDriver driver = new SwingDriver();
  }
  
  /**
   * returns the current board
   *
   * @return The current board
   */
  protected Board getCurrentBoard() {
    return (move == null ? backgammon.getCurrentBoard() : move.getCurrentBoard());
  }
  
  public class BoardPanel extends JPanel implements MouseListener, MouseMotionListener {
    
    // variables defining the layout of the swing UI
    public static final int WIDTH = 540;
    public static final int HEIGHT = 360;
    public static final int BORDER = 15;
    public static final int SPIKE_WIDTH = 30;
    public static final int SPIKE_HEIGHT = 140;
    public static final int MAN_WIDTH = 30;
    public static final int MAN_HEIGHT = 30;
    public static final int OFFSET = 60;
    public static final int BAR_WIDTH = 60;
    public static final int MAX_MEN = 4;
    public static final int DICE_RANDOM = 20;
    
    public static final int BAR_WHITE_LOCATION = -5;
    public static final int BAR_BLACK_LOCATION = -6;
    
    // and random number generate
    protected Random random;
    
    // the graphics objects
    protected BufferedImage board;
    protected Image black;
    protected Image white;
    protected Image[][] dice;
    
    public BoardPanel() {
      this.random = new Random();
      
      this.board = toBufferedImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("board.gif")));
      this.black = toBufferedImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("black.gif")));
      this.white = toBufferedImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("white.gif")));
      
      this.dice = new Image[2][6];
      
      for (int j=1; j<7; j++) {
        this.dice[0][j-1] = toBufferedImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("dice-white-" + j + ".gif")));
        this.dice[1][j-1] = toBufferedImage(Toolkit.getDefaultToolkit().getImage(ClassLoader.getSystemResource("dice-black-" + j + ".gif")));
      }
        
      setSize(WIDTH, HEIGHT);
      addMouseListener(this);
      addMouseMotionListener(this);
    }
    
    public Dimension getPreferredSize() {
      return new Dimension(WIDTH, HEIGHT);
    }
    
    public void paint(Graphics g) {
      Graphics2D g2 = (Graphics2D) g;
      g2.drawImage(board, null, 0, 0);
      
      drawMen(g2);
      drawDice(g2);
      drawBar(g2);
      drawOff(g2);
      drawDragged(g2);
    }
    
    protected void drawDragged(Graphics g2) {
      if (pressed) {
        if (backgammon.getCurrentPlayer() == Board.WHITE)
          g2.drawImage(white, pressedX - MAN_WIDTH/2, pressedY - MAN_WIDTH/2, null);
        else
          g2.drawImage(black, pressedX - MAN_WIDTH/2, pressedY - MAN_WIDTH/2, null);
      }
    }
    
    protected void drawMen(Graphics2D g2) {
      for (int i=0; i<Board.NUM_SPIKES; i++) 
        drawMen(g2, i);
    }
    
    protected void drawMen(Graphics2D g2, int location) {
      int num = getCurrentBoard().getPieces(location);
      
      if (pressed && (pressedLocation == location))
        num--;
      
      for (int i=0; i<num; i++)
        drawMan(g2, location, i, getCurrentBoard().getColor(location));
    }
    
    protected void drawMan(Graphics2D g2, int location, int num, int player) {  
      int width, height;
      
      if (location < Board.NUM_SPIKES/2)
        if (location >= Board.NUM_SPIKES/4)
          width = OFFSET + ((Board.NUM_SPIKES/2 - location - 1) * SPIKE_WIDTH);
        else
          width = OFFSET + BAR_WIDTH + ((Board.NUM_SPIKES/2 - location - 1) * SPIKE_WIDTH);
      else
        if (location >= 3 * Board.NUM_SPIKES/4)
          width = OFFSET + BAR_WIDTH + ((location - Board.NUM_SPIKES/2) * SPIKE_WIDTH);
        else
          width = OFFSET + ((location - Board.NUM_SPIKES/2) * SPIKE_WIDTH);
        
      int count = (num > MAX_MEN ? MAX_MEN : num);
        
      if (location < Board.NUM_SPIKES/2) 
        height = HEIGHT - BORDER - ((count+1) * MAN_HEIGHT);
      else  
        height = BORDER + (count * MAN_HEIGHT);
      
      if (player == Board.WHITE)
        g2.drawImage(white, width, height, null);
      else
        g2.drawImage(black, width, height, null);
                   
      if (count != num) 
        drawText(g2, "" + (num+1), width+MAN_WIDTH/2, height+MAN_WIDTH/2);
    }
    
    protected void drawBar(Graphics2D g2) {
      int black = getCurrentBoard().getBar(Board.BLACK);
      if (pressed && (pressedLocation == BAR_BLACK_LOCATION))
        black--;
      
      for (int i=0; i<black; i++)
        drawBar(g2, i, Board.BLACK);
      
      int white = getCurrentBoard().getBar(Board.WHITE);
      if (pressed && (pressedLocation == BAR_WHITE_LOCATION))
        white--;
      
      for (int i=0; i<white; i++)
        drawBar(g2, i, Board.WHITE);
    }
    
    protected void drawBar(Graphics2D g2, int location, int player) {
      int x = WIDTH/2 - MAN_WIDTH/2;
      
      int count = (location > MAX_MEN ? MAX_MEN : location);
      
      int y = BORDER + (count * MAN_HEIGHT);
      
      if (player == Board.WHITE) {
        y = HEIGHT - y - MAN_HEIGHT;
        g2.drawImage(white, x, y, null);
      } else {
        g2.drawImage(black, x, y, null);
      }
      
      if (count != location) 
        drawText(g2, "" + (location+1), x+MAN_WIDTH/2, y+MAN_WIDTH/2);
    }
    
    protected void drawOff(Graphics2D g2) {
      for (int i=0; i<getCurrentBoard().getOff(Board.BLACK); i++)
        drawOff(g2, i, Board.BLACK);
      
      for (int i=0; i<getCurrentBoard().getOff(Board.WHITE); i++)
        drawOff(g2, i, Board.WHITE);
    }
    
    protected void drawOff(Graphics2D g2, int location, int player) {
      int x = WIDTH - MAN_WIDTH - (OFFSET - MAN_WIDTH)/2;
      
      int count = (location > MAX_MEN ? MAX_MEN : location);
      
      int y = BORDER + (count * MAN_HEIGHT);
      
      if (player == Board.BLACK) {
        y = HEIGHT - y - MAN_HEIGHT;
        g2.drawImage(black, x, y, null);
      } else {
        g2.drawImage(white, x, y, null);
      }
      
      if (count != location) 
        drawText(g2, "" + (location+1), x+MAN_WIDTH/2, y+MAN_WIDTH/2);
    }
    
    protected void drawDice(Graphics2D g2) {
      int index = 0;
      
      if (backgammon.getCurrentPlayer() == Board.BLACK) 
        index = 1;
          
      int first = backgammon.getDice().getDie1();
      int second = backgammon.getDice().getDie2();
      
      g2.drawImage(dice[index][first-1], (3 * (WIDTH/4)) - 30, HEIGHT/2 - 15 - 9 + (first*3), null);
      g2.drawImage(dice[index][second-1], (3 * (WIDTH/4)) + 30, HEIGHT/2 - 15 - 9 + (second*3), null);
    }
    
    protected void drawText(Graphics2D g2, String message, int x, int y) {
      Font font = new Font("Helvetica", Font.BOLD, 10);
      FontMetrics fm = g2.getFontMetrics(font);
      int width = (int) fm.getStringBounds(message, g2).getWidth();
      int height = fm.getAscent();
      
      g2.setFont(font);
      g2.setColor(new Color(0, 0, 0));
      g2.drawString(message, x - width/2, y + height/2);
    }
    
    protected boolean isSpike(int x, int y) {
      return ((((x > OFFSET) && (x <= OFFSET + 6 * SPIKE_WIDTH)) || 
               ((x > OFFSET + 6 * SPIKE_WIDTH + BAR_WIDTH) && (x <= OFFSET + 12 * SPIKE_WIDTH + BAR_WIDTH))) &&
              (((y > BORDER) && (y <= BORDER + SPIKE_HEIGHT)) ||
               ((y > HEIGHT - BORDER - SPIKE_HEIGHT) && (y <= HEIGHT - BORDER))));
    }
    
    protected boolean isDice(int x, int y) {
      return ((x > OFFSET + 6 * SPIKE_WIDTH + BAR_WIDTH) && (x <= OFFSET + 12 * SPIKE_WIDTH + BAR_WIDTH) &&
              (y > BORDER + SPIKE_HEIGHT) && (y <= HEIGHT - BORDER - SPIKE_HEIGHT));
    }
    
    protected boolean isBar(int x, int y, int player) {
      if ((x > OFFSET + 6 * SPIKE_WIDTH) && (x < OFFSET + 6 * SPIKE_WIDTH + BAR_WIDTH)) {
        if (player == Board.BLACK) 
          return (y < HEIGHT/2);
        else
          return (y > HEIGHT/2);
      } else {
        return false;
      }
    }
    
    protected int getSpike(int x, int y) {
      int xq = 0, yq = 0;
      x = x - OFFSET;
      
      if (x > 6 * SPIKE_WIDTH) {
        xq = 1;
        x = x - 6 * SPIKE_WIDTH - BAR_WIDTH;
      }
      
      if (y > HEIGHT/2)
        yq = 1;
      
      int offset = x / SPIKE_WIDTH;
            
      if (yq == 0) 
        return 12 + offset + (xq == 1 ? 6 : 0);
      else
        return 11 - offset - (xq == 1 ? 6 : 0);
    }
    
    
    
    protected boolean pressed = false;
    protected int pressedLocation = 0;
    protected int pressedX = 0;
    protected int pressedY = 0;
    
    public void mouseClicked(MouseEvent e) {}
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}
    public void mouseMoved(MouseEvent e) {}
    
    public void mouseDragged(MouseEvent e) {
      pressedX = e.getX();
      pressedY = e.getY();
      
      SwingDriver.this.repaint();
    }
    
    public void mousePressed(MouseEvent e) {
      if (isSpike(e.getX(), e.getY())) {
        int location = getSpike(e.getX(), e.getY());
        
        if (getCurrentBoard().getPieces(backgammon.getCurrentPlayer(), location) > 0) {
          pressed = true;
          pressedLocation = location;
          pressedX = e.getX();
          pressedY = e.getY();          
        }
      } else if (isBar(e.getX(), e.getY(), backgammon.getCurrentPlayer())) {
        int location = BAR_BLACK_LOCATION;
        
        if (backgammon.getCurrentPlayer() == Board.WHITE)
          location = BAR_WHITE_LOCATION;
        
        if (((location == BAR_BLACK_LOCATION) && (getCurrentBoard().getBar(Board.BLACK) > 0)) || 
            ((location == BAR_WHITE_LOCATION) && (getCurrentBoard().getBar(Board.WHITE) > 0))) {
          pressed = true;
          pressedLocation = location;
          pressedX = e.getX();
          pressedY = e.getY();          
        }
      }  
              
            
      SwingDriver.this.repaint();
    }
    
    public void mouseReleased(MouseEvent e) { 
      if (isSpike(e.getX(), e.getY())) {
        int location = getSpike(e.getX(), e.getY());
        
        // handle a drag event
        if (location != pressedLocation) {
          pressedX = e.getX();
          pressedY = e.getY();
          
          if (pressedLocation >= 0) {
            try {
              handleMovement(new NormalMovement(backgammon.getCurrentPlayer(), pressedLocation, location));
              status.message = "";
            } catch (IllegalMoveException g) {
              status.message = g.getMessage();
            }
          } else {
            try {
              handleMovement(new BarMovement(backgammon.getCurrentPlayer(), location));
              status.message = "";
            } catch (IllegalMoveException g) {
              status.message = g.getMessage();
            }
          }
        // handle a click event
        } else {
          try {
            handleMovement(new BearOffMovement(backgammon.getCurrentPlayer(), location));
            status.message = "";
          } catch (IllegalMoveException h) {
            try {
              int amount = (backgammon.getDice().getDie1() > backgammon.getDice().getDie2() ? 
                            backgammon.getDice().getDie1() :
                            backgammon.getDice().getDie2());
              handleMovement(new NormalMovement(backgammon.getCurrentPlayer(),
                                                location,
                                                location + amount*Board.getDirection(backgammon.getCurrentPlayer())));
              status.message = "";
            } catch (IllegalMoveException f) {
              try {
                int amount = (backgammon.getDice().getDie1() < backgammon.getDice().getDie2() ? 
                              backgammon.getDice().getDie1() :
                              backgammon.getDice().getDie2());
                handleMovement(new NormalMovement(backgammon.getCurrentPlayer(),
                                                  location,
                                                  location + amount*Board.getDirection(backgammon.getCurrentPlayer())));
                status.message = "";
              } catch (IllegalMoveException g) {
                status.message = g.getMessage();
              }
            }
          }
        }
      } else if (isDice(e.getX(), e.getY())) {
        if ((move != null) && (! move.movePossible())) {
          player.moveDone();
          status.message = "";
        } else {
          status.message = "You must use all of your dice!";
        }
      } else if (isBar(e.getX(), e.getY(), backgammon.getCurrentPlayer())) {
        int start = Board.getBase(Board.getOtherPlayer(backgammon.getCurrentPlayer()));
        int direction = Board.getDirection(backgammon.getCurrentPlayer());
        
        try {
          int amount = (backgammon.getDice().getDie1() > backgammon.getDice().getDie2() ? 
                        backgammon.getDice().getDie1() :
                        backgammon.getDice().getDie2());
          handleMovement(new BarMovement(backgammon.getCurrentPlayer(), start + amount * direction));
          status.message = "";
        } catch (IllegalMoveException f) {
          try {
            int amount = (backgammon.getDice().getDie1() < backgammon.getDice().getDie2() ? 
                          backgammon.getDice().getDie1() :
                          backgammon.getDice().getDie2());
            handleMovement(new BarMovement(backgammon.getCurrentPlayer(), start + amount * direction));
            status.message = "";
          } catch (IllegalMoveException g) {
            status.message = g.getMessage();
          }
        }
      }
      
      SwingDriver.this.repaint();
      pressed = false;      
    }
  } 


  protected void handleMovement(Movement movement) {
    if (move != null) 
      move.addMovement(movement);
  }
  
  public class StatusPanel extends JPanel {
    // variables defining the layout of the swing UI
    public static final int HEIGHT = 20;
    public static final int PIPS_OFFSET = 400;
    public static final int MOVE_OFFSET = 270;
    
    public String message;
    
    public StatusPanel() {
      message = "";
    }
    
    public Dimension getPreferredSize() {
      return new Dimension(BoardPanel.WIDTH, HEIGHT);
    }
    
    public void paint(Graphics g) {
      g.setColor(new Color(255, 30, 0));
      g.setFont(new Font("Helvetica", Font.BOLD, 10));
      g.drawString(message, 5, 13); 
      
      g.setColor(new Color(0, 0, 0));
      g.drawString((move == null ? "" : "" + move), MOVE_OFFSET, 13);
      g.drawString("Pips: " + getCurrentBoard().getBlackPips() + " Black " +
                   getCurrentBoard().getWhitePips() + " White ", PIPS_OFFSET, 13); 
    }
    
  }
  
  // This method returns a buffered image with the contents of an image
  public static BufferedImage toBufferedImage(Image image) {
    if (image instanceof BufferedImage) {
      return (BufferedImage)image;
    } 
    
    // This code ensures that all the pixels in the image are loaded
    image = new ImageIcon(image).getImage();
    
    // Determine if the image has transparent pixels; for this method's
    // implementation, see e661 Determining If an Image Has Transparent Pixels
    boolean hasAlpha = hasAlpha(image);
    
    // Create a buffered image with a format that's compatible with the screen
    BufferedImage bimage = null;
    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
    try {
      // Determine the type of transparency of the new buffered image
      int transparency = Transparency.OPAQUE;
      if (hasAlpha) {
        transparency = Transparency.BITMASK;
      }
      
      // Create the buffered image
      GraphicsDevice gs = ge.getDefaultScreenDevice();
      GraphicsConfiguration gc = gs.getDefaultConfiguration();
      bimage = gc.createCompatibleImage(
                                        image.getWidth(null), image.getHeight(null), transparency);
    } catch (HeadlessException e) {
      // The system does not have a screen
    }
    
    if (bimage == null) {
      // Create a buffered image using the default color model
      int type = BufferedImage.TYPE_INT_RGB;
      if (hasAlpha) {
        type = BufferedImage.TYPE_INT_ARGB;
      }
      bimage = new BufferedImage(image.getWidth(null), image.getHeight(null), type);
    }
    
    // Copy image to buffered image
    Graphics g = bimage.createGraphics();
    
    // Paint the image onto the buffered image
    g.drawImage(image, 0, 0, null);
    g.dispose();
    
    return bimage;
  }
  
  // This method returns true if the specified image has transparent pixels
  public static boolean hasAlpha(Image image) {
    // If buffered image, the color model is readily available
    if (image instanceof BufferedImage) {
      BufferedImage bimage = (BufferedImage)image;
      return bimage.getColorModel().hasAlpha();
    }
    
    // Use a pixel grabber to retrieve the image's color model;
    // grabbing a single pixel is usually sufficient
    PixelGrabber pg = new PixelGrabber(image, 0, 0, 1, 1, false);
    try {
      pg.grabPixels();
    } catch (InterruptedException e) {
    }
    
    // Get the image's color model
    ColorModel cm = pg.getColorModel();
    return cm.hasAlpha();
  }
  
  protected class SwingHumanPlayer implements Player {
    
    Object lock = new Object();
        
    public Move move(Backgammon backgammon) {
      SwingDriver.this.repaint();
      move = new Move(backgammon.getDice(), backgammon.getCurrentBoard(), backgammon.getCurrentPlayer());
      
      synchronized (lock) {
        try {
          lock.wait();
        } catch (InterruptedException cannotHappen) {
          throw new NewGameException();
        }
      }
      
      
      Move result = move;
      move = null;
      return result;
    }
    
    public void moveDone() {
      synchronized (lock) {
        lock.notifyAll();
      }
    }
    
    public void won(Backgammon game) {
      JOptionPane.showMessageDialog(SwingDriver.this, "Congratulations!  You won!");
    }
    public void lost(Backgammon game) {
      JOptionPane.showMessageDialog(SwingDriver.this, "Too bad - you lost!");
    }
  }
  
  protected class SwingRandomPlayer extends RandomPlayer {
    
    public int DELAY = 100;
    
    Random random = new Random();
    
    public Move move(Backgammon backgammon) {
      SwingDriver.this.repaint();
      
      try {
        Thread.sleep(DELAY);
      } catch (InterruptedException cannotHappen) {
        throw new NewGameException();
      } 
    
      return super.move(backgammon);
    }
  }
}