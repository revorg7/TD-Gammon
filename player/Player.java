package player;

import game.Backgammon;
import move.Move;

/** 
 * Class which represents a backgammon player.  The Backgammon
 * object will repeatedly call move() on this player, asking
 * the player to pick a move.  Once the game is over, the
 * backgammon object will either call won() or lost() with the 
 * result of the game.
 */
public interface Player {
  
  /**
   * Requests that the player make a move using the given
   * backgammon setup.  The player should return a valid
   * and complete move.  The list of valid moves can be
   * found by calling backgammon.getAllMoves().
   *
   * @param backgammon The current backgammon situation
   */
  public Move move(Backgammon backgammon);
  
  /**
   * Upcall to indicate that this player has won the current game
   */
  public void won(Backgammon game);
  
  /**
    * Upcall to indicate that this player has lost the current game
   */
  public void lost(Backgammon game);
  
}