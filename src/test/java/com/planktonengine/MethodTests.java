package com.planktonengine;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class MethodTests {

	private PlanktonEngine engine = new PlanktonEngine();
	private Game game = new Game();

	@Test
	public void testCheckMethods() {
		game.blankGame();
		game.createPiece(1, 5, new int[] {0, 0});
		game.createPiece(0, 5, new int[] {0, 2});
		game.createPiece(0, 3, new int[] {7, 0});
		game.setMoves();
		assertEquals(true, engine.inCheck(game, 1));
		assertEquals(true, engine.inCheckmate(game, 1));
	}

	//TODO add move tests for all pieces
	@Test
	public void testMoveGen() {
		game.blankGame();
		game.createPiece(0, 0, new int[] {0, 1});
		game.setMoves();
		List<PieceMove> rightMoves = new ArrayList<>();
		rightMoves.add(new PieceMove(8, 16, SpecialMove.NONE));
		rightMoves.add(new PieceMove(8, 24, SpecialMove.NONE));
		boolean equals = game.pieceMovesFromSquare(8).containsAll(rightMoves)
				&& rightMoves.containsAll(game.pieceMovesFromSquare(8));
		assertEquals(true, equals);
	}

	@Test
	public void testSEE() {
		game.blankGame();
		game.createPiece(0, 5, new int[] {0, 0});
		game.createPiece(1, 5, new int[] {2, 0});
		game.createPiece(1, 0, new int[] {7, 6});
		game.createPiece(0, 3, new int[] {6, 5});
		game.createPiece(0, 0, new int[] {5, 4});
		game.setMoves();
		assertEquals(-4, engine.see(game, new PieceMove(55, 46, SpecialMove.NONE)), 0.001);
	}

}
