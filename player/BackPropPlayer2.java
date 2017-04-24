
package player;

import game.Backgammon;
import move.Move;
import net.*;

import java.io.File;
import java.io.IOException;

import board.Board;
import player.Utility;

/**
 * He is right, we don't have to monkey with next output, as it always learns the probability of winning for black(or white)
 * He's also right in doing the end of game training by calling lost(game), since we dont need the state of game just before lost.
 * As we will be training from backprop[input,V(winning-state),1.0]
 * NOTE: The NN here is trained to learn prob(white),prob(black) [BASED ON HOW YOU DEFINE ACTUAL[] IN LOST/WON METHODS AND NOT BASED ON THE
 * NN REPRESENTATION USED]
 * NOTE: But also note that, during play-time, each player sees the board as follows:
 * 1. Opponent's pieces from my homeboard to 24
 * 2. myBsr, oppBar, myOff, oppOff
 * 3. My pieces from my homeboard to 24
 */
public class BackPropPlayer2 implements Player {
	
	//Static because shared with otherplayer instance
	static public NeuralNetwork net = new NeuralNetwork(196, new int[] {40, 1});
	private final boolean learningMode;
	private double LAMBDA,ALPHA,BETA;
	//Not declared static as each opponent has its own copy of eligibility traces(not sure if its correct)
	private double[][] Ew = new double[net.hidden[0].length][net.hidden[1].length];
	private double[][][] Ev = new double[net.input.length][net.hidden[0].length][net.hidden[1].length];
	public BackPropPlayer2(double lambda,double alpha, boolean learningmode){
		for (int j = 0; j < net.hidden[0].length; j++)
		for (int k = 0; k < net.hidden[1].length; k++) {
			Ew[j][k]=0.0;
			for (int i = 0; i < net.input.length; i++) Ev[i][j][k] = 0.0;
		}
		LAMBDA = lambda;
		ALPHA = alpha*0.8;
		BETA = alpha*0.2;
		learningMode = learningmode;
		
		if (new File("SavedNN").isFile()){
			try {
				net = NeuralNetwork.readFrom("SavedNN");
				System.out.println("Import of old NN successful");
			} catch (ClassNotFoundException  e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
			}
			catch (IOException e){
				e.printStackTrace();
			}
		}
	}
	
	public Move move(Backgammon backgammon) {
	int thisPlayer = backgammon.getCurrentPlayer();
	int otherPlayer = backgammon.getOtherPlayer();
	Move bestMove = null;
	double expectedUtility = -1.0;
	for (Move m: backgammon.getMoves()) {
	/*
	* output is an array of 1-4 depending on configuration
	*
	* evaluate the next board for the OTHER PLAYER; you’ll
	* have to adjust your utility accordingly...
	*
	* getCurrentBoard is the board AFTER the move
	*/
	double output[] = getValue(m.getCurrentBoard(), otherPlayer);
	double utility = Utility.computeUtility(output);
	if (utility > expectedUtility) {
	bestMove = m;
	expectedUtility = utility;
	}
	}
	if (learningMode) {
	/* Nextoutput is only needed here onwards*/
	double nextoutput [] = null;
	/* you’ll need to pass the input values to backprop... */
	double currentinput[] = null;
	if (backgammon.getCurrentPlayer()==Board.BLACK) currentinput = Utility.boardToVector(bestMove.getOriginalBoard());
	else currentinput = Utility.boardToVectorInv(bestMove.getOriginalBoard());
	/* get original board is the board BEFORE the move... i.e., right now */
	/*
	* OH OH!!!! WARNING WARNING WARNING, DANGER WILL ROBINSON!!!
	* you must call the neural net’s get value on the original input
	* BEFORE you call backprop. The HiddenUnit class has a "getValue" that
	* is cached; it is not computed every time. If you don’t call this
	* before backprop, then the nodes will have the values set from
	* the last call to the whole network’s getValue() which may or may not
	* be the one you want
	*/
	double currentoutput[] = getValue(bestMove.getOriginalBoard(), thisPlayer);
	/*
	9
	* notice, you don’t have to monkey with nextoutput even though it is
	* for the other player... this is because it’s output is player independent.
	* if one output, it equals odds of white winning, if two outputs than
	* one is odds of white winning, other is odds of black winning
	*UPDATE: BUT INDEED, we have to monkey with nextoutput, as it is calculated from the other player's perspective
	*UPDATE: He's right, no need to monkey with nextoutput, as explained at the very top (near class declaration)
	*UPDATE: BUT INDEED, although the predicted value always gives the prob of winning for black(or white), but this
	*calculted using an inverted state-representation, furthermore, for TD-learning, the value of next-state needs
	*to be calculed from thisPlayer's perspective only
	*UPDATE: After reflecting again(see the arguments at the class declaration), I believe that he was right(or not !!????!!!!, still confused)
	*UPDATE: No, although both players see the board invariantly, the state rep for one might be different than for the other
	*/
	nextoutput = getValue(bestMove.getCurrentBoard(),thisPlayer);
	backprop(currentinput, currentoutput, nextoutput);
		
	}
	return bestMove;
	}


	/*
	 * On any call be it lost or won, the game is already over,
	 * so the game.currentplayer is loser, and game.otherplayer is winner, since
	 * the board is already updated before checking the gameover condition.
	 * And since this player has been called, so he in particular lost i.e
	 * he is the game.currentplayer
	 */
	public void lost(Backgammon game) {
		if (learningMode) {
		/* assuming 1 output although the original code assumed 2 outputs */
		double actual[] = new double[1];
		/* For this player who is called, its always a loss for him*/
		actual[0] = 0.0; 
		/* you’ll have to save currentBoard from the move method or something */
		double[] in = new double[196];
		if (game.getCurrentPlayer() == Board.BLACK)in = Utility.boardToVector(game.getCurrentBoard());
		else in = Utility.boardToVectorInv(game.getCurrentBoard());
		double out[] = net.getValue(in);
		backprop(in, out, actual);
		}
		}

	/*
	 * On any call be it lost or won, the game is already over,
	 * so the game.currentplayer is loser, and game.otherplayer is winner, since
	 * the board is already updated before checking the gameover condition.
	 * And since this player has been called, so he in particular won i.e
	 * he is the game.otherplayer
	 */
	public void won(Backgammon game) {
		if (learningMode) {
		/* assuming 1 output although the original code assumed 2 outputs */
		double actual[] = new double[1];
		/* For this player who is called, its always a win for him*/
		actual[0] = 1.0; 
		/* you’ll have to save currentBoard from the move method or something */
		double[] in = new double[196];
		if (game.getOtherPlayer() == Board.BLACK)in = Utility.boardToVector(game.getCurrentBoard());
		else in = Utility.boardToVectorInv(game.getCurrentBoard());
		double out[] = net.getValue(in);
		backprop(in, out, actual);
		}
		}


		//NN training is done here, sice this is not a general backprop algorithm
		public static double gradient(HiddenUnit u) {
		return u.getValue() * (1.0 - u.getValue());
		}
		/* Ew and Ev must be set up somewhere to the proper size and set to 0 */
		public void backprop(double[] in, double[] out, double[] expected) {
		/* compute eligibility traces */
		for (int j = 0; j < net.hidden[0].length; j++)
		for (int k = 0; k < out.length; k++) {
		/* ew[j][k] = (lambda * ew[j][k]) + (gradient(k)*hidden_j) */
		Ew[j][k] = (LAMBDA * Ew[j][k]) + (gradient(net.hidden[1][k]) * net.hidden[0][j].getValue());
		for (int i = 0; i < in.length; i++)
		/* ev[i][j][k] = (lambda * ev[i][j][k]) + (gradient(k)+w[j][k]+gradient(j)+input_i)*/
		Ev[i][j][k] = ( ( LAMBDA * Ev[i][j][k] ) + ( gradient(net.hidden[1][k]) * net.hidden[1][k].weights[j] * gradient(net.hidden[0][j])* in[i]));
		}
		double error[] = new double[out.length];
		for (int k =0; k < out.length; k++)
		error[k] = expected[k] - out[k];
		for (int j = 0; j < net.hidden[0].length; j++)
		for (int k = 0; k < out.length; k++) {
		/* weight from j to k, shown with learning param of BETA */
		net.hidden[1][k].weights[j] += BETA * error[k] * Ew[j][k];
		for (int i = 0; i < in.length; i ++) {
		net.hidden[0][j].weights[i] += ALPHA * error[k] * Ev[i][j][k];
		}
		}
		}
		
		/*
		 * The line in this function player==BLAC, defines here we choose to train
		 * the NN from whose perspective: Black or White, here each player thinks
		 * he is a black player
		 */
		public static double[] getValue(Board board,int player){
			if (player == Board.BLACK)
			return net.getValue(Utility.boardToVector(board) );
			else return net.getValue(Utility.boardToVectorInv(board));
		}

		

}
