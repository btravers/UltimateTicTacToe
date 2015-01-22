package algorithms;

import java.util.ArrayList;
import java.util.List;

import game.Game;

public class MonteCarloTreeSearch extends Algorithm{
	
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
	
	private Node tree;
	
	public MonteCarloTreeSearch(Game g) {
		System.out.println("MonteCarloTreeSearch");
		
		this.game = g;
		
		int p = this.game.getCurrentPlayer();
		
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
			if (n.n == 2) {
				this.expandNode(n, g);
			}
			
			if (n.n >= 2 && n.children.isEmpty()) {
				int winner = n.player == Game.CROSS ? Game.CIRCLE : Game.CROSS;
				int score = g.getScore(this.tree.player, winner);
				
				int factor = -1;
				if (score > 0) {
					factor = 1;
				}
				if (score == 0) {
					factor = 0;
				}
				
				Node tmp = n;
				while (tmp.move != -1) {
					tmp.w += factor;
					factor *= -1;
					tmp.n++;
					tmp = tmp.parent;
				}
				this.tree.w += factor;
				this.tree.n++;
			} 
			
			if (n.n < 2) {
				int winner = g.playOut();
				int factor = -1;
				if (winner == this.tree.player) {
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
				this.tree.w += factor;
				this.tree.n++;
			}
		} else {
			double bestScore = n.children.get(0).n == 0 ? Integer.MAX_VALUE : ((double)n.children.get(0).w)/((double)n.children.get(0).n) + Math.sqrt(2)*Math.sqrt(Math.log(n.n)/n.children.get(0).n);
			List<Node> successors = new ArrayList<Node>();
			successors.add(n.children.get(0));
			
			for (Node c : n.children) {
				double tmp = c.n == 0 ? Integer.MAX_VALUE : ((double)c.w)/((double)c.n) + Math.sqrt(2)*Math.sqrt(Math.log(n.n)/c.n);
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
				successor = successors.get(0);
			} else {
				int rg = (int)(Math.random()*successors.size());
				successor = successors.get(rg);
			}
			
			g.play(successor.move);
			this.visitNode(successor, g);
		}
	}
	
	public int run(int timeout) {
		long firstTime = System.currentTimeMillis();
		
		while (System.currentTimeMillis() - firstTime < timeout) {
			
			Game clone = this.game.clone();
			this.visitNode(this.tree, clone);
		
		}
		
		System.out.println("Temps d'exécution de MonteCarloTreeSearch : " + (System.currentTimeMillis() - firstTime));
		double bestScore = ((double)this.tree.children.get(0).w)/((double)this.tree.children.get(0).n) + Math.sqrt(2)*Math.sqrt(Math.log(this.tree.n)/this.tree.children.get(0).n);
		List<Node> successors = new ArrayList<Node>();
		successors.add(this.tree.children.get(0));
		
		for (Node c : this.tree.children) {
			double tmp = ((double)c.w)/((double)c.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.tree.n)/c.n);
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
			successor = successors.get(0);
		} else {
			int rg = (int)(Math.random()*successors.size());
			successor = successors.get(rg);
		}
		
		System.out.println("Nombre de playout exécuté : " + this.tree.n);

		return successor.move;
	}
	
}
