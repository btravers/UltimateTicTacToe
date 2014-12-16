package algorithms;

import game.Game;

import java.util.List;
import java.util.Stack;

public class AlphaBeta {
	
	private Game game;
	private Stack<Integer> bestMoves;
	
	public AlphaBeta(Game game) {
		this.game = game;
		this.bestMoves = new Stack<Integer>();
	}
	
	public int run() {
		return maxValue(Integer.MIN_VALUE, Integer.MAX_VALUE, this.bestMoves, 12);
	}

	private int maxValue(int alpha, int beta, Stack<Integer> actionList, int depth) {
		int result = this.game.isEndOfGame();
		if (result != 0) {
			return this.game.getScore(result);
		}
		
		if (depth == 0) {
			return this.game.eval();
		}
		
		int v = Integer.MIN_VALUE;
		List<Integer> successors = this.game.getSuccessors();
		
		for (Integer move : successors) {
			Stack<Integer> tmp = new Stack<Integer>();
			
			this.game.play(move);
			int vbis = minValue(alpha, beta, tmp, depth-1);
			this.game.unplay();
			
			if (vbis > v) {
				v = vbis;
				actionList.clear();
				actionList.addAll(tmp);
				actionList.push(move);
			}
			
			if (v >= beta) {
				return v;
			}
			
			alpha = Math.max(alpha, v);
		}
		
		return v;
	}
	
	private int minValue(int alpha, int beta, Stack<Integer> actionList, int depth) {
		
		int result = game.isEndOfGame();
		if (result != 0) {
			return game.getScore(result);
		}
		
		if (depth == 0) {
			return this.game.eval();
		}
		
		int v = Integer.MAX_VALUE;
		List<Integer> successors = game.getSuccessors();
		
		for (Integer move : successors) {
			Stack<Integer> tmp = new Stack<Integer>();
			
			game.play(move);
			int vbis = maxValue(alpha, beta, tmp, depth-1);
			game.unplay();
			
			if (vbis < v) {
				v = vbis;
				actionList.clear();
				actionList.addAll(tmp);
				actionList.push(move);
			}
			
			if (v <= alpha) {
				return v;
			}
			
			beta = Math.min(beta, v);
		}
		
		return v;
	}
	
	public static void main(String[] args) {
		AlphaBeta ab = new AlphaBeta(new Game());
		
		System.out.println(ab.run());
		
		String moves = "";
		for (Integer move : ab.bestMoves) {
			moves += " " + move;
			ab.game.play(move);
		}
		System.out.println(moves);
		
		System.out.println(ab.game.toString());
	}
}
