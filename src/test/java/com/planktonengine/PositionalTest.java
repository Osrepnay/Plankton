package com.planktonengine;

import org.junit.Assert;
import org.junit.Test;

public class PositionalTest{

	private PlanktonEngine engine=new PlanktonEngine();
	private Game game=new Game();

	@Test
	public void testCheckmates(){
		//basic rook back-rank
		game.blankGame();
		game.createPiece(1, 5, new int[]{0, 0});
		game.createPiece(0, 5, new int[]{0, 2});
		game.createPiece(0, 3, new int[]{7, 1});
		game.castleAvailable=new boolean[4];
		game.setMoves();
		double[] bestMove=engine.bestMove(game, 0, 1);
		Assert.assertArrayEquals(new double[]{15, 7, 10000}, bestMove, 0);
	}

	@Test
	public void testMaterialGain(){
		//queen fork
		game.blankGame();
		game.createPiece(1, 5, new int[]{0, 0});
		game.createPiece(1, 4, new int[]{0, 2});
		game.createPiece(0, 5, new int[]{7, 7});
		game.createPiece(0, 1, new int[]{4, 0});
		game.castleAvailable=new boolean[4];
		game.setMoves();
		double[] bestMove=engine.bestMove(game, 0, 3);
		Assert.assertArrayEquals(new double[]{4, 10, 3}, bestMove, 0);
	}

}
