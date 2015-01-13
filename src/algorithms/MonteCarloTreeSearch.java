package algorithms;

import java.util.ArrayList;
import java.util.List;

import game.Game;

public class MonteCarloTreeSearch {
	
	class Node {
		Node parent;
		int move;
		int player;
		int w;
		int n;

		public Node(Node parent, int m, int player) {
			this.parent = parent;
			this.move = m;
			this.player = player;
			this.w = 0;
			this.n = 0;
		}
	}
	
	private Game game;
	private int t;
	private List<Node> successors;
	
	public MonteCarloTreeSearch(Game g) {
		this.game = g;
		this.t = 0;
		this.successors = new ArrayList<Node>();
	}
	
	public void expandNode(Node n) {
		int depth = 0;
		Node tmp = n;
		while (tmp.move != -1) {
			this.game.play(tmp.move);
			tmp = tmp.parent;
			depth++;
		}
		
		int p = n.player == Game.CROSS ? Game.CIRCLE : Game.CROSS;
		
		for (int s : this.game.getSuccessors()) {
			this.successors.add(new Node(n, s, p));
		}
		
		for (int i=0; i<depth; i++) {
			this.game.unplay();
		}
	}
	
	public void run() {
		int p = this.game.getCurrentPlayer() == Game.CROSS ? Game.CIRCLE : Game.CROSS;
		
		this.expandNode(new Node(null, -1, p));
		
		long firstTime = System.currentTimeMillis();
		
		Node focus;
		double bestScore;
		
		while (System.currentTimeMillis() - firstTime < 3000) {
			focus = this.successors.get(0);
			bestScore = focus.n == 0 ? Integer.MAX_VALUE : ((double)focus.w)/((double)focus.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/focus.n);
			
			for (Node n : this.successors) {
				double tmp = n.n == 0 ? Integer.MAX_VALUE : ((double)n.w)/((double)n.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/n.n);
				if (tmp > bestScore) {
					focus = n;
					bestScore = tmp;
				}
			}
			
			if (focus.n == 10) {
				this.expandNode(focus);
				this.successors.remove(focus);
			} else {
				Game clone = this.game.clone();
				Node tmp = focus;
				while (tmp.move != -1) {
					clone.play(tmp.move);
					tmp = tmp.parent;
				}
				
				int winner = clone.playOut();
				int factor = -1;
				if (winner == focus.player) {
					factor = 1;
				}
				if (winner == Game.DRAW) {
					factor = 0;
				}
				
				tmp = focus;
				while (tmp.move != -1) {
					tmp.w += factor;
					factor *= -1;
					tmp.n++;
					tmp = tmp.parent;
				}
				this.t++;
			}
		}
	}
	
}
