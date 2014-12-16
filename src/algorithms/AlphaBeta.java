package algorithms;

import game.Game;

import java.util.List;
import java.util.Stack;

public class AlphaBeta {
	
	private Game game;
	private Stack<Integer> playedMoves;
	
	public AlphaBeta(Game game) {
		this.game = game;
		this.playedMoves = new Stack<Integer>();
	}
	
	public int run() {
		return maxValue(Integer.MIN_VALUE, Integer.MAX_VALUE, this.playedMoves);
	}

	private int maxValue(int alpha, int beta, Stack<Integer> playedMoves) {
		int lastMove = playedMoves.isEmpty() ? -1 : playedMoves.peek();
		
		int result = game.isEndOfGame(lastMove);
		if (result != 0) {
			return game.getScore(result);
		}
		
		int v = Integer.MIN_VALUE;
		List<Integer> successors = game.getSuccessors(lastMove);
		
		for (Integer move : successors) {
			Stack<Integer> tmp = new Stack<Integer>();
			int vbis = minValue(alpha, beta, tmp);
			
			if (vbis > v) {
				v = vbis;
				playedMoves = tmp;
				playedMoves.push(move);
			}
			
			if (v >= beta) {
				return v;
			}
			
			alpha = Math.max(alpha, v);
		}
		
		return v;
	}
	
	private int minValue(int alpha, int beta, Stack<Integer> playedMoves) {
		int lastMove = playedMoves.isEmpty() ? -1 : playedMoves.peek();
		
		int result = game.isEndOfGame(lastMove);
		if (result != 0) {
			return game.getScore(result);
		}
		
		int v = Integer.MAX_VALUE;
		List<Integer> successors = game.getSuccessors(lastMove);
		
		for (Integer move : successors) {
			Stack<Integer> tmp = new Stack<Integer>();
			int vbis = maxValue(alpha, beta, tmp);
			
			if (vbis < v) {
				v = vbis;
				playedMoves = tmp;
				playedMoves.push(move);
			}
			
			if (v <= alpha) {
				return v;
			}
			
			beta = Math.min(beta, v);
		}
		
		return v;
	}
	
}
