package tests;

import game.Game;
import junit.framework.TestCase;

public class GameTests extends TestCase {

	protected Game game;
	
	protected void setUp() {
		game = new Game();
	}
	
	public void testGetCurrentPlay() {
		assertEquals(game.getCurrentPlayer(), Game.CROSS);
	}
	
	public void testPlay() {
		game.play(0);
		assertEquals(game.getCurrentPlayer(), Game.CIRCLE);
	}
	
	public void testUnplay() {
		game.play(0);
		game.unplay(0);
		assertEquals(game.getCurrentPlayer(), Game.CROSS);
	}
}
