package main;

import game.Game;

import java.util.Scanner;

import algorithms.AlphaBeta;
import algorithms.MonteCarloTreeSearch;

public class Main {

	public static void main(String[] args) {
			
		Scanner scan = new Scanner(System.in);

		Game g = new Game();
				
		int tour = 0;

		while (g.isEndOfGame() == 0) {
			if (tour%2 == 0) {
				AlphaBeta ab = new AlphaBeta(g, Game.CROSS);
				g.play(ab.run());
				System.out.println(g.toString());
			} else {
				MonteCarloTreeSearch ab = new MonteCarloTreeSearch(g);
				g.play(ab.run());
				System.out.println(g.toString());
			}
			tour++;
		}
		
		scan.close();
				
	}

}
