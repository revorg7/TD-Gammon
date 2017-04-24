package driver;

import game.BackgammonException;

public class UnrecognizedCommandException extends BackgammonException {
  protected UnrecognizedCommandException(String s) {
    super("Unrecognized command: '" + s + "'.");
  }
}