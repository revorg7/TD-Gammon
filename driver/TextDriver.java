package driver;

import java.io.*;
import board.*;
import game.*;
import player.*;
import move.*;

public class TextDriver {
  
  // the Backgammon used to run this driver
  protected Backgammon backgammon;
  
  // the stream tokenizer
  protected StreamTokenizer tokenizer;
  
  // the human player
  protected TextHumanPlayer player;
  
  // the in-progress move
  protected Move move;
  
  // the game-thread
  protected Thread game;
  
  public TextDriver() {
    this.player = new TextHumanPlayer();
    this.tokenizer = new StreamTokenizer(System.in);
    this.backgammon = new Backgammon(player, new BackPropPlayer2(0.8,0.1,true));
    this.tokenizer.resetSyntax();
    this.tokenizer.wordChars('0', 'z');
    this.tokenizer.whitespaceChars('\0', ' ');
    this.tokenizer.eolIsSignificant(true);
    this.tokenizer.parseNumbers();
    this.tokenizer.lowerCaseMode(true);
    
    game = new Thread("Game Thread") {
      public void run() {
        while (true) {
          try {
            backgammon.run();
          } catch (NewGameException e) {
            System.out.println("Resetting to a new game!");
          }

          backgammon.reset();
        }
      }
    };
    
    game.start();
  }
  
  public static void main(String[] args) throws IOException {
    TextDriver driver = new TextDriver();
    
    driver.go();
  }
  
  protected void go() throws IOException {    
    while (true) {
      if (move == null)
        backgammon.print(System.out);
      else
        backgammon.print(System.out, move.getCurrentBoard());
      
      while (true) {
        System.out.println();
        System.out.print("> ");
      
        if (tokenizer.nextToken() == StreamTokenizer.TT_WORD) {    
          try {
            handleCommand(tokenizer.sval);
            break;
          } catch (BackgammonException e) {
            System.out.println();
            System.out.println(e.getMessage());
          }
        }
      }
    }
  }
  
  protected void handleCommand(String command) throws UnrecognizedCommandException, IOException {
    try {      
      if (command.equals("print")) {
        handlePrintCommand();
        return;
      } else if (command.equals("reset")) {
        handleResetCommand();
        return;
      } else if (command.equals("help")) {
        handleHelpCommand();
        throw new IllegalMoveException("");
      } else if (command.equals("quit")) {
        System.exit(0);
      }  
      
      if (backgammon.isGameOver())
        gameOver();
      
      if (command.equals("move")) {
        handleMoveCommand();
      } else if (command.equals("undo")) {
        handleUndoCommand();
      } else if (command.equals("list")) {
        handleListCommand();
      } else {
        throw new UnrecognizedCommandException(command);
      }
      
      if (backgammon.isGameOver()) {
        gameOver();
      }
    } finally {
      if (tokenizer.ttype != StreamTokenizer.TT_EOL)
        while (tokenizer.nextToken() != StreamTokenizer.TT_EOL) {}
    }
  }
    
  protected void handleHelpCommand() {
    System.out.println("Comp440 Backgammon Command Help :\n");
    System.out.println("\thelp\t\t\t\tPrints this help message.");
    System.out.println("\tquit\t\t\t\tQuits the comp440 backgammon game.");
    System.out.println("\treset\t\t\t\tResets the backgammon to a new game.");
    System.out.println("\tprint\t\t\t\tPrints the current state of the board.");
    System.out.println("\tlist\t\t\t\tLists all of the current valid moves.");
    System.out.println("\tmove\t\t\t\tPerforms a move.");
    System.out.println();
    System.out.println("\tThe move syntax is a sequence of pairs corresponding to ");
    System.out.println("\tman movements. For example, the move\n");
    System.out.println("\tmove 13 7 8 2\n");
    System.out.println("\tmoves one man from 13 to 7 and another from 8 to 2.  You ");
    System.out.println("\tcan also use the tokens 'bar' and 'off' to signify moving ");
    System.out.println("\tfrom the bar and off the bar.  For example,\n");
    System.out.println("\tmove bar 19 19 17");
    System.out.print("\tmove 5 off 3 off");
  }
  
  protected void handleResetCommand() {
    synchronized (player.lock) {
      game.interrupt();
      try {
        player.lock.wait();
      } catch (InterruptedException e) {}
    }
  }
    
  protected void handleListCommand() {
    Move[] moves = backgammon.getMoves();
    
    System.out.println("\nAvailable moves:");
    
    for (int i=0; i<moves.length; i++)
      System.out.println(moves[i] + "");
    
    throw new IllegalMoveException("");
  }
  
  protected void handlePrintCommand() {
  }
    
  protected void handleUndoCommand() {
    if (move != null)
      move = new Move(backgammon.getDice(), backgammon.getCurrentBoard(), backgammon.getCurrentPlayer());
  }
  
  protected void handleMoveCommand() throws IOException {
    int type = tokenizer.nextToken();
    
    try {
      while (type != StreamTokenizer.TT_EOL) {
        Movement movement = handleMoveCommandHelper();
        
        if (movement != null)
          move.addMovement(movement);
                
        type = tokenizer.nextToken();
      }
      
      if (move.movePossible())
        throw new IllegalMoveException("You must use all of the dice!");
      
      player.moveDone();
    } catch (IllegalMoveException e) {
      handleUndoCommand();
      
      throw e;
    }
  }
  
  protected void gameOver() {
    throw new GameOverException("Game Over: Player " + (backgammon.getCurrentPlayer() == Board.WHITE ? "O" : "X") + " wins!");
  }
  
  protected Movement handleMoveCommandHelper() throws IOException {
    int type = tokenizer.ttype;
    
    if (type == StreamTokenizer.TT_WORD) {
      if (tokenizer.sval.equals("bar")) {
        int type2 = tokenizer.nextToken();
        
        if (type2 != StreamTokenizer.TT_NUMBER)
          throw new ImproperlyFormattedCommandException("move", "Unexpected token " + type2);

        return new BarMovement(backgammon.getCurrentPlayer(), (int) tokenizer.nval-1);
      } else {
        throw new ImproperlyFormattedCommandException("move", "Unknown string " + tokenizer.sval);
      }
    } else if (type == StreamTokenizer.TT_NUMBER) {
      int source = (int) tokenizer.nval-1;
      int type2 = tokenizer.nextToken();
      
      if (type2 == StreamTokenizer.TT_NUMBER) { 
        return new NormalMovement(backgammon.getCurrentPlayer(), source, (int) tokenizer.nval-1);
      } else if (type2 == StreamTokenizer.TT_WORD) {
        if (tokenizer.sval.equals("off")) {
          return new BearOffMovement(backgammon.getCurrentPlayer(), source);
        } else {
          throw new ImproperlyFormattedCommandException("move", "Unknown string " + tokenizer.sval);
        }
      } else {
        throw new ImproperlyFormattedCommandException("move", "Unexpected token " + type2);
      }
    } else if (type == StreamTokenizer.TT_EOL) {
      return null;
    } else {
      throw new ImproperlyFormattedCommandException("move", "Unexpected token " + type);
    }
  }
    
    protected class TextHumanPlayer implements Player {
      
      Object lock = new Object();
      
      public Move move(Backgammon backgammon) {
    	//System.out.println(backgammon.getCurrentPlayer());
        move = new Move(backgammon.getDice(), backgammon.getCurrentBoard(), backgammon.getCurrentPlayer());
        
        synchronized (lock) {
          try {
            lock.notifyAll();
            lock.wait();
          } catch (InterruptedException e) {
            throw new NewGameException();
          }
        }
        
        Move result = move;
        move = null;
        return result;
      }
      
      public void moveDone() {
        synchronized (lock) {
          try {
            lock.notifyAll();
            lock.wait();
          } catch (InterruptedException cannotHappen) {}
        }
      }
      
      public void won(Backgammon game) {
    	  System.out.println("Congratulations!  You won!");
      }
      
      public void lost(Backgammon game) {
    	  System.out.println("Too bad - you lost!");
      }
    }
}