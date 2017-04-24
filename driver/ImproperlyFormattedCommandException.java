package driver;

import game.BackgammonException;

public class ImproperlyFormattedCommandException extends BackgammonException {
  protected ImproperlyFormattedCommandException(String s) {
    super("Improperly formatted command: '" + s + "' - type 'help' for assistance.");
  }
  
  protected ImproperlyFormattedCommandException(String s, String m) {
    super("Improperly formatted command: '" + s + "' - " + m);
  }
}