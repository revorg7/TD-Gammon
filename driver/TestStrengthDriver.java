package driver;

import java.io.IOException;

import board.Board;
import game.Backgammon;
import player.BackPropPlayer2;
import player.Player;
import player.RandomPlayer;

public class TestStrengthDriver {
	  // the Backgammon used to run this driver, one for each instance of SimulationDrover
	  protected Backgammon game;
	  
	  public TestStrengthDriver() {
		    this.game = new Backgammon(new BackPropPlayer2(0.7,0.1,false), new RandomPlayer());		    
		  }

	  protected void go() throws IOException {
		  
		  long start = System.currentTimeMillis();
		  int k=0; int count=0;
		  while (k<1000){
			  	game.run();
			  	if (game.getOtherPlayer() == Board.BLACK ) {
			  	  count+=1;
			  	}
		    	//System.out.println(k);
		    	//System.out.println("Player won is "+ game.getOtherPlayer());
		    	//game.print(System.out, game.getCurrentBoard());
		    	game.reset();
		    	k+=1;
		  	}
		  long elapsedTime = System.currentTimeMillis() - start;
		  System.out.println("Black won "+count/(double)k+" percent of the times");
		  System.out.println("Total time in mins: "+ elapsedTime/60000);
		  
		  }
	  
	  public static void main(String[] args) throws IOException {
		  
		  //Each simulation-driver instance is a separate simulation, call multiple to run multiple simulations
		    TestStrengthDriver driver = new TestStrengthDriver();
		    
		    driver.go(); //The go command for each simulation thread
		  }

}
