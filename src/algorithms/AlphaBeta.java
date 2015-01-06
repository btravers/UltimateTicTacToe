package algorithms;

import game.Game;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class AlphaBeta {
	
	private Game game;
	private List<Integer> bestMoves;
	private int truc;
	
	public AlphaBeta(Game game, int truc) {
		this.game = game;
		this.bestMoves = new ArrayList<Integer>();
		this.truc = truc;
	}
	
	public int run() {
		long firstTime = System.currentTimeMillis();
		int depth = 1;
		while (System.currentTimeMillis()-firstTime < 3000) {
			maxValue(Integer.MIN_VALUE, Integer.MAX_VALUE, this.bestMoves, depth++);
		}
		System.out.println(depth);
		return this.bestMoves.get(this.bestMoves.size()-1);
	}

	private int maxValue(int alpha, int beta, List<Integer> actionList, int depth) {
		int result = this.game.isEndOfGame();
		if (result != 0) {
			return this.game.getScore(result);
		}
		
		if (depth == 0) {
			return this.game.eval(this.truc);
		}
		
		int v = Integer.MIN_VALUE;
		List<Integer> successors = this.game.getSuccessors();
		
		for (Integer move : successors) {
			List<Integer> tmp = new ArrayList<Integer>();
			
			this.game.play(move);
			int vbis = minValue(alpha, beta, tmp, depth-1);
			this.game.unplay();
			
			if (vbis > v) {
				v = vbis;
				actionList.clear();
				actionList.addAll(tmp);
				actionList.add(move);
			}
			
			if (v >= beta) {
				return v;
			}
			
			alpha = Math.max(alpha, v);
		}
		
		return v;
	}
	
	private int minValue(int alpha, int beta, List<Integer> actionList, int depth) {
		
		int result = game.isEndOfGame();
		if (result != 0) {
			return game.getScore(result);
		}
		
		if (depth == 0) {
			return this.game.eval(this.truc);
		}
		
		int v = Integer.MAX_VALUE;
		List<Integer> successors = game.getSuccessors();
		
		for (Integer move : successors) {
			List<Integer> tmp = new ArrayList<Integer>();
			
			game.play(move);
			int vbis = maxValue(alpha, beta, tmp, depth-1);
			game.unplay();
			
			if (vbis < v) {
				v = vbis;
				actionList.clear();
				actionList.addAll(tmp);
				actionList.add(move);
			}
			
			if (v <= alpha) {
				return v;
			}
			
			beta = Math.min(beta, v);
		}
		
		return v;
	}
	
	public static void main(String[] args) {
		
		
		Scanner scan = new Scanner(System.in);

		Game g = new Game();
		int computer = Game.CROSS;
		
		
		int tour = 0;

		while (g.isEndOfGame() == 0) {
			if (tour%2 == 0) {
				AlphaBeta ab = new AlphaBeta(g, computer);
				g.play(ab.run());
				System.out.println(g.toString());
			} else {
				int move;
				List<Integer> successors = g.getSuccessors();
				do {
					System.out.println("Entrez deux entiers entre 1 et 9 (grand carré, petit carré): ");
					int bigSquare = scan.nextInt()-1;
					int smallSquare = scan.nextInt()-1;
					move = bigSquare * 9 + smallSquare;
				} while (!successors.contains(move));
				g.play(move);
				System.out.println(g.toString());
			}
			tour++;
		}
		
		scan.close();
				
	}
}
