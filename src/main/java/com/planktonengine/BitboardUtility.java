package com.planktonengine;

import java.util.ArrayList;
import java.util.List;

public class BitboardUtility {

	private BitboardUtility() {
		throw new IllegalStateException("Utility class cannot be instantiated.");
	}

	public static int scanDown(Bitboard board) {
		return 63 - Long.numberOfLeadingZeros(board.getBitboard());
	}

	public static int scanUp(Bitboard board) {
		return Long.numberOfTrailingZeros(board.getBitboard());
	}

	public static List<PieceMove> bitboardToPieceMoves(int start, Bitboard board) {
		List<PieceMove> pieceMoves = new ArrayList<>();
		for(int i = 0; i < 64; i++) {
			if(board.getSquare(i)) {
				pieceMoves.add(new PieceMove(start, i, SpecialMove.NONE));
			}
		}
		return pieceMoves;
	}

	public static Bitboard pieceMovesToBitboard(List<PieceMove> pieceMoves) {
		Bitboard board = new Bitboard();
		for(PieceMove square : pieceMoves) {
			board.setSquare(square.end, true);
		}
		return board;
	}

}
