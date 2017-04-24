package driver;

import java.io.IOException;
import java.io.StreamTokenizer;

import driver.TextDriver.TextHumanPlayer;
import game.Backgammon;
import game.BackgammonException;
import move.Move;
import player.BackPropPlayer2;
import player.Player;

public class SimulationDriver {
	  // the Backgammon used to run this driver, one for each instance of SimulationDrover
	  protected Backgammon game;
	  
	  public SimulationDriver() {
		    this.game = new Backgammon(new BackPropPlayer2(0.7,0.1,true), new BackPropPlayer2(0.7,0.1,true));		    
		  }

	  protected void go() throws IOException {
		  
		  long start = System.currentTimeMillis();
		  int k=0;
		  while (k<1000){
			  	game.run();
		    	//System.out.println(k);
		    	//System.out.println("Player won is "+ game.getOtherPlayer());
		    	//game.print(System.out, game.getCurrentBoard());
		    	game.reset();
		    	k+=1;
		  	}
		  long elapsedTime = System.currentTimeMillis() - start;
		  System.out.println("Total time in mins: "+ elapsedTime/60000);
		  
		  //Calling one last time to recover the final NN should suffice
		  BackPropPlayer2 winner = (BackPropPlayer2) game.run();
		  /*This method doesn't need to go inside loop, because
		   * for a given simulation, the static variable net for the two
		   * variables is already initialized once (either randomly or not)
		   */
	    	winner.net.writeTo("SavedNN");

		  }
	  
	  public static void main(String[] args) throws IOException {
		  
		  //Each simulation-driver instance is a separate simulation, call multiple to run multiple simulations
		    SimulationDriver driver = new SimulationDriver();
		    
		    driver.go(); //The go command for each simulation thread
		  }

}
