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
		game.unplay();
		assertEquals(game.getCurrentPlayer(), Game.CROSS);
	}
	
	@Test
	public void testClone() {
		game.play(0);
		
		Game tmp = game.clone();
		
		assertEquals(tmp.getCurrentPlayer(), Game.CIRCLE);
		assertEquals(tmp.toString(), game.toString());
		
		game.play(1);
		assertNotEquals(tmp.toString(), game.toString());
	}
	
	@Test
	public void testPLayOut() {
		int res = game.playOut();
		
		assertNotEquals(res, 0);
		assertEquals(res, game.isEndOfGame());
	}
}
