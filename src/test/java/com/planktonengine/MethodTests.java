package com.planktonengine;

import org.junit.Test;

import java.util.ArrayList;

import static org.junit.Assert.assertEquals;

public class MethodTests{

	private PlanktonEngine engine=new PlanktonEngine();
	private Game game=new Game();

	@Test
	public void testCheckMethods(){
		game.blankGame();
		game.createPiece(1, 5, new int[]{0, 0});
		game.createPiece(0, 5, new int[]{0, 2});
		game.createPiece(0, 3, new int[]{7, 0});
		game.castleAvailable=new boolean[4];
		game.setMoves();
		assertEquals(true, engine.inCheck(game, 1));
		assertEquals(true, engine.inCheckmate(game, 1));
	}

	//TODO add move tests for all pieces
	@Test
	public void testMoveGen(){
		game.blankGame();
		game.createPiece(0, 0, new int[]{0, 1});
		game.castleAvailable=new boolean[]{false, false, false, false};
		game.setMoves();
		ArrayList<Integer> rightMoves=new ArrayList<>();
		rightMoves.add(16);
		rightMoves.add(24);
		boolean equals=game.pieceMoves[8].getMoves().containsAll(rightMoves)
				&& rightMoves.containsAll(game.pieceMoves[8].getMoves());
		assertEquals(true, equals);
	}

	@Test
	public void testSEE(){
		game.blankGame();
		game.createPiece(0, 5, new int[]{0, 0});
		game.createPiece(1, 5, new int[]{2, 0});
		game.createPiece(1, 0, new int[]{7, 6});
		game.createPiece(0, 3, new int[]{6, 5});
		game.createPiece(0, 0, new int[]{5, 4});
		game.castleAvailable=new boolean[4];
		game.setMoves();
		assertEquals(-4, engine.see(game, new int[]{55, 46}, false), 0.001);
	}

}
