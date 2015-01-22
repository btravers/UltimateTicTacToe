package algorithms;

import game.Game;

public abstract class Algorithm {

	protected Game game;
	
	public abstract int run(int timeout);
	
}
