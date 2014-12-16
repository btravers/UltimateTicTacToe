package algorithms;

import game.Game;

import java.util.List;
import java.util.Stack;

public class AlphaBeta {
	
	private Game game;
	private Stack<Integer> playedMoves;
	private Stack<Integer> bestMoves;
	
	public AlphaBeta(Game game) {
		this.game = game;
		this.playedMoves = new Stack<Integer>();
	}
	
	public int run() {
		return maxValue(Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	private int maxValue(int alpha, int beta) {
		int lastMove = this.playedMoves.isEmpty() ? -1 : this.playedMoves.peek();
		
		int result = game.isEndOfGame(lastMove);
		if (result != 0) {
			return game.getScore(result);
		}
		
		int v = Integer.MIN_VALUE;
		List<Integer> successors = game.getSuccessors(lastMove);
		
		for (Integer move : successors) {			
			game.play(move);
			this.playedMoves.add(move);
			int vbis = minValue(alpha, beta);
			this.playedMoves.remove(move);
			game.unplay(move);
			
			if (vbis > v) {
				v = vbis;
				this.bestMoves.push(move);
			}
			
			if (v >= beta) {
				return v;
			}
			
			alpha = Math.max(alpha, v);
		}
		
		return v;
	}
	
	private int minValue(int alpha, int beta) {
		int lastMove = this.playedMoves.isEmpty() ? -1 : this.playedMoves.peek();
		
		int result = game.isEndOfGame(lastMove);
		if (result != 0) {
			return game.getScore(result);
		}
		
		int v = Integer.MAX_VALUE;
		List<Integer> successors = game.getSuccessors(lastMove);
		
		for (Integer move : successors) {			
			game.play(move);
			this.playedMoves.add(move);
			int vbis = maxValue(alpha, beta);
			this.playedMoves.remove(move);
			game.unplay(move);
			
			if (vbis < v) {
				v = vbis;
				this.bestMoves.push(move);
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
		
		System.out.println(ab.game.toString());
	}
}
