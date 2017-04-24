package move;

import board.*;

public class NormalMovement extends Movement {
  
  // the starting location of this movement
  protected int start;
  
  // the end location of this movement
  protected int end;
  
  /**
   * Constructor, taking the player, the start and
   * the end location
   *
   * @param player The player making the movement
   * @param start The start location
   * @param end The end location
   */
  public NormalMovement(int player, int start, int end) {
    super(player);
    
    if ((! Board.onBoard(start)) || (! Board.onBoard(end)))
      throw new IllegalMoveException("Start or end is not on the board!");
    
    this.start = start;
    this.end = end;
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
   * Returns the end location movement
   *
   * @return The end location for this movement
   */
  public int getEnd() {
    return end;
  }
  
  /**
   * Returns whether or not this movement can use the given
   * dice roll to perfrom it's movement
   *
   * @param die The dice roll
   * @return Wether or not that roll can make this move
   */
  public boolean canUse(Board board, int die) {
    return (die == Math.abs(start - end));
  }
  
  /**
   * Validates this movement given the provided
   * board situation.
   *
   * @param board The current setup
   */
  public void apply(Board board) throws IllegalMoveException {
    if (start == end) 
      throw new IllegalMoveException("You must move your piece at least one space.");
    
    if (Math.abs(start - end) > Dice.MAX_VALUE)
      throw new IllegalMoveException("You cannot move a pice move than " + Dice.MAX_VALUE + " spots.");
    
    if (board.getBar(player) > 0)
      throw new IllegalMoveException("You must move your pieces from the bar first.");
    
    if (board.getPieces(player, start) == 0)
      throw new IllegalMoveException("You have no pieces at the start location.");
    
    if (((start > end) && (board.getDirection(player) > 0)) || 
        ((start < end) && (board.getDirection(player) < 0)))
      throw new IllegalMoveException("You cannot move backwards.");
    
    if (board.getPieces(board.getOtherPlayer(player), end) > 1)
      throw new IllegalMoveException("The other player occupies that location.");
    
    // now perform the move, by first checking to see if we're going to bump someone
    if (board.getPieces(board.getOtherPlayer(player), end) == 1) {
      board.removeFromLocation(board.getOtherPlayer(player), end);
      board.moveToBar(board.getOtherPlayer(player));
    }

    // now move the piece
    board.removeFromLocation(player, start);
    board.moveToLocation(player, end);
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
    int start = board.getBase(board.getOtherPlayer(player))+direction; 

    for (int i=start; board.onBoard(i+(die*direction)); i+=direction) 
      try {
        new NormalMovement(player, i, i+(die*direction)).apply(board.getScratch());
        return true;
      } catch (IllegalMoveException e) {}
    
    return false;
  }
  
  /**
   * Returns whether not this movement equals the object
   *
   * @return Whether or not they are equal
   */
  public boolean equals(Object o) {
    if (! (o instanceof NormalMovement))
      return false;
    
    return ((((NormalMovement) o).start == start) && (((NormalMovement) o).end == end));
  }
  
  /**
   * Returns a String representation of this move
   *
   * @return a String
   */
  public String toString() {
    return (start+1) + "/" + (end+1);
  }
        
}
  
  