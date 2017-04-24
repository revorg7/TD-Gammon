package board;

import game.*;

public class IllegalDiceRollException extends BackgammonException {
  protected IllegalDiceRollException(String s) {
    super(s);
  }
}
