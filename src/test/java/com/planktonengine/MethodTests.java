package com.planktonengine;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;

import org.junit.Test;

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

	@Test
	public void testMoveGen(){
		game.blankGame();
		game.createPiece(0, 0, new int[]{0, 1});
		game.castleAvailable=new boolean[]{false, false, false, false};
		game.setMoves();
		ArrayList<Integer> rightMoves=new ArrayList<>();
		rightMoves.add(16);
		rightMoves.add(24);
		System.out.println(game.pieceMoves[8].getMoves());
		boolean equals=game.pieceMoves[8].getMoves().containsAll(rightMoves)
				&& rightMoves.containsAll(game.pieceMoves[8].getMoves());
		assertEquals(true, equals);
	}

}
