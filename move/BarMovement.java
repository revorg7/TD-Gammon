package move;

import board.*;

public class BarMovement extends Movement {
  
  // the end location of this movement
  protected int end;
  
  /**
   * Constructor, taking the player and
   * the end location
   *
   * @param player The player making the movement
   * @param start The start location
   * @param end The end location
   */
  public BarMovement(int player, int end) {
    super(player);
    
    if (! Board.onBoard(end))
      throw new IllegalMoveException("End is not on the board!");
    
    this.end = end;
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
    return (die == Math.abs(Board.getBase(Board.getOtherPlayer(player)) - end));
  }
  
  /**
   * Validates this movement given the provided
   * board situation.
   *
   * @param board The current setup
   */
  public void apply(Board board) throws IllegalMoveException {
    if (board.getBar(player) == 0)
      throw new IllegalMoveException("You have no pieces on the bar.");

    if (! board.inHomeQuadrant(end, board.getOtherPlayer(player)))
      throw new IllegalMoveException("You cannot move from the bar to that location.");
    
    if (board.getPieces(board.getOtherPlayer(player), end) > 1)
      throw new IllegalMoveException("The other player occupies that location.");
    
    // now do the movement - first bump any pieces there
    if (board.getPieces(board.getOtherPlayer(player), end) == 1) {
      board.removeFromLocation(board.getOtherPlayer(player), end);
      board.moveToBar(board.getOtherPlayer(player));
    }
    
    // then move a piece from the bar to the location
    board.removeFromBar(player);
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
    try {
      int destination = board.getBase(board.getOtherPlayer(player)) + (board.getDirection(player) * die);
      new BarMovement(player, destination).apply(board.getScratch());
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
    if (! (o instanceof BarMovement))
      return false;
    
    return ((BarMovement) o).end == end;
  }
  
  /**
   * Returns a String representation of this move
   *
   * @return a String
   */
  public String toString() {
    return "bar/" + (end+1);
  }
}

