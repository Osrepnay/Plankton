package com.planktonengine;

public class BitboardUtility{

	private BitboardUtility(){
		throw new IllegalStateException("Utility class cannot be instantiated.");
	}

	public static int scanDown(Bitboard board){
		return 63-Long.numberOfLeadingZeros(board.getBitboard());
	}

	public static int scanUp(Bitboard board){
		return Long.numberOfTrailingZeros(board.getBitboard());
	}

	public static PieceMoves bitboardToPieceMoves(Bitboard board){
		PieceMoves pieceMoves=new PieceMoves();
		for(int i=0; i<64; i++){
			if(board.getSquare(i)){
				pieceMoves.addMove(i);
			}
		}
		return pieceMoves;
	}

	public static Bitboard pieceMovesToBitboard(PieceMoves pieceMoves){
		Bitboard board=new Bitboard();
		for(int square : pieceMoves.getMoves()){
			board.setSquare(square, true);
		}
		return board;
	}

	public static void printBoard(Bitboard board){
		for(int i=7; i>=0; i--){
			for(int j=0; j<8; j++){
				System.out.print(board.getSquare(j, i) ? "0 " : "- ");
			}
			System.out.println();
		}
	}

}
