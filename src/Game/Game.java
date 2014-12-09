package Game;

public class Game {

	/**
	 * Representation of the small squares.
	 */
	private int[] babyTable;
	/**
	 * Representation of the big squares.
	 */
	private int[] daddyTable;
	
	private int[] movesPerDaddySquare;
	
	private int currentPlayer;
	
	private int nbMoves;
	
	public static final int EMPTY = 0;
	public static final int CROSS = 1;
	public static final int CIRCLE = 2;
	public static final int DRAW = 3;
	
	public Game() {
		this.currentPlayer = CROSS;
		
		this.nbMoves = 0;
		
		this.babyTable = new int[81];
		this.daddyTable = new int[9];
		this.movesPerDaddySquare = new int[9];
				
		for (int i=0 ; i < this.babyTable.length ; i++) {
			this.babyTable[i] = EMPTY; 
		}
		
		for (int i=0 ; i < this.daddyTable.length ; i++) {
			this.daddyTable[i] = EMPTY; 
			this.movesPerDaddySquare[i] = 0;
		}
	}
	
	public int getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public void play(int move) {
		this.babyTable[move] = this.currentPlayer;
		if (this.winDaddySqare(move)) {
			this.daddyTable[move / this.daddyTable.length] = this.currentPlayer;
		} else if (this.movesPerDaddySquare[move / this.daddyTable.length] == 9) {
			this.daddyTable[move / this.daddyTable.length] = DRAW;
		}
		
		this.nbMoves++;
		
		this.changePlayer();
	}
	
	public void unplay(int move) {
		this.babyTable[move] = EMPTY;
		this.daddyTable[move / this.daddyTable.length] = EMPTY;
		
		this.nbMoves--;
		
		this.changePlayer();		
	}
	
	private void changePlayer() {
		this.currentPlayer = (this.currentPlayer == CROSS) ? CIRCLE : CROSS;
	}
	
	/**
	 * Return the winner if there is any, draw if the game is finished, 0 otherwise.
	 * @param move
	 * @return CROSS, CIRCLE, DRAW or 0
	 */
	public int isEndOfGame(int move) {
		int normalizedMove = move/this.daddyTable.length;
		
		if(this.winHorizontally(this.daddyTable, normalizedMove) 
				|| this.winVertically(this.daddyTable, normalizedMove)
				|| this.winDiagonally(this.daddyTable, normalizedMove)) {
			return this.currentPlayer == CROSS ? CIRCLE : CROSS;
		}
		
		if (this.nbMoves == 81) {
			return DRAW;
		}
		
		return 0;
	}
	
	private boolean winHorizontally(int[] table, int normalizedMove) {
		int tmp = normalizedMove/3;
		return table[tmp]!=DRAW && table[tmp] == table[tmp+1] && table[tmp+1] == table[tmp+2];
	}
	
	private boolean winVertically(int[] table, int normalizedMove) {
		int tmp = normalizedMove%3;
		return table[tmp]!=DRAW && table[tmp] == table[tmp+3] && table[tmp+3] == table[tmp+6];
	}
	
	private boolean winDiagonally(int[] table, int normalizedMove) {
		if (normalizedMove%2 != 0 || table[4] == DRAW) {
			return false;
		}
		
		return table[4] == table[0] &&  table[4] == table[8] || table[4] == table[2] &&  table[4] == table[6];
	}
	
	private boolean winDaddySqare(int move) {
		int[] subtable = new int[9];
		int normalizedMove = move%this.daddyTable.length;
		int rg = move/9;
		
		for (int i=0 ; i<9 ; i++) {
			subtable[i] = this.babyTable[i+rg];
		}
		
		return this.winHorizontally(subtable, normalizedMove) 
				|| this.winVertically(subtable, normalizedMove)
				|| this.winDiagonally(subtable, normalizedMove);
	}
	
	public int getScore() {
		return this.currentPlayer == CROSS ? 1 : -1;
	}
	
	public String toString() {
		String display = "";
		String[] representation = {" ", "X", "O", "-"};
		
		for (int b=0; b<81; b+=27) {
			for (int i=0; i<9 ; i+=3) {
				for (int j=0; j<27; j+=9) {
					display += " " + representation[this.babyTable[b+i+j]] + " " 
							+ " " + representation[this.babyTable[b+i+1+j]] + " "
							+ " " + representation[this.babyTable[b+i+2+j]] + " ";
					if (j != 18) {
						display += " || ";
					}
				}
				display += "\n";
			}
			
			if (b!=54) {
				display += "===================================\n";
			}
		}
		
		return display;
	}
	
	public static void main(String[] args) {
		Game game = new Game();
		
		System.out.println(game.toString());
	}
}
