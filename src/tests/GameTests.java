package tests;

import static org.junit.Assert.*;
import game.Game;

import org.junit.*;

public class GameTests {

	protected Game game;
	
	@Before
	public void setUp() {
		game = new Game();
	}
	
	@Test
	public void testGetCurrentPlay() {
		assertEquals(game.getCurrentPlayer(), Game.CROSS);
	}
	
	@Test
	public void testPlay() {
		game.play(0);
		assertEquals(game.getCurrentPlayer(), Game.CIRCLE);
	}
	
	@Test
	public void testUnplay() {
		game.play(0);
		game.unplay(0);
		assertEquals(game.getCurrentPlayer(), Game.CROSS);
	}
}
