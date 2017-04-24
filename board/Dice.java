package board;

import java.io.*;
import java.util.*;

public class Dice {
  
  // the minimum and maximum dice values
  public static int MIN_VALUE = 1;
  public static int MAX_VALUE = 6;
  
  // the number of dice to simulate
  public static int NUM_DICE = 2;
  
  // the internal represetation of all the dice
  private static Dice[][] DICE = new Dice[MAX_VALUE-MIN_VALUE+1][MAX_VALUE-MIN_VALUE+1];
  
  // build the internal representation
  static {
    for (int i=MIN_VALUE; i<=MAX_VALUE; i++)
      for (int j=MIN_VALUE; j<=MAX_VALUE; j++)
        DICE[i-1][j-1] = new Dice(i,j);
  }
    
  // the random number generator
  private static Random random = new Random();
  
  // the value of the dies
  protected int die1;
  protected int die2;
  
  /**
   * Builds some new dice
   */
  private Dice(int i, int j) {
    this.die1 = i;
    this.die2 = j;
  }
  
  /**
   * Method which returns a specific dice roll.  The numbers should be
   * the desired roll of the dice (i.e. on the range (1-6) not (0-5)).
   *
   * @param die1 The first die
   * @param die2 The second die
   */
  public static Dice getDice(int i, int j) {
    return DICE[i-1][j-1];
  }
  
  /**
   * Method which rolls the dice
   */
  public static Dice roll() {
    return DICE[random.nextInt(MAX_VALUE-MIN_VALUE+1)][random.nextInt(MAX_VALUE-MIN_VALUE+1)];
  }
  
  /**
   * Returns the value of the first die
   *
   * @return the value of the first die
   */
  public int getDie1() {
    return die1;
  }
  
  /**
   * Returns the value of the second die
   *
   * @return the value of the first die
   */
  public int getDie2() {
    return die2;
  }
  
  /**
   * Returns whether or not the dice are doubles
   *
   * @return Whether or not the dice are doubles
   */
  public boolean isDoubles() {
    return die1 == die2;
  }
  
  /**
   * Prints out a gnubg-style text board.
   *
   * @param out The output stream to write the dice to
   */
  public void print(PrintStream out) {
    out.println("Dice: " + die1 + " " + die2);
  }
    
}