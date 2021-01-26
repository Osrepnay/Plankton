package com.planktonengine;

import java.util.ArrayList;
import java.util.List;

public class MoveGen {

	Bitboard[][] rays = new Bitboard[64][8];
	Bitboard[] knightMoves = new Bitboard[64];
	Bitboard[] kingMoves = new Bitboard[64];

	public MoveGen() {
		for(int i = 0; i < rays.length; i++) {
			for(int j = 0; j < rays[i].length; j++) {
				rays[i][j] = new Bitboard();
			}
		}
		for(int i = 0; i < knightMoves.length; i++) {
			knightMoves[i] = new Bitboard();
		}
		for(int i = 0; i < kingMoves.length; i++) {
			kingMoves[i] = new Bitboard();
		}
		for(int i = 0; i < knightMoves.length; i++) {
			int[] possibleKnightMoves = new int[8];
			possibleKnightMoves[0] = i + 15;
			possibleKnightMoves[1] = i + 17;
			possibleKnightMoves[2] = i + 10;
			possibleKnightMoves[3] = i - 6;
			possibleKnightMoves[4] = i - 15;
			possibleKnightMoves[5] = i - 17;
			possibleKnightMoves[6] = i - 10;
			possibleKnightMoves[7] = i + 6;
			for(int j = 0; j < possibleKnightMoves.length; j++) {
				if(possibleKnightMoves[j] >= 0 && possibleKnightMoves[j] < 64) {
					if(!((i - 1) % 8 == 0 && (possibleKnightMoves[j] + 1) % 8 == 0) &&
							!(i % 8 == 0 && ((possibleKnightMoves[j] + 2) % 8 == 0 || (possibleKnightMoves[j] + 1) % 8 == 0)) &&
							!((i + 2) % 8 == 0 && possibleKnightMoves[j] % 8 == 0) &&
							!((i + 1) % 8 == 0 && ((possibleKnightMoves[j] - 1) % 8 == 0 || possibleKnightMoves[j] % 8 == 0))) {
						knightMoves[i].setSquare(possibleKnightMoves[j], true);
					}
				}
			}
		}
		for(int i = 0; i < kingMoves.length; i++) {
			int[] possibleKingMoves = new int[8];
			possibleKingMoves[0] = i + 8;
			possibleKingMoves[1] = i + 9;
			possibleKingMoves[2] = i + 1;
			possibleKingMoves[3] = i - 7;
			possibleKingMoves[4] = i - 8;
			possibleKingMoves[5] = i - 9;
			possibleKingMoves[6] = i - 1;
			possibleKingMoves[7] = i + 7;
			for(int j = 0; j < possibleKingMoves.length; j++) {
				if(possibleKingMoves[j] >= 0 && possibleKingMoves[j] < 64) {
					if(!(i % 8 == 0 && (possibleKingMoves[j] + 1) % 8 == 0) &&
							!((i + 1) % 8 == 0 && possibleKingMoves[j] % 8 == 0)) {
						kingMoves[i].setSquare(possibleKingMoves[j], true);
					}
				}
			}
		}
		for(int i = 0; i < rays.length; i++) {
			for(int j = 0; j < rays[i].length; j++) {
				rays[i][j] = genRay(i, j);
			}
		}
	}

	public List<PieceMove> genMove(int position, Bitboard blockers, boolean[] castleAvailable, int color, int piece) {
		switch(piece) {
			case 0:
				return genPawn(position, blockers, color);
			case 1:
				return BitboardUtility.bitboardToPieceMoves(position, knightMoves[position]);
			case 2:
				return genBishop(position, blockers);
			case 3:
				return genRook(position, blockers);
			case 4:
				List<PieceMove> bishopMoves = genBishop(position, blockers);
				List<PieceMove> rookMoves = genRook(position, blockers);
				bishopMoves.addAll(rookMoves);
				return bishopMoves;
			case 5:
				return genKing(position, blockers, castleAvailable, color);
			default:
				throw new IllegalArgumentException(String.format("Invalid Piece %s", piece));
		}
	}

	public List<PieceMove> genPawn(int position, Bitboard blockers, int color) {
		List<PieceMove> pieceMoves = new ArrayList<PieceMove>();
		int posChange = -((color * 2 - 1) * 8);
		//List of possible promotions if is promotion, SpecialMove.NONE if not
		SpecialMove[] promotions;
		if(position + posChange >= 56 || position + posChange < 8) {
			promotions = new SpecialMove[] {SpecialMove.PROMOTION_KNIGHT, SpecialMove.PROMOTION_BISHOP,
					SpecialMove.PROMOTION_ROOK, SpecialMove.PROMOTION_QUEEN};
		} else {
			promotions = new SpecialMove[] {SpecialMove.NONE};
		}
		if(!blockers.getSquare(position + posChange)) {
			for(SpecialMove promotion : promotions) {
				pieceMoves.add(new PieceMove(position, position + posChange, promotion));
			}
			if(color == 0) {
				//if still on starting position
				if(position >= 8 && position < 16 && !blockers.getSquare(position + 2 * posChange)) {
					for(SpecialMove promotion : promotions) {
						pieceMoves.add(new PieceMove(position, position + 2 * posChange, promotion));
					}
				}
			} else {
				//if still on starting position
				if(position >= 48 && position < 56 && !blockers.getSquare(position + 2 * posChange)) {
					for(SpecialMove promotion : promotions) {
						pieceMoves.add(new PieceMove(position, position + 2 * posChange, promotion));
					}
				}
			}
		}
		if(position + posChange + 1 >= 0 && position + posChange + 1 < 64 && blockers.getSquare(
				position + posChange + 1) && (position + 1) % 8 != 0) {
			for(SpecialMove promotion : promotions) {
				pieceMoves.add(new PieceMove(position, position + posChange + 1, promotion));
			}
		}
		if(position + posChange - 1 >= 0 && position + posChange - 1 < 64 && blockers.getSquare(
				position + posChange - 1) && position % 8 != 0) {
			for(SpecialMove promotion : promotions) {
				pieceMoves.add(new PieceMove(position, position + posChange - 1, promotion));
			}
		}
		return pieceMoves;
	}

	public List<PieceMove> genBishop(int position, Bitboard blockers) {
		Bitboard board = new Bitboard();
		for(int i = 0; i < 4; i++) {
			Bitboard maskedBlockers = new Bitboard(blockers.getBitboard() & rays[position][i * 2 + 1].getBitboard());
			int firstBlockerPosition = -1;
			Bitboard moves;
			if(maskedBlockers.getBitboard() == 0) {
				moves = rays[position][i * 2 + 1];
			} else {
				switch(i * 2 + 1) {
					case 1:
					case 7:
						firstBlockerPosition = BitboardUtility.scanUp(maskedBlockers);
						break;
					case 3:
					case 5:
						firstBlockerPosition = BitboardUtility.scanDown(maskedBlockers);
						break;
				}
				Bitboard blockerRay = new Bitboard(~rays[firstBlockerPosition][i * 2 + 1].getBitboard());
				moves = new Bitboard(rays[position][i * 2 + 1].getBitboard() & blockerRay.getBitboard());
			}
			board = new Bitboard(moves.getBitboard() | board.getBitboard());
		}
		return BitboardUtility.bitboardToPieceMoves(position, board);
	}

	public List<PieceMove> genRook(int position, Bitboard blockers) {
		Bitboard board = new Bitboard();
		for(int i = 0; i < 4; i++) {
			Bitboard maskedBlockers = new Bitboard(blockers.getBitboard() & rays[position][i * 2].getBitboard());
			int firstBlockerPosition = -1;
			Bitboard moves;
			if(maskedBlockers.getBitboard() == 0) {
				moves = rays[position][i * 2];
			} else {
				switch(i * 2) {
					case 0:
					case 2:
						firstBlockerPosition = BitboardUtility.scanUp(maskedBlockers);
						break;
					case 4:
					case 6:
						firstBlockerPosition = BitboardUtility.scanDown(maskedBlockers);
						break;
				}
				Bitboard blockerRay = new Bitboard(~rays[firstBlockerPosition][i * 2].getBitboard());
				moves = new Bitboard(rays[position][i * 2].getBitboard() & blockerRay.getBitboard());
			}
			board = new Bitboard(moves.getBitboard() | board.getBitboard());
		}
		return BitboardUtility.bitboardToPieceMoves(position, board);
	}

	public List<PieceMove> genKing(int position, Bitboard blockers, boolean[] castleAvailable, int color) {
		List<PieceMove> moves = BitboardUtility.bitboardToPieceMoves(position, kingMoves[position]);
		if(castleAvailable[color * 2] && !blockers.getSquare(position + 1) && !blockers.getSquare(position + 2)) {
			moves.add(new PieceMove(position, position + 2, SpecialMove.CASTLE_KINGSIDE));
		}
		if(castleAvailable[color * 2 + 1] && !blockers.getSquare(position - 1) && !blockers.getSquare(position - 2) && !blockers.getSquare(position - 3)) {
			moves.add(new PieceMove(position, position + 2, SpecialMove.CASTLE_QUEENSIDE));
		}
		return moves;
	}

	private Bitboard genRay(int position, int direction) {
		Bitboard ray = new Bitboard();
		switch(direction) {
			case 0:
				// up
				for(int i = position; i < 64; i += 8) {
					ray.setSquare(i, true);
				}
				break;
			case 1:
				// up-right
				for(int i = position; i < 64 && !(i % 8 == 0 && i != position); i += 9) {
					ray.setSquare(i, true);
				}
				break;
			case 2:
				// right
				for(int i = position; i < 64 && !(i % 8 == 0 && i != position); i++) {
					ray.setSquare(i, true);
				}
				break;
			case 3:
				// down-right
				for(int i = position; i >= 0 && !(i % 8 == 0 && i != position); i -= 7) {
					ray.setSquare(i, true);
				}
				break;
			case 4:
				// down
				for(int i = position; i >= 0; i -= 8) {
					ray.setSquare(i, true);
				}
				break;
			case 5:
				// down-left
				for(int i = position; i >= 0 && !((i + 1) % 8 == 0 && i != position); i -= 9) {
					ray.setSquare(i, true);
				}
				break;
			case 6:
				// left
				for(int i = position; i >= 0 && !((i + 1) % 8 == 0 && i != position); i--) {
					ray.setSquare(i, true);
				}
				break;
			case 7:
				// up-left
				for(int i = position; i < 64 && !((i + 1) % 8 == 0 && i != position); i += 7) {
					ray.setSquare(i, true);
				}
				break;
		}
		ray.setSquare(position, false);
		return ray;
	}
}
