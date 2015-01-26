package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import game.Game;

public class MonteCarloTreeSearch extends Algorithm {

	class Node {
		Node parent;
		List<Node> children;
		int move;
		int player;
		int s; // score of the node
		int n; // number of times visited
		int depth;

		public Node(Node parent, int m, int player, int depth) {
			this.parent = parent;
			this.children = new ArrayList<Node>();
			this.move = m;
			this.player = player;
			this.depth = depth;
			this.s = 0;
			this.n = 0;
		}

		public boolean isRoot() {
			return this.move == -1;
		}

		public boolean isLeaf() {
			return this.children.isEmpty();
		}

		public void expand(Game g) {    
			int p = this.player == Game.CROSS ? Game.CIRCLE : Game.CROSS;

			for (int s : g.getSuccessors()) {
				this.children.add(new Node(this, s, p, this.depth+1));
			}
		}

		public void visit(Game g) {
			if (this.isLeaf()) {
				if (this.n == EXPLORATION_RATIO) {
					this.expand(g);
				}

				if (this.n >= EXPLORATION_RATIO && this.isLeaf()) {
					int factor = g.isEndOfGame() == Game.DRAW ? 0 : -1;
					this.update(factor);
				} 

				if (this.n < EXPLORATION_RATIO) {
					int result = g.playOut();
					int factor = -1;
					if (result == this.player) {
						factor = 1;
					}
					if (result == Game.DRAW) {
						factor = 0;
					}

					this.update(factor);
				}
			} else {
				Node successor = this.select();

				g.play(successor.move);
				successor.visit(g);
			}
		}

		private Node select() {
			double bestScore = Double.NEGATIVE_INFINITY;
			List<Node> successors = new ArrayList<Node>();

			for (Node c : this.children) {
				double tmp = c.n == 0 ? Double.MAX_VALUE : ((double)c.s)/((double)c.n) + Math.sqrt(EXPLORATION_RATIO)*Math.sqrt(Math.log(this.n)/c.n);
				if (tmp > bestScore) {
					successors.clear();
					successors.add(c);
					bestScore = tmp;
				} else if (tmp == bestScore) {
					successors.add(c);
				}
			}

			return this.pickSuccessor(successors);
		}

		private Node pickSuccessor(List<Node> successors) {
			int rg = r.nextInt(successors.size()); 
			return successors.get(rg);
		}

		private void update(int factor) {
			this.s += factor;
			this.n++;
			if (!this.isRoot()) {
				this.parent.update(-factor);
			}
		}
	}

	private Node tree;

	private static final int EXPLORATION_RATIO = 2;
	private static Random r = new Random();

	public MonteCarloTreeSearch(Game g) {
		System.out.println("MonteCarloTreeSearch");

		this.game = g;

		int p = this.game.getCurrentPlayer();

		this.tree = new Node(null, -1, p, 0);
		
	}

	public int run(int timeout) {
		long firstTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - firstTime < timeout) {

			Game clone = this.game.clone();
			this.tree.visit(clone);

		}

		System.out.println("Temps d'exécution de MonteCarloTreeSearch : " + (System.currentTimeMillis() - firstTime));

		Node successor = this.tree.select();

		System.out.println("Nombre de playout execute : " + this.tree.n);
		
		System.out.println("Coup retenu : " + successor.move);

		return successor.move;
	}
}
