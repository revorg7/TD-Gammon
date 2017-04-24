package move;

import board.*;

public class BearOffMovement extends Movement {
  
  // the starting location of this movement
  protected int start;
  
  /**
   * Constructor, taking the player, the start location
   *
   * @param player The player making the movement
   * @param start The start location
   * @param end The end location
   */
  public BearOffMovement(int player, int start) {
    super(player);
    
    if (! Board.onBoard(start))
      throw new IllegalMoveException("Start is not on the board!");
    
    this.start = start;
  }
  
  /**
   * Returns the starting location movement
   *
   * @return The starting location for this movement
   */
  public int getStart() {
    return start;
  }
  
  /**
   * Return whether or not a move is possible using 
   * the given dice roll by the given player in the given board.
   * Checks to see if a bar, bear off, or normal movement 
   * are possible.
   *
   * @param int die The dice roll
   * @param player The player
   * @param board The board
   * @return Wether or not a move is possible
   */
  public static boolean movePossible(int die, int player, Board board) {
    int direction = board.getDirection(player);
    
    for (int i=board.getBase(player)-direction; board.inHomeQuadrant(i, player); i-=direction) {
      BearOffMovement movement = new BearOffMovement(player, i);
      
      if (movement.canUse(board, die))
        try {
          movement.apply(board.getScratch());
          return true;
        } catch (IllegalMoveException e) {}
    }
    
    return false;
  }
  
  /**
   * Returns whether or not this movement can use the given
   * dice roll to perfrom it's movement
   *
   * @param die The dice roll
   * @return Wether or not that roll can make this move
   */
  public boolean canUse(Board board, int die) {
    int direction = board.getDirection(player);
    
    for (int i=start-direction; board.inHomeQuadrant(i, player); i-=direction)
      if (board.getPieces(player, i) > 0)
        return (die == Math.abs(start - Board.getBase(player)));
        
    return (die >= Math.abs(start - Board.getBase(player)));
  }
  
  /**
   * Validates this movement given the provided
   * board situation.
   *
   * @param board The current setup
   */
  public void apply(Board board) throws IllegalMoveException {
    if (board.getPieces(player, start) == 0)
      throw new IllegalMoveException("You have no pieces at that location.");
    
    if (board.getBar(player) > 0)
      throw new IllegalMoveException("You must move your pieces from the bar first.");
            
    // make sure we're able to bear pieces off
    for (int i=0; i<board.NUM_SPIKES; i++)
      if ((board.getPieces(player, i) > 0) && (! board.inHomeQuadrant(i, player)))
        throw new IllegalMoveException("You must move the pieces from " + (i+1) + " to your home quadrant before bearing off.");
    
    // now do the move
    board.removeFromLocation(player, start);
    board.moveOff(player);
  }
  
  /**
   * Returns whether not this movement equals the object
   *
   * @return Whether or not they are equal
   */
  public boolean equals(Object o) {
    if (! (o instanceof BearOffMovement))
      return false;
    
    return ((BearOffMovement) o).start == start;
  }
  
  /**
   * Returns a String representation of this move
   *
   * @return a String
   */
  public String toString() {
    return (start+1) + "/off";
  }
}

