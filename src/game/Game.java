package game;

import java.util.ArrayList;
import java.util.List;

public class Game {
	
	/**
	 * Representation of the small squares.
	 */
	private int[] babyTable;
	
	/**
	 * Representation of the big squares.
	 */
	private int[] daddyTable;
	
	/**
	 * Number of moves for each daddy square.
	 */
	private int[] movesPerDaddySquare;
	
	/**
	 * The current play.
	 */
	private int currentPlayer;
	
	/**
	 * Number of free squares on the board.
	 */
	private int nbFreeSquare;
	
	public static final int EMPTY = 0;
	public static final int CROSS = 1;
	public static final int CIRCLE = 2;
	public static final int DRAW = 3;
	
	public Game() {
		this.currentPlayer = CROSS;

		this.nbFreeSquare = 81;
		
		this.babyTable = new int[81];
		this.daddyTable = new int[9];
		this.movesPerDaddySquare = new int[9];
		
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
	
	public List<Integer> getSuccessors(int lastMove) {
		List<Integer> successors = new ArrayList<Integer>();
		
		if (lastMove == -1) {
			for (int i=0; i<81; i++) {
				successors.add(i);
			}
			
			return successors;
		}
				
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
			for (int i=0; i<9; i++) {
				if (this.daddyTable[i] == EMPTY) {
					int tmp = i*9;
					for (int j=tmp; j<tmp+9; j++) {
						successors.add(j);
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
		
		System.out.println("Play " + move);
		this.changePlayer();
	}
	
	public void unplay(int move) {
		int square = move/9;
		
		if (this.daddyTable[square] != EMPTY) {
			this.daddyTable[square] = EMPTY;
			this.nbFreeSquare += (9-this.movesPerDaddySquare[square]);	
		}
		
		this.babyTable[move] = EMPTY;
		this.movesPerDaddySquare[square]--;
		this.nbFreeSquare++;
		
		System.out.println("Unplay " + move);
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
	public int isEndOfGame(int move) {
		
		// The board is empty.
		if (move == -1) {
			return 0;
		}
		
		int normalizedMove = move/9;
		
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
	
	private boolean winHorizontally(int[] table, int normalizedMove) {
		int tmp = normalizedMove/3;
		return table[tmp] != DRAW && table[tmp] != EMPTY && table[tmp] == table[tmp+1] && table[tmp+1] == table[tmp+2];
	}
	
	private boolean winVertically(int[] table, int normalizedMove) {
		int tmp = normalizedMove % 3;
		return table[tmp] != DRAW && table[tmp] != EMPTY && table[tmp] == table[tmp+3] && table[tmp+3] == table[tmp+6];
	}
	
	private boolean winDiagonally(int[] table, int normalizedMove) {
		if (normalizedMove % 2 != 0 || table[4] == DRAW || table[4] == EMPTY) {
			return false;
		}
	
		return table[4] == table[0] && table[4] == table[8] || table[4] == table[2] && table[4] == table[6];
	}
	
	private boolean winDaddySquare(int move) {
		int[] subtable = new int[9];
		int normalizedMove = move%this.daddyTable.length;
		int rg = move / 9;
	
		for (int i=0 ; i<9 ; i++) {
			subtable[i] = this.babyTable[i+rg];
		}
		
		return this.winHorizontally(subtable, normalizedMove)
				|| this.winVertically(subtable, normalizedMove)
				|| this.winDiagonally(subtable, normalizedMove);
	}
	
	public int getScore(int player) {
		switch(player){
			case CROSS:
				return 1;
			case CIRCLE:
				return -1;
		}
		return 0;	
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