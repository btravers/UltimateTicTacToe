package algorithms;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import game.Game;

public class MonteCarlo {

	class Node {
		Game game;
		int move;
		int w;
		int n;

		public Node(Game g, int move) {
			this.game = g;
			this.move = move;
			this.w = 0;
			this.n = 0;
		}
	}

	private Game game;

	private int t;

	private List<Node> successors;

	public MonteCarlo(Game g) {
		this.game = g;
		this.t = 0;
		this.successors = new ArrayList<Node>();
	}

	public int run() {
		for (int m : this.game.getSuccessors()) {
			this.successors.add(new Node(this.game, m));
		}

		Node focus;
		double bestScore;
		long firstTime = System.currentTimeMillis();

		while (System.currentTimeMillis() - firstTime < 2000) {
			focus = this.successors.get(0);
			bestScore = focus.n == 0 ? 1 : ((double)focus.w)/((double)focus.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/focus.n);

			for (Node n : this.successors) {
				double tmp = n.n == 0 ? 1 : ((double)n.w)/((double)n.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/n.n);
				if (tmp > bestScore) {
					focus = n;
					bestScore = tmp;
				}
			}

			Game clone = focus.game.clone();
			clone.play(focus.move);
			int winner = clone.playOut();
			
			if (winner == this.game.getCurrentPlayer()) {
				focus.w++;
			}
			focus.n++;
			this.t++;
		}

		focus = this.successors.get(0);
		bestScore = focus.n == 0 ? -1 : ((double)focus.w)/((double)focus.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/focus.n);

		for (Node n : this.successors) {
			double tmp = ((double)n.w)/((double)n.n) + Math.sqrt(2)*Math.sqrt(Math.log(this.t)/n.n);
			if (tmp > bestScore) {
				focus = n;
				bestScore = tmp;
			}
		}
		
		System.out.println(System.currentTimeMillis() - firstTime);

		return focus.move;
	}

	public static void main(String[] args) {
		

		Scanner scan = new Scanner(System.in);

		Game g = new Game();
		
		int tour = 0;

		while (g.isEndOfGame() == 0) {
			if (tour%2 == 0) {
				MonteCarlo mc = new MonteCarlo(g);
				g.play(mc.run());
				System.out.println(g.toString());
			} else {
				System.out.println("Move: ");
				int move = scan.nextInt();
				g.play(move);
				System.out.println(g.toString());
			}
			tour++;
		}
		
		scan.close();
	}
}
