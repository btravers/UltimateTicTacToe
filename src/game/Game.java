package game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
	
	/**
	 * Zobrist random numbers. Size 2*81.
	 */
	private long[][] randomBabyNumbers;
	private long[][] randomDaddyNumbers;
	private Map<Long, Integer> hitMap;
	
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
		
		this.computeRandoms();
		this.hitMap = new HashMap<Long, Integer>();
	}
	
	public int getCurrentPlayer() {
		return this.currentPlayer;
	}
	
	public byte getDaddySquareValue(int index) {
		return this.daddyTable[Math.min(index, this.daddyTable.length - 1)];
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
		this.addHit();
		
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
	
	public int new_eval(int player) {
		int[] babyTableEval = new int[9];
		
		int adversary = player == CIRCLE ? CROSS : CIRCLE;
		
		// On évalue chaque tic tac toe intermédiaire
		for (int i=0; i<9; i++) {
			if (this.daddyTable[i] == player) {
				babyTableEval[i] = 100;
				break;
			} else if (this.daddyTable[i] == adversary) {
				babyTableEval[i] = -100;
				break;
			} else if (this.daddyTable[i] == DRAW) {
				babyTableEval[i] = 0;
				break;
			} else if (this.daddyTable[i] == EMPTY) {
				int startIndex = i*9;
				
				babyTableEval[i] = 0;
				
				// On vérifie la formation de colonnes
				for (int j=startIndex; j<startIndex+3; j++) {
					// Colonnes formées par l'ordinateur
					if ((this.babyTable[j] == EMPTY || this.babyTable[j] == player) 
							&& (this.babyTable[j+3] == EMPTY || this.babyTable[j+3] == player) 
							&& (this.babyTable[j+6] == EMPTY || this.babyTable[j+6] == player) ) {
						for (int k=j; k<j+9; k+=3) {
							if (this.babyTable[k] == player) {
								babyTableEval[i]++;
							}
						}
						
					}
					// Colonnes formées par l'adversaire
					if ((this.babyTable[j] == EMPTY || this.babyTable[j] == adversary) 
							&& (this.babyTable[j+3] == EMPTY || this.babyTable[j+3] == adversary) 
							&& (this.babyTable[j+6] == EMPTY || this.babyTable[j+6] == adversary) ) {
						for (int k=j; k<j+9; k+=3) {
							if (this.babyTable[k] == adversary) {
								babyTableEval[i]--;
							}
						}
					}
				}
				
				// On vérifie la formation de lignes
				for (int j=startIndex; j<startIndex+9; j+=3) {
					// Lignes formées par l'ordinateur
					if ((this.babyTable[j] == EMPTY || this.babyTable[j] == player) 
							&& (this.babyTable[j+1] == EMPTY || this.babyTable[j+1] == player) 
							&& (this.babyTable[j+2] == EMPTY || this.babyTable[j+2] == player) ) {
						for (int k=j; k<j+3; k++) {
							if (this.babyTable[k] == player) {
								babyTableEval[i]++;
							}
						}
					}
					// Lignes formées par l'adversaire
					if ((this.babyTable[j] == EMPTY || this.babyTable[j] == adversary) 
							&& (this.babyTable[j+1] == EMPTY || this.babyTable[j+1] == adversary) 
							&& (this.babyTable[j+2] == EMPTY || this.babyTable[j+2] == adversary) ) {
						for (int k=j; k<j+3; k++) {
							if (this.babyTable[k] == adversary) {
								babyTableEval[i]--;
							}
						}
					}
				}
				
				// On vérifie la formation de la diagonale par l'ordinateur
				if ((this.babyTable[0] == EMPTY || this.babyTable[0] == player) 
						&& (this.babyTable[4] == EMPTY || this.babyTable[4] == player) 
						&& (this.babyTable[8] == EMPTY || this.babyTable[8] == player) ) {
					for (int k=startIndex; k<startIndex+9; k+=4) {
						if (this.babyTable[k] == player) {
							babyTableEval[i]++;
						}
					}
				}
				
				// On vérifie la formation de la diagonale par l'adversaire
				if ((this.babyTable[0] == EMPTY || this.babyTable[0] == adversary) 
						&& (this.babyTable[4] == EMPTY || this.babyTable[4] == adversary) 
						&& (this.babyTable[8] == EMPTY || this.babyTable[8] == adversary) ) {
					for (int k=startIndex; k<startIndex+9; k+=4) {
						if (this.babyTable[k] == adversary) {
							babyTableEval[i]--;
						}
					}
				}
				
				// On vérifie la formation de l'anti diagonale par l'ordinateur
				if ((this.babyTable[2] == EMPTY || this.babyTable[2] == player) 
						&& (this.babyTable[4] == EMPTY || this.babyTable[4] == player) 
						&& (this.babyTable[6] == EMPTY || this.babyTable[6] == player) ) {
					for (int k=startIndex+2; k<startIndex+7; k+=2) {
						if (this.babyTable[k] == player) {
							babyTableEval[i]++;
						}
					}
				}
				
				// On vérifie la formation de l'anti diagonale par l'adversaire
				if ((this.babyTable[0] == EMPTY || this.babyTable[0] == adversary) 
						&& (this.babyTable[4] == EMPTY || this.babyTable[4] == adversary) 
						&& (this.babyTable[8] == EMPTY || this.babyTable[8] == adversary) ) {
					for (int k=startIndex+2; k<startIndex+7; k+=2) {
						if (this.babyTable[k] == adversary) {
							babyTableEval[i]--;
						}
					}
				}
			}
		}
		
		// Le résultat de l'évaluation du plateau.
		int res = 0;
		
		// On évalue la formation de grandes colonnes.
		for (int i=0; i<3; i++) {
			List<Byte> tmp = new ArrayList<Byte>();
			for (int j=0; j<9; j+=3) {
				tmp.add(daddyTable[i+j]);
			}
			
			if (tmp.contains(DRAW) || (tmp.contains(CIRCLE) && tmp.contains(CROSS))) {
				break;
			}
			
			for (int j=0; j<9; j+=3) {
				res += babyTableEval[i+j];
			}
		}
		
		// On évalue la formation de grandes lignes.
		for (int i=0; i<9; i+=3) {
			List<Byte> tmp = new ArrayList<Byte>();
			for (int j=0; j<3; j++) {
				tmp.add(daddyTable[i+j]);
			}
			
			if (tmp.contains(DRAW) || (tmp.contains(CIRCLE) && tmp.contains(CROSS))) {
				break;
			}
			
			for (int j=0; j<3; j++) {
				res += babyTableEval[i+j];
			}
		}
		
		// On évalue la formation de grandes diagonales.
		List<Byte> tmp = new ArrayList<Byte>();
		for (int j=0; j<9; j+=4) {
			tmp.add(daddyTable[j]);
		}
		if (!(tmp.contains(DRAW) || (tmp.contains(CIRCLE) && tmp.contains(CROSS)))) {
			for (int j=0; j<9; j+=4) {
				res += babyTableEval[j];
			}
		}
		
		// On évalue la formation de grandes anti diagonales.
		tmp.clear();
		for (int j=2; j<7; j+=2) {
			tmp.add(daddyTable[j]);
		}
		if (!(tmp.contains(DRAW) || (tmp.contains(CIRCLE) && tmp.contains(CROSS)))) {
			for (int j=2; j<7; j+=2) {
				res += babyTableEval[j];
			}
		}
		
		return res;
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
		clone.randomBabyNumbers = this.randomBabyNumbers;
		
		return clone;
	}

	private void computeRandoms() {
		Random rand = new Random();
		
		this.randomBabyNumbers = new long[2][81];
		this.randomDaddyNumbers = new long[3][9];
		for (int player = 0; player < 2; player++) {
			for (int babySquare = 0; babySquare < 81; babySquare++) {
				this.randomBabyNumbers[player][babySquare] = rand.nextLong();
			}
		}
		
		for (int player = 0; player < 3; player++) {
			for (int daddySquare = 0; daddySquare < 9; daddySquare++) {
				this.randomDaddyNumbers[player][daddySquare] = rand.nextLong();
			}
		}
	}
	
	public long getHash() {
		long hash = 0;
		
		for (int i = 0; i < babyTable.length; i++) {
			int daddySquare = i/9;
			if (daddyTable[daddySquare] != EMPTY) {
				hash ^= randomDaddyNumbers[daddyTable[daddySquare]-1][daddySquare];
				i += 9;
			}			
			else if (babyTable[i] != EMPTY) {
				hash = hash ^ randomBabyNumbers[babyTable[i]-1][i];
			}
		}
		
		return hash;
	}
	
	private void addHit() {
		long hash = this.getHash();
		int hits = 1;
		
		if (hitMap.containsKey(hash)) {
			hits = hitMap.get(hash) + 1;
		}
		
		//hitMap.put(hash, hits);
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
	
	public void displayHitMap() {
		for(Entry<Long, Integer> entry : hitMap.entrySet()) {
		    Long hash = entry.getKey();
		    Integer nbHits = entry.getValue();
		    
		    if (nbHits > 100000)
		    	System.out.println("Hash : " + hash + " ; Nb d'occurences : " + nbHits);
		}
	}
}