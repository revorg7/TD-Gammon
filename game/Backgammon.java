package game;

import java.io.*;

import board.*;
import player.*;
import move.*;
import net.*;

/** 
 * This class wraps all of the backgammon functionality.  Basically, the use model
 * for this class is build a new Backgammon with two players, execute backgammon.run(),
 * which runs the game, and the call backgammon.reset(), backgammon.run() if you
 * want to play again.
 */
public class Backgammon {
  
  // the internal board, which is the state before the current move
  protected Board board;
  
  // the dice 
  protected Dice dice;
  
  // the color of the current player
  protected int player;
  
  // the list of players
  protected Player[] players;
  
  /**
   * Builds a new backgammon instance, given the two players
   * to play the game between.
   *
   * @param player1 The first player (BLACK)
   * @param player2 The second player (WHITE)
   */
  public Backgammon(Player player1, Player player2) {
    this.players = new Player[2];
    this.players[0] = player1;
    this.players[1] = player2;
    
    reset();
  }
  
  /**
   * Runs a game of backgammon, and does not return until the game
   * is over.  Returns the player who won the game.
   *
   * @return The winning player
   */
  public Player run() {
    while (! board.isGameOver()) {
      getMove(players[0]);
      if (board.isGameOver()) break;
      
      getMove(players[1]);
      if (board.isGameOver()) break;
    }
    
    if (board.getOff(board.BLACK) == 15) {
      players[0].won(this);
      players[1].lost(this);
      
      return players[0];
    } else {
      players[1].won(this);
      players[0].lost(this);
      
      return players[1];
    }
  }
  
  /**
   * Gets a player's move 
   *
   * @param player The player
   */
  private void getMove(Player player) {
    Move move = player.move(Backgammon.this);
    
    if (! move.getOriginalBoard().equals(board)) {
      System.out.println("ERROR: Attempt to perform an invalid move " + move + "!");
      getMove(player);
    } else {
      doMove(move);
    }
  }
  
  /**
   * Resets this backgammon instance to the initial state, with
   * a new board and the black player's move
   */
  public void reset() {
    this.board = new Board();
    this.dice = Dice.roll();
    this.player = Board.BLACK;
  }
  
  /**
   * Returns the current player, either Board.BLACK or Board.WHITE
   *
   * @return The current player
   */
  public int getCurrentPlayer() {
    return player;
  }
  
  public int getOtherPlayer(){
	  return board.getOtherPlayer(player);
  }
  
  /**
   * Returns a list of all of the possible moves which the player
   * can currently make
   *
   * @return The current available moves
   */
  public Move[] getMoves() {
    return MovementFactory.getAllMoves(player, dice, board);
  }
  
  /**
   * Returns the current dice
   *
   * @return The current dice
   */
  public Dice getDice() {
    return dice;
  }
  
  /**
   * Returns the current board.  Note that this is the actual
   * board, so *NO CHANGES SHOULD BE MADE TO THIS OBJECT*.
   *
   * @return The current board
   */
  public Board getCurrentBoard() {
    return board;
  }
  
  /**
   * Returns whether or not the game is over
   *
   * @return Whether or not the game is over
   */
  public boolean isGameOver() {
    return board.isGameOver();
  }
  
  /**
   * Performs the provided move
   *
   * @param movement The movement to do
   * @return Whether or not more moves are required
   */
  private void doMove(Move move) throws IllegalMoveException {
    board = move.getCurrentBoard();
    dice = Dice.roll();
    player = board.getOtherPlayer(player);
  }   
  
  /**
   * Prints out a gnubg-style text board.
   *
   * @param out The output stream to write the board to
   */
  public void print(PrintStream out) {
    print(out, board);
  }
    
  /**
   * Prints out a gnubg-style text board.
   *
   * @param out The output stream to write the board to
   * @param board The board 
   */
  public void print(PrintStream out, Board board) {
    out.println("Player: " + (player == Board.WHITE ? "O" : "X") + "\t\t\t\t\tPips X " + board.getBlackPips() + " / O " + board.getWhitePips());
    dice.print(out);
    out.println();
    board.print(out);
  }
}