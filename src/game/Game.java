package game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class Game {
	
	/**
	 * Representation of the small squares.
	 */
	private byte[] babyTable;
	
	/**
	 * Representation of the big squares.
	 */
	private byte[] daddyTable;
	
	/**
	 * Number of moves for each daddy square.
	 */
	private byte[] movesPerDaddySquare;
	
	/**
	 * The current play.
	 */
	private byte currentPlayer;
	
	/**
	 * Number of free squares on the board.
	 */
	private byte nbFreeSquare;
	
	/**
	 * Stack of played moves.
	 */
	private Stack<Integer> playedMoves;
	
	public static final byte EMPTY = 0;
	public static final byte CROSS = 1;
	public static final byte CIRCLE = 2;
	public static final byte DRAW = 3;
	
	public Game() {
		this.currentPlayer = CROSS;

		this.nbFreeSquare = 81;
		
		this.babyTable = new byte[81];
		this.daddyTable = new byte[9];
		this.movesPerDaddySquare = new byte[9];
		this.playedMoves = new Stack<Integer>();
		
		for (int i=0; i<this.babyTable.length; i++) {
			this.babyTable[i] = EMPTY;
		}
		
		for (int i=0; i<this.daddyTable.length; i++) {
			this.daddyTable[i] = EMPTY;
			this.movesPerDaddySquare[i] = 0;
		}
	}
	
	public int getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public List<Integer> getSuccessors() {
		List<Integer> successors = new ArrayList<Integer>();
		
		if (this.playedMoves.empty()) {
			for (int i=0; i<81; i++) {
				successors.add(i);
			}
			
			return successors;
		}
		
		int lastMove = this.playedMoves.peek(); 
				
		int square = lastMove/9;
		int newSquare = lastMove - square*9;
		
		if (this.daddyTable[newSquare] == EMPTY) {
			
			int firstBabySquare = newSquare*9;
			
			for (int i=firstBabySquare; i<firstBabySquare+9; i++) {
				if (this.babyTable[i] == EMPTY) {
					successors.add(i);
				}
			}
		} else {
			for (int i=0; i<this.daddyTable.length; i++) {
				if (this.daddyTable[i] == EMPTY) {
					int tmp = i*9;
					for (int j=tmp; j<tmp+9; j++) {
						if (this.babyTable[j] == EMPTY) {
							successors.add(j);
						}
					}
				}
			}
		}
		
		return successors;
	}
	
	/**
	 * Play the move for the current player.
	 * @param move
	 */
	public void play(int move) {
		int square = move/9;
		
		this.babyTable[move] = this.currentPlayer;
		this.movesPerDaddySquare[square]++;
		this.nbFreeSquare--;
		
		if (this.winDaddySquare(move)) {
			this.daddyTable[square] = this.currentPlayer;
			this.nbFreeSquare -= (9-this.movesPerDaddySquare[square]);
		} else if (this.movesPerDaddySquare[square] == 9) {
			this.daddyTable[square] = DRAW;
		}
		
		this.playedMoves.push(move);
		
		this.changePlayer();
	}
	
	public void unplay() {
		
		int lastMove = this.playedMoves.pop();
		
		int square = lastMove/9;
		
		if (this.daddyTable[square] != EMPTY) {
			this.daddyTable[square] = EMPTY;
			this.nbFreeSquare += (9-this.movesPerDaddySquare[square]);	
		}
		
		this.babyTable[lastMove] = EMPTY;
		this.movesPerDaddySquare[square]--;
		this.nbFreeSquare++;
		
		this.changePlayer();
	}
	
	/**
	 * Change the current player.
	 */
	private void changePlayer() {
		this.currentPlayer = (this.currentPlayer == CROSS) ? CIRCLE : CROSS;
	}
	
	/**
	 * Return the winner if there is any, draw if the game is finished, 0 otherwise.
	 * @param move
	 * @return CROSS, CIRCLE, DRAW or 0
	 */
	public int isEndOfGame() {
		
		// There are not enough moves played to have a winning situation.
		if (this.playedMoves.size() < 17) {
			return 0;
		}
		
		int lastMove = this.playedMoves.peek();
		
		int normalizedMove = lastMove/9;
		
		if(this.winHorizontally(this.daddyTable, normalizedMove)
				|| this.winVertically(this.daddyTable, normalizedMove)
				|| this.winDiagonally(this.daddyTable, normalizedMove)) {
			return this.currentPlayer == CROSS ? CIRCLE : CROSS;
		}
		
		if (this.nbFreeSquare == 0) {
			return DRAW;
		}
		
		return 0;
	}
	
	private boolean winHorizontally(byte[] table, int normalizedMove) {
		int tmp = normalizedMove/3;
		tmp *= 3;
		return table[tmp] != DRAW && table[tmp] != EMPTY && table[tmp] == table[tmp+1] && table[tmp+1] == table[tmp+2];
	}
	
	private boolean winVertically(byte[] table, int normalizedMove) {
		int tmp = normalizedMove % 3;
		return table[tmp] != DRAW && table[tmp] != EMPTY && table[tmp] == table[tmp+3] && table[tmp+3] == table[tmp+6];
	}
	
	private boolean winDiagonally(byte[] table, int normalizedMove) {
		// NormalizedMove is even.
		if (normalizedMove%2 != 0 || table[4] == DRAW || table[4] == EMPTY) {
			return false;
		}
	
		return table[4] == table[0] && table[4] == table[8] || table[4] == table[2] && table[4] == table[6];
	}
	
	private boolean winDaddySquare(int move) {
		byte[] subtable = new byte[9];
		int normalizedMove = move%this.daddyTable.length;
		int rg = move / 9;
		rg *= 9;
	
		for (int i=0 ; i<9 ; i++) {
			subtable[i] = this.babyTable[i+rg];
		}
		
		return this.winHorizontally(subtable, normalizedMove)
				|| this.winVertically(subtable, normalizedMove)
				|| this.winDiagonally(subtable, normalizedMove);
	}
	
	public int getScore(int player, int winner) {
		int adversary = player == CIRCLE ? CROSS : CIRCLE;
		
		if (player == winner) {
				return Integer.MAX_VALUE;
		} else if (adversary == winner) {
				return Integer.MIN_VALUE;
		}
		
		return 0;	
	}
	
	public int eval(int player) {
		
		int[] babyTableEval = new int[9];
		
		int adversary = player == CIRCLE ? CROSS : CIRCLE; 
		
		for (int i=0; i<9; i++) {
			if (this.daddyTable[i] == player) {
				babyTableEval[i] = 40;
				break;
			} else if (this.daddyTable[i] == adversary) {
				babyTableEval[i] = -40;
				break;
			} else if (this.daddyTable[i] == DRAW) {
				babyTableEval[i] = 0;
				break;
			} else if (this.daddyTable[i] == EMPTY) {
				int nbX = 0;
				int nbO = 0;
				int start = i*9;
				int end = start+9;
				for (int j=start; j<end; j++) {
					if (this.babyTable[j] == player) {
						nbX++;
					} else if (this.babyTable[j] == adversary) {
						nbO++;
					}
				}
				babyTableEval[i] = (int) (Math.pow(nbX, 2) - Math.pow(nbO, 2));
			}
		}
		
		return 8*babyTableEval[4]+3*(babyTableEval[0]+babyTableEval[2]+babyTableEval[6]+babyTableEval[8])+babyTableEval[1]+babyTableEval[3]+babyTableEval[5]+babyTableEval[7];
	}
	
	public int playOut() {
		int res;
		Random rand = new Random();
		
		while ((res = this.isEndOfGame()) == 0) {
			List<Integer> successors = this.getSuccessors();
			int rg = 0;
			if (successors.size()>1) {
				rg = rand.nextInt(successors.size()-1);
			}
			this.play(successors.get(rg));
		}
		
		return res;
	}
	
	public Game clone() {
		Game clone = new Game();
		
		clone.babyTable = this.babyTable.clone();
		clone.daddyTable = this.daddyTable.clone();
		clone.movesPerDaddySquare = this.movesPerDaddySquare.clone();
		clone.currentPlayer = this.currentPlayer;
		clone.nbFreeSquare = this.nbFreeSquare;
		clone.playedMoves = new Stack<Integer>();
		clone.playedMoves.addAll(this.playedMoves);
		
		return clone;
	}
	
	public String toString() {
		String display = "";
		String[] representation = {" ", "X", "O", "-"};
	
		for (int b=0; b<81; b+=27) {
			for (int i=0; i<9; i+=3) {
				for (int j=0; j<27; j+=9) {
					for (int k=0; k<3; k++) {
						if (this.daddyTable[(b+i+j+k)/9] != EMPTY) {
							display += " " + representation[this.daddyTable[(b+i+j+k)/9]] + " ";
						} else {
							display += " " + representation[this.babyTable[b+i+j+k]] + " ";
						}
					}
			
					if (j != 18) {
						display += " || ";
					}
				}
				display += "\n";
			}
			
			if (b != 54) {
				display += "===================================\n";
			}
		}
		
		return display;
	}
}