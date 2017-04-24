package player;

import java.util.*;

import game.Backgammon;
import move.Move;

/** 
 * Class which implements a random player.  This player
 * will get the list of all available moves and return
 * a random one.
 */
public class RandomPlayer implements Player {
  
  // the random move selector
  protected Random random = new Random();
  
  /**
   * Requests that the player make a move using the given
   * backgammon setup.  The player should return a valid
   * and complete move.  The list of valid moves can be
   * found by calling backgammon.getAllMoves().
   *
   * @param backgammon The current backgammon situation
   */
  public Move move(Backgammon backgammon) {
    Move[] moves = backgammon.getMoves();
    
    return moves[random.nextInt(moves.length)];
  }
  
  public void won(Backgammon game) {}
  
  public void lost(Backgammon game) {}
  
}