package main;

import game.Game;

import java.util.List;
import java.util.Scanner;

import algorithms.AlphaBeta;
import algorithms.MonteCarloTreeSearch;

public class Main {
	
	static int TIMEOUT = 3000;

	public static void main(String[] args) {
		long startTime = System.currentTimeMillis();
		
		Scanner scan = new Scanner(System.in);

		Game g = new Game();
				
		int tour = 0;

		while (g.isEndOfGame() == 0) {
			if (tour%2 == 0) {
				AlphaBeta ab = new AlphaBeta(g);
				g.play(ab.run(TIMEOUT));
				System.out.println(g.toString());
			} else {
				MonteCarloTreeSearch mc = new MonteCarloTreeSearch(g);
				g.play(mc.run(TIMEOUT));
				System.out.println(g.toString());
				int move;
				/*List<Integer> successors = g.getSuccessors();
				do {
					System.out.println("Entrez deux entiers entre 1 et 9 (grand carre, petit carre): ");
					int bigSquare = scan.nextInt()-1;
					int smallSquare = scan.nextInt()-1;
					move = bigSquare * 9 + smallSquare;
				} while (!successors.contains(move));
				g.play(move);
				System.out.println(g.toString());*/
			}
			tour++;
		}
		
		scan.close();
		
		System.out.println("Temps total :" + (System.currentTimeMillis()-startTime)/1000 + "s");
		
		g.displayHitMap();
				
	}

}
