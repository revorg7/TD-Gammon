package driver;

import game.BackgammonException;

public class GameOverException extends BackgammonException {
  protected GameOverException(String s) {
    super(s);
  }
}