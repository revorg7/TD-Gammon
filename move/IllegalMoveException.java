package move;

import game.BackgammonException;

public class IllegalMoveException extends BackgammonException {
  public IllegalMoveException(String s) {
    super(s);
  }
} 
