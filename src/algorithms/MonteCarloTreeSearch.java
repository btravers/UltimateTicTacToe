package algorithms;

import java.util.ArrayList;
import java.util.List;

import game.Game;

public class MonteCarloTreeSearch {
	
	class Node {
		Node parent;
		List<Node> children;
		int move;
		int player;
		int w;
		int n;

		public Node(Node parent, int m, int player) {
			this.parent = parent;
			this.children = new ArrayList<Node>();
			this.move = m;
			this.player = player;
			this.w = 0;
			this.n = 0;
		}
	}
	
	private Game game;
	private Node tree;
	private int t;
	
	public MonteCarloTreeSearch(Game g) {
		this.game = g;
		this.t = 0;
		
		int p = this.game.getCurrentPlayer() == Game.CROSS ? Game.CIRCLE : Game.CROSS;
		
		this.tree = new Node(null, -1, p);
		this.expandNode(this.tree, this.game);
	}
	
	public void expandNode(Node n, Game g) {		
		int p = n.player == Game.CROSS ? Game.CIRCLE : Game.CROSS;
		
		for (int s : g.getSuccessors()) {
			n.children.add(new Node(n, s, p));
		}
	}
	
	public void visitNode(Node n, Game g) {
		if (n.children.isEmpty()) {
			if (n.n == 10) {
				this.expandNode(n, g);
			} else {
				int winner = g.playOut();
				int factor = -1;
				if (winner == n.player) {
					factor = 1;
				}
				if (winner == Game.DRAW) {
					factor = 0;
				}
				
				Node tmp = n;
				while (tmp.move != -1) {
					tmp.w += factor;
					factor *= -1;
					tmp.n++;
					tmp = tmp.parent;
				}
				this.t++;
			}
		} else {
			double bestScore = n.children.get(0).n == 0 ? Integer.MAX_VALUE : ((double)n.children.get(0).w)/((double)n.children.get(0).n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/n.children.get(0).n);
			List<Node> successors = new ArrayList<Node>();
			successors.add(n.children.get(0));
			
			for (Node c : n.children) {
				double tmp = c.n == 0 ? Integer.MAX_VALUE : ((double)c.w)/((double)c.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/c.n);
				if (tmp == bestScore) {
					successors.add(c);
				}
				if (tmp > bestScore) {
					successors.clear();
					successors.add(c);
					bestScore = tmp;
				}
			}
			
			Node successor;
			if (successors.size() == 1) {
				successor = successors.get(1);
			} else {
				int rg = (int)(Math.random()*successors.size());
				successor = successors.get(rg);
			}
			
			g.play(successor.move);
			this.visitNode(successor, g);
		}
	}
	
	public int run() {
		long firstTime = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - firstTime < 3000) {
			
			Game clone = this.game.clone();
			this.visitNode(this.tree, clone);
		
		}
		double bestScore = this.tree.children.get(0).n == 0 ? Integer.MAX_VALUE : ((double)this.tree.children.get(0).w)/((double)this.tree.children.get(0).n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/this.tree.children.get(0).n);
		List<Node> successors = new ArrayList<Node>();
		successors.add(this.tree.children.get(0));
		
		for (Node c : this.tree.children) {
			double tmp = c.n == 0 ? Integer.MAX_VALUE : ((double)c.w)/((double)c.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/c.n);
			if (tmp == bestScore) {
				successors.add(c);
			}
			if (tmp > bestScore) {
				successors.clear();
				successors.add(c);
				bestScore = tmp;
			}
		}
		
		Node successor;
		if (successors.size() == 1) {
			successor = successors.get(1);
		} else {
			int rg = (int)(Math.random()*successors.size());
			successor = successors.get(rg);
		}

		return successor.move;
	}
	
}
