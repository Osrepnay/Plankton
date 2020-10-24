package com.planktonengine;

import java.util.Arrays;

/**
 * Stores some stuff that {@link Game#unMakeMove} uses to restore the previous position
 */
public class PrevMoveGameState{

	private int capturePiece;
	private boolean[] castleAvailable;
	private int promotion;

	public PrevMoveGameState(int capturePiece, boolean[] castleAvailable, int promotion){
		this.capturePiece=capturePiece;
		this.castleAvailable=Arrays.copyOf(castleAvailable, castleAvailable.length);
		this.promotion=promotion;
	}

	public int getCapturePiece(){
		return capturePiece;
	}

	public boolean[] getCastleAvailable(){
		return castleAvailable;
	}

	public int getPromotion(){
		return promotion;
	}
}
