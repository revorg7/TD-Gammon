package player;

import board.Board;
import move.IllegalMoveException;

public class Utility {
	/*
	 * Return the negative of output[0] because in minimax game, goal is to minimize
	 * otherplayer's utility
	 */
	public static double computeUtility(double[] output){
		return -output[0];	
	}
	public static Board createInverseBoard(Board board){
	//Data-structures, because I don't want to create getters,setters for creating an inverse board
	int[] new_board,colors,bar,off;
	new_board = new int[Board.NUM_SPIKES];
	colors = new int[Board.NUM_SPIKES];
	bar = new int[2];
	off = new int[2];
	bar[Board.BLACK] = board.getBar(Board.WHITE);
	bar[Board.WHITE] = board.getBar(Board.BLACK);
	off[Board.BLACK] = board.getOff(Board.WHITE);
	off[Board.WHITE] = board.getOff(Board.BLACK);
    for (int i=0; i<Board.NUM_SPIKES; i++) {
    	if ( board.getColor(Board.NUM_SPIKES - 1 -i ) == Board.BLACK ){
    		new_board[i] = board.getBlackPieces(Board.NUM_SPIKES - 1 -i);
    		colors[i] = Board.WHITE; 
    	}
    	else if (board.getColor(Board.NUM_SPIKES - 1 -i ) == Board.WHITE){
    		new_board[i] = board.getWhitePieces(Board.NUM_SPIKES - 1 -i);
    		colors[i] = Board.BLACK;
    	}
    	else{
    		new_board[i]=0;
    		colors[i]=Board.NEITHER;
    	}
    	
      }	
    return new Board();
    }
	
	/* I don't encode the information that whose turn is it, black or white
	 * hence only 196 input units
	 */
	public static double[] boardToVector(Board board){
		double[] input = new double[196];
		
		//Doing for each of 4 units of White Pieces each at 24 spikes
		int counter=0;
		int white_pieces;
		for (int i=0;i < Board.NUM_SPIKES;i++){
			white_pieces = board.getWhitePieces(i);
			if (white_pieces == 0){
				for (int j=0;j<4;j++){
					input[counter]=0;
					counter+=1;
				}
			}
			else if (white_pieces==1){
				input[counter]=1;
				counter+=4;
			}
			else if (white_pieces==2){
				input[counter]=1;
				input[counter+1]=1;
				counter+=4;
			}
			else if (white_pieces>=3){
				for (int j=0;j<3;j++){
					input[counter]=1;
					counter+=1;
				}
				input[counter]= (white_pieces - 3.0)/2.0;
				counter+=1;
			}
		}
		
		//Doing for the last 4 units
		input[counter]=board.getBlackBar()/2.0;
		input[counter+1]=board.getWhiteBar()/2.0;
		input[counter+2]=board.getBlackOff()/15.0;
		input[counter+3]=board.getWhiteOff()/15.0;
		counter+=4;
		
		//Doing for each of 4 units of Black Pieces each at 24 spikes
		white_pieces=0;
		for (int i=0;i < Board.NUM_SPIKES;i++){
			white_pieces = board.getBlackPieces(i); //Variable name is white piece but it is indeed black
			if (white_pieces == 0){
				for (int j=0;j<4;j++){
					input[counter]=0;
					counter+=1;
				}
			}
			else if (white_pieces==1){
				input[counter]=1;
				counter+=4;
			}
			else if (white_pieces==2){
				input[counter]=1;
				input[counter+1]=1;
				counter+=4;
			}
			else if (white_pieces>=3){
				for (int j=0;j<3;j++){
					input[counter]=1;
					counter+=1;
				}
				input[counter]= (white_pieces - 3.0)/2.0;
				counter+=1;
			}
		}
		if (counter!=196){
			throw new IllegalMoveException("Board to NN input not working properly");
		}
		return input;
	}
	
	
	/*
	 * Creates BoardtoVec for opponent, better than inverting the board's internal representation each time
	 */
	public static double[] boardToVectorInv(Board board){
		double[] input = new double[196];
		int counter=0;
		int white_pieces;
		for (int i=Board.NUM_SPIKES-1;i > -1 ;i--){
			white_pieces = board.getBlackPieces(i);
			if (white_pieces == 0){
				for (int j=0;j<4;j++){
					input[counter]=0;
					counter+=1;
				}
			}
			else if (white_pieces==1){
				input[counter]=1;
				counter+=4;
			}
			else if (white_pieces==2){
				input[counter]=1;
				input[counter+1]=1;
				counter+=4;
			}
			else if (white_pieces>=3){
				for (int j=0;j<3;j++){
					input[counter]=1;
					counter+=1;
				}
				input[counter]= (white_pieces - 3.0)/2.0;
				counter+=1;
			}
		}
		input[counter]=board.getWhiteBar()/2.0;
		input[counter+1]=board.getBlackBar()/2.0;
		input[counter+2]=board.getWhiteOff()/15.0;
		input[counter+3]=board.getBlackOff()/15.0;
		counter+=4;
		
		//Remaining color pieces
		white_pieces=0;
		for (int i=Board.NUM_SPIKES-1;i > -1 ;i--){
			white_pieces = board.getWhitePieces(i);
			if (white_pieces == 0){
				for (int j=0;j<4;j++){
					input[counter]=0;
					counter+=1;
				}
			}
			else if (white_pieces==1){
				input[counter]=1;
				counter+=4;
			}
			else if (white_pieces==2){
				input[counter]=1;
				input[counter+1]=1;
				counter+=4;
			}
			else if (white_pieces>=3){
				for (int j=0;j<3;j++){
					input[counter]=1;
					counter+=1;
				}
				input[counter]= (white_pieces - 3.0)/2.0;
				counter+=1;
			}
		}

		if (counter!=196){
			throw new IllegalMoveException("Board to NN input not working properly");
		}
		return input;
	}

}
