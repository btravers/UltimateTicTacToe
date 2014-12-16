package algorithms;

import game.Game;

import java.util.ArrayList;
import java.util.List;

public class AlphaBeta {
	
	private Game game;
	private List<Integer> playedMoves;
	
	public AlphaBeta(Game game) {
		this.game = game;
		this.playedMoves = new ArrayList<Integer>();
	}
	
	public int run() {
		return maxValue(Integer.MIN_VALUE, Integer.MAX_VALUE, this.playedMoves);
	}

	private int maxValue(int alpha, int beta, List<Integer> playedMoves) {
		int lastMove = playedMoves.isEmpty() ? -1 : playedMoves.get(playedMoves.size()-1);
		
		if (game.isEndOfGame(lastMove) != 0) {
			return game.getScore();
		}
		
		int v = Integer.MIN_VALUE;
		List<Integer> successors = game.getSuccessors(lastMove);
		
		for (Integer move : successors) {
			List<Integer> tmp = new ArrayList<Integer>();
			int vbis = minValue(alpha, beta, tmp);
			
			if (vbis > v) {
				v = vbis;
				playedMoves = tmp;
				playedMoves.add(move);
			}
			
			if (v >= beta) {
				return v;
			}
			
			alpha = Math.max(alpha, v);
		}
		
		return v;
	}
	
	private int minValue(int alpha, int beta, List<Integer> playedMoves) {
		int lastMove = playedMoves.isEmpty() ? -1 : playedMoves.get(playedMoves.size()-1);
		
		if (game.isEndOfGame(lastMove) != 0) {
			return game.getScore();
		}
		
		int v = Integer.MAX_VALUE;
		List<Integer> successors = game.getSuccessors(lastMove);
		
		for (Integer move : successors) {
			List<Integer> tmp = new ArrayList<Integer>();
			int vbis = maxValue(alpha, beta, tmp);
			
			if (vbis < v) {
				v = vbis;
				playedMoves = tmp;
				playedMoves.add(move);
			}
			
			if (v <= alpha) {
				return v;
			}
			
			beta = Math.min(beta, v);
		}
		
		return v;
	}
	
}
