package board;

import java.io.*;

import move.IllegalMoveException;

public class Board {
  
  // the board size, or the number of spikes in the board
  public static final int NUM_SPIKES = 24;
  public static final int WHITE = 0;
  public static final int BLACK = 1;
  public static final int NEITHER = -1;
  
  // the initial configuration
  public static final int[] INITIAL_LOCATIONS = new int[] {0, 5, 7, 11, 12, 16, 18, 23};
  public static final int[] INITIAL_NUMBERS = new int[] {2, 5, 3, 5, 5, 3, 5, 2};
  public static final int[] INITIAL_COLORS = new int[] {WHITE, BLACK, BLACK, WHITE, BLACK, WHITE, WHITE, BLACK};
  
  // the internal array representing the number of pieces at each spike
  private int[] board;
  
  // the array representing the color of the pieces on each spike
  private int[] colors;
  
  // the number of white/black pieces on the bar (bar[WHITE] white, bar[BLACK] black)
  private int[] bar;
  
  // the number of white/black pieces off of the board
  private int[] off;
  
  /**
   * Public equals
   *
   * @param other To compare to
   * @return equality
   */
  public boolean equals(Object o) {
    Board board = (Board) o;
    
    return (isEqual(this.board, board.board) && isEqual(colors, board.colors) && 
            isEqual(bar, board.bar) && isEqual(off, board.off));
  }
  
  /**
   * Utility function to detect equality
   *
   * @param a The frist array
   * @param b The second array
   * @return Wtheher they are equal
   */
  public static boolean isEqual(int[] a, int[] b) {
    for (int i=0; i<a.length; i++)
      if (a[i] != b[i])
        return false;
    
    return true;
  }
  
  /**
   * Returns the hashCode of this move
   *
   * @return the hasCode of the final board
   */
  public int hashCode() {
    int base = 19283;
    
    base = hash(base, off);
    base = hash(base, bar);
    base = hash(base, board);
    base = hash(base, colors);
    
    return base;
  }
  
  protected int hash(int base, int[] array) {
    for (int i=0; i<array.length; i++) {
      if (array[i] == 0)
        base = (base * 273891);
      else
        base = (base + array[i]) ^ (array[i] + 55);
    }
    
    return base;
  }
  
  /**
   * Constructor, build an newly initialzed board
   */
  public Board() {
    // build the board
    this.board = new int[NUM_SPIKES];
    this.colors = new int[NUM_SPIKES];
    
    // build the other locations
    this.bar = new int[2];
    this.off = new int[2];
    
    // place the initial pieces
    reset();
  }
  
  /**
   * Constructor, builds a copy of the given board
   */
  protected Board(Board other) {
    // initialze this board
    this();
    
    // place the pieces
    System.arraycopy(other.board, 0, this.board, 0, this.board.length);
    System.arraycopy(other.colors, 0, this.colors, 0, this.colors.length);
    System.arraycopy(other.bar, 0, this.bar, 0, this.bar.length);
    System.arraycopy(other.off, 0, this.off, 0, this.off.length);
  }
  
  /**
   * Returns a scratch copy of the board, which is a clone and
   * can be changes without any problems.
   *
   * @return a scratch copy of this board
   */
  public Board getScratch() {
    return new Board(this);
  }
  
  /**
   * If the game is over, this returns the winner
   *
   * @return The winner of the game
   */
  public int getWinner() {
    boolean black = (bar[BLACK] > 0);
    boolean white = (bar[WHITE] > 0);
    
    for (int i=0; i<NUM_SPIKES; i++) {
      black = black | (colors[i] == BLACK);
      white = white | (colors[i] == WHITE);
    }
    
    if (! black)
      return BLACK;
    else if (! white)
      return WHITE;
    else 
      return NEITHER;
  }
  
  /**
   * Returns whether or not the game is over
   *
   * @return Whether or not the game is over
   */
  public boolean isGameOver() {
    boolean black = (bar[BLACK] > 0);
    boolean white = (bar[WHITE] > 0);
    
    for (int i=0; i<NUM_SPIKES; i++) {
      black = black | (colors[i] == BLACK);
      white = white | (colors[i] == WHITE);
    }
    
    return (! (black && white));
  }
  
  /**
   * Resets the pieces on the board to their initial configurations
   */
  public void reset() {
    for (int i=0; i<NUM_SPIKES; i++) {
      board[i] = 0;
      colors[i] = NEITHER;
    }
  
    for (int i=0; i<bar.length; i++)
      bar[i] = 0;
    
    for (int i=0; i<off.length; i++)
      off[i] = 0;
    
    for (int i=0; i<INITIAL_LOCATIONS.length; i++) {
      board[INITIAL_LOCATIONS[i]] = INITIAL_NUMBERS[i];
      colors[INITIAL_LOCATIONS[i]] = INITIAL_COLORS[i];
    }
  }
  
  /**
   * Returns the color of the pieces at the given location
   *
   * @param location The location
   * @return The color of the pieces at the given location
   *         (WHITE, BLACK, or NEITHER)
   */
  public int getColor(int location) {
    return colors[location];
  }
  
  /**
   * Returns the number of pieces at the given location
   *
   * @param location The location
   * @return the number of pieces at the location
   */
  public int getPieces(int location) {
    return board[location];
  }
  
  /**
   * Returns the number of black pieces at the given location.
   * If there are white pieces at the location, returns 0.
   *
   * @param location The location
   * @return the number of black pieces at the location
   */
  public int getBlackPieces(int location) {
    return getPieces(BLACK, location);
  }
  
  /**
   * Returns the number of black pieces at the given location.
   * If there are white pieces at the location, returns 0.
   *
   * @param location The location
   * @return the number of black pieces at the location
   */
  public int getWhitePieces(int location) {
    return getPieces(WHITE, location);
  }
  
  /**
   * Returns the number of pieces at the given location an
   * of the given color.
   * If there are other color pieces at the location, returns 0.
   *
   * @param color The color
   * @param location The location
   * @return the number of black pieces at the location
   */
  public int getPieces(int color, int location) {
    if (colors[location] == color)
      return board[location];
    
    return 0;
  }
  
  /**
   * Returns the number of black pieces on the bar.
   *
   * @return the number of black pieces on the bar
   */
  public int getBlackBar() {
    return getBar(BLACK);
  }  
  
  /**
   * Returns the number of white pieces on the bar.
   *
   * @return the number of white pieces on the bar
   */
  public int getWhiteBar() {
    return getBar(WHITE);
  }  
  
  /**
   * Returns the number of pieces on the bar of the
   * given color.
   *
   * @param color The color
   * @return the number of pieces on the bar
   */
  public int getBar(int color) {
    return bar[color];
  }  
  
  /**
   * Returns the number of black pieces beared off
   *
   * @return the number of black pieces beared off
   */
  public int getBlackOff() {
    return getOff(BLACK);
  }  
  
  /**
   * Returns the number of white pieces beared off
   *
   * @return the number of white pieces on the bar
   */
  public int getWhiteOff() {
    return getOff(WHITE);
  }  
  
  /**
   * Returns the number of pieces beared off of the
   * given color.
   *
   * @param color The color
   * @return the number of pieces beared off
   */
  public int getOff(int color) {
    return off[color];
  }  
  
  /**
   * Returns the number of black pips left
   *
   * @return the number of black pips left
   */
  public int getBlackPips() {
    return getPips(BLACK);
  }  
  
  /**
   * Returns the number of white pips left
   *
   * @return the number of white pips left
   */
  public int getWhitePips() {
    return getPips(WHITE);
  }  
  
  /**
   * Returns the number of pips for the
   * given color.
   *
   * @param color The color
   * @return the number of pieces beared off
   */
  public int getPips(int color) {
    int base = getBase(color);
    int result = 0;
    
    for (int i=0; i<NUM_SPIKES; i++)
      if (colors[i] == color)
        result += getPieces(i) * Math.abs(base - i);
    
    result += NUM_SPIKES * getBar(color);
    
    return result;
  }  
  
  /**
   * Returns the home location of the given color
   * (the location where the color wants to move it's
   * pieces).
   *
   * @param color The color
   * @return The base, either 0 or 25
   */
  public static int getBase(int color) {
    return (color == WHITE ? 24 : -1); 
  } 
  
  /**
   * Returns the direction of the given color
   * (the direction where the color wants to move it's
   * pieces).
   *
   * @param color The color
   * @return The direction, either 1 or -1
   */
  public static int getDirection(int color) {
    return (color == WHITE ? 1 : -1); 
  }
  
  /**
   * Returns the other player
   *
   * @param color The color
   * @return The other color
   */
  public static int getOtherPlayer(int color) {
    return (color == WHITE ? BLACK : WHITE); 
  }
  
  /**
   * Internal method which returns whether or not the given
   * location is on the board
   *
   * @param location The location to check for
   * @return Whether or not the locaiton is on the board
   */
  public static boolean onBoard(int location) {
    return ((location >= 0) && (location < NUM_SPIKES));
  }
  
  /**
   * Moves a piece to the given location of the given color
   *
   * @param color The color to move
   * @param location The location to move
   */
  public void moveToLocation(int color, int location) throws IllegalMoveException {
    if (colors[location] == getOtherPlayer(color))
      throw new IllegalMoveException("Unexpected error - other color pieces at location " + location + " of color " + color);
    
    board[location]++;
    
    if (board[location] == 1)
      colors[location] = color;
  }
  
  /**
   * Removes a piece from the given location of the given color
   *
   * @param color The color to remove
   * @param location The location to remove
   */
  public void removeFromLocation(int color, int location) throws IllegalMoveException {
    if (colors[location] != color)
      throw new IllegalMoveException("Unexpected error - no pieces at location " + location + " of color " + color);
    
    board[location]--;
    
    if (board[location] == 0)
      colors[location] = NEITHER;
  }
  
  /**
   * Moves a piece to the bar of the given color
   *
   * @param color The color to move
   */
  public void moveToBar(int color) {
    bar[color]++;
  }
  
  /**
   * Removes a piece from the bar of the given color
   *
   * @param color The color to remove
   */
  public void removeFromBar(int color) throws IllegalMoveException {
    if (bar[color] == 0)
      throw new IllegalMoveException("Unexpected error - no pieces on bar of color " + color);
    
    bar[color]--;
  }
  
  /**
   * Moves a piece off of the given color
   *
   * @param color The color to move
   */
  public void moveOff(int color) {
    off[color]++;
  }
  
  /**
   * Method which returns whether or not the given spike is in the
   * given player's home square.
   *
   * @param location Th elocation
   * @param color The player
   * @return Whether or not th elocation is in that player's home sequare
   */
  public static boolean inHomeQuadrant(int location, int color) {
    return isBetween(location, getBase(color), getBase(color) + (getDirection(getOtherPlayer(color)) * 6));
  }
  
  /**
   * Method which checks to see if the location is between the other
   * two locations.
   *
   * @param location The location to check
   * @param a The first location
   * @param b The second location
   */
  protected static boolean isBetween(int location, int a, int b) {
    return ((location >= a && location <= b) || (location <= a && location >= b));
  }
  
  /**
   * Prints out a gnubg-style text board.
   *
   * @param out The output stream to write the board to
   */
  public void print(PrintStream out) {
    out.println("+13-14-15-16-17-18------19-20-21-22-23-24-+");
    for (int i=1; i<6; i++) {
      out.print("|");
      
      for (int j=12; j<18; j++) {
        out.print(" ");
        print(out, j, i);
        out.print(" ");
      }
      
      out.print("| ");
      
      if ((i == 1) && (bar[BLACK] > 6))
        out.print(bar[BLACK] + "");
      else if (bar[BLACK] >= (6-i))
        out.print("X");
      else
        out.print(" ");
      
      out.print(" |");
      
      for (int j=18; j<24; j++) {
        out.print(" ");
        print(out, j, i);
        out.print(" ");
      }
      
      out.print("| ");
      
      if (off[WHITE] >= i)
        out.print("O");
      if (off[WHITE] >= 5+i)
        out.print("O");
      if (off[WHITE] >= 10+i)
        out.print("O");
      
      out.println();
    }
     
    out.println("|                  |BAR|                  |");
    
    for (int i=5; i>0; i--) {
      out.print("|");
      
      for (int j=11; j>=6; j--) {
        out.print(" ");
        print(out, j, i);
        out.print(" ");
      }
      
      out.print("| ");
      
      if ((i == 1) && (bar[WHITE] > 6))
        out.print(bar[WHITE] + "");
      else if (bar[WHITE] >= (6-i))
        out.print("O");
      else
        out.print(" ");
      
      out.print(" |");
      
      for (int j=5; j>=0; j--) {
        out.print(" ");
        print(out, j, i);
        out.print(" ");
      }
      
      out.print("| ");
      
      if (off[BLACK] >= i)
        out.print("X");
      if (off[BLACK] >= 5+i)
        out.print("X");
      if (off[BLACK] >= 10+i)
        out.print("X");
      
      out.println();
    }
    
    out.println("+12-11-10--9--8--7-------6--5--4--3--2--1-+");
  }
  
  /**
   * Internal method to print a piece and location
   *
   * @param out The stream
   * @param location The location
   * @param count The count number
   */
  protected void print(PrintStream out, int location, int count) {
    String key = (colors[location] == WHITE ? "O" : "X");
    
    if ((count >= 5) && (board[location] > count))
      out.print("" + board[location]);
    else
      if (board[location] >= count)
        out.print(key);
      else
        out.print(" ");
  }
}