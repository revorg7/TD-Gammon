package move;

import java.util.*;
import game.*;
import move.*;
import board.*;
import player.*;

public class MovementFactory {
  
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
    return (BarMovement.movePossible(die, player, board) ||
            BearOffMovement.movePossible(die, player, board) ||
            NormalMovement.movePossible(die, player, board));
  }
  
  /**
   * Returns all possible distinct moves for the given player
   * given the starting board and the given dice roll.
   *
   * @param player The player who is active
   * @param dice The dice roll
   * @param board The current board
   * @return All of the possible moves
   */
  public static Move[] getAllMoves(int player, Dice dice, Board board) {
    LinkedHashSet result = new LinkedHashSet();
    result.addAll(generateMoves(new Move(dice, board, player)));
    
    return (Move[]) result.toArray(new Move[0]);
  }
  
  protected static Collection generateMoves(Move base) {
    Collection result = new LinkedHashSet();
    
    // if this is a full move, or no more moves are possible,
    // return just this move
    if (base.isFull() || (! base.movePossible())) {
      result.add(base);
      return result;
    }

    // get the current board
    Board board = base.getCurrentBoard();
    int player = base.player;
    
    // otherwise, first try to do any off-bar moves
    if (board.getBar(base.player) > 0) {
      for (int i=0; i<base.used.length; i++) 
        if (base.used[i] != base.USED) { 
          try {
            Move bar = new Move(base);
            int destination = board.getBase(board.getOtherPlayer(player)) + (bar.used[i]*board.getDirection(player));
            bar.addMovement(new BarMovement(player, destination));
            result.addAll(generateMoves(bar));
          } catch (IllegalMoveException e) {}
        }
    } else {
      // now try for any normal moves
      for (int i=board.getBase(board.getOtherPlayer(player))+board.getDirection(player); board.onBoard(i); i+=board.getDirection(player)) {
        if (board.getPieces(player, i) > 0) {
          if (base.used.length == 2) {
            for (int j=0; j<base.used.length; j++) 
              if (base.used[j] != base.USED) { 
                try {
                  Move normal = new Move(base);
                  int destination = i + board.getDirection(player) * base.used[j];
                  normal.addMovement(new NormalMovement(player, i, destination));
                  result.addAll(generateMoves(normal));
                } catch (IllegalMoveException e) {}
              }
          } else {
            try {
              Move normal = new Move(base);
              int destination = i + board.getDirection(player) * base.dice.getDie1();
              normal.addMovement(new NormalMovement(player, i, destination));
              result.addAll(generateMoves(normal));
            } catch (IllegalMoveException e) {}
          }
        }
      }
        
      // and lastly try any bear-off moves
      for (int i=board.getBase(player)-board.getDirection(player); board.inHomeQuadrant(i, player); i-=board.getDirection(player)) 
        if (board.getPieces(player, i) > 0) {
          try {
            Move bearoff = new Move(base);
            bearoff.addMovement(new BearOffMovement(player, i));
            result.addAll(generateMoves(bearoff));
          } catch (IllegalMoveException e) {}
        }
    }
        
    return result;
  }
  
}