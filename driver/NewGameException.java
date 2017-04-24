package driver;

import game.BackgammonException;

public class NewGameException extends BackgammonException {
  protected NewGameException() {
    super("New game");
  }
}