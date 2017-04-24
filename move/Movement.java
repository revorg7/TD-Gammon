package move;

import board.Board;

public abstract class Movement {
  
  // the player making the movement
  protected int player;
  
  /**
   * Constructor, taking the player
   *
   * @param player The player
   */
  public Movement(int player) {
    this.player = player;
  }
  
  /**
   * Returns the player making this movement
   *
   * @return The die used for this movement
   */
  public int getPlayer() {
    return player;
  }
  
  /**
   * Validates this movement given the provided
   * board situation.
   *
   * @param board The current setup
   */
  public abstract void apply(Board board) throws IllegalMoveException;
  
  /**
   * Returns whether or not this movement can use the given
   * dice roll to perfrom it's movement
   *
   * @param die The dice roll
   * @return Wether or not that roll can make this move
   */
  public abstract boolean canUse(Board board, int die);
}