package com.planktonengine;

import java.util.Arrays;

public class PlanktonEngine{

	public volatile boolean keepSearching=true;
	private double[] pieceScores=new double[]{1, 3, 3.25, 5, 9, 10000};

	public double[] bestMove(Game game, int color, int depth){
		double[] bestMove=new double[]{-1, -1, 0};
		double bestMoveScore=color==0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		for(int square=0; square<64; square++){
			if(!game.squareToColor.containsKey(square) || game.squareToColor.get(square)!=color){
				continue;
			}
			int piece=game.squareToPiece.get(square);
			for(int moveIndex=0; moveIndex<game.pieceMoves[square].getMoves().size(); moveIndex++){
				if(!keepSearching){
					return new double[]{-1, -1, 0};
				}
				int[] move=new int[]{square, game.pieceMoves[square].getMove(moveIndex)};
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				if(!validMove(game, move, color, piece, specialMove)){
					continue;
				}
				PrevMoveGameState prevMoveState=game.makeMove(move, color, piece, specialMove);
				double moveScore=color==0
						? min(game, bestMoveScore, Integer.MAX_VALUE,
						depth-1)
						: max(game, Integer.MIN_VALUE, bestMoveScore,
						depth-1);
				game.unMakeMove(move, color, piece, specialMove, prevMoveState);
				//sign for ran out of time
				if(moveScore==Double.MAX_VALUE){
					return new double[]{-1, -1, 0};
				}
				if(color==0 ? moveScore>bestMoveScore : moveScore<bestMoveScore){
					bestMoveScore=moveScore;
					bestMove=new double[]{move[0], move[1], bestMoveScore};
				}
			}
		}
		return bestMove;
	}

	public double max(Game game, double alpha, double beta, int depth){
		if(depth<=0){
			return qMax(game, alpha, beta);
		}
		for(int square=0; square<64; square++){
			if(!game.squareToColor.containsKey(square) || game.squareToColor.get(square)!=0){
				continue;
			}
			int piece=game.squareToPiece.get(square);
			for(int moveIndex=0; moveIndex<game.pieceMoves[square].getMoves().size(); moveIndex++){
				if(!keepSearching){
					return Double.MAX_VALUE;
				}
				int[] move=new int[]{square, game.pieceMoves[square].getMove(moveIndex)};
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				if(!validMove(game, move, 0, piece, specialMove)){
					continue;
				}
				PrevMoveGameState prevMoveState=game.makeMove(move, 0, piece, specialMove);
				double moveScore=min(game, alpha, beta, depth-1);
				game.unMakeMove(move, 0, piece, specialMove, prevMoveState);
				if(moveScore>=beta){
					return beta;
				}
				if(moveScore>alpha){
					alpha=moveScore;
				}
			}
		}
		return alpha;
	}

	public double min(Game game, double alpha, double beta, int depth){
		if(depth<=0){
			return qMin(game, alpha, beta);
		}
		for(int square=0; square<64; square++){
			if(!game.squareToColor.containsKey(square) || game.squareToColor.get(square)!=1){
				continue;
			}
			int piece=game.squareToPiece.get(square);
			for(int moveIndex=0; moveIndex<game.pieceMoves[square].getMoves().size(); moveIndex++){
				if(!keepSearching){
					return Double.MAX_VALUE;
				}
				int[] move=new int[]{square, game.pieceMoves[square].getMove(moveIndex)};
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				if(!validMove(game, move, 1, piece, specialMove)){
					continue;
				}
				PrevMoveGameState prevMoveState=game.makeMove(move, 1, piece, specialMove);
				double moveScore=max(game, alpha, beta, depth-1);
				game.unMakeMove(move, 1, piece, specialMove, prevMoveState);
				if(moveScore<=alpha){
					return alpha;
				}
				if(moveScore<beta){
					beta=moveScore;
				}
			}
		}
		return beta;
	}

	public double qMax(Game game, double alpha, double beta){
		double standPat=eval(game);
		if(standPat>=beta){
			return beta;
		}
		if(standPat>alpha){
			alpha=standPat;
		}
		for(int square=0; square<64; square++){
			if(!game.squareToColor.containsKey(square) || game.squareToColor.get(square)!=0){
				continue;
			}
			int piece=game.squareToPiece.get(square);
			for(int moveIndex=0; moveIndex<game.pieceMoves[square].getMoves().size(); moveIndex++){
				if(!keepSearching){
					return Double.MAX_VALUE;
				}
				int[] move=new int[]{square, game.pieceMoves[square].getMove(moveIndex)};
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				if(!game.squareToColor.containsKey(move[1])){
					continue;
				}
				if(!validMove(game, move, 0, piece, specialMove)){
					continue;
				}
				if(see(game, move, specialMove)<0){
					continue;
				}
				PrevMoveGameState prevMoveState=game.makeMove(move, 0, piece, specialMove);
				double moveScore=qMin(game, alpha, beta);
				game.unMakeMove(move, 0, piece, specialMove, prevMoveState);
				if(moveScore>=beta){
					return beta;
				}
				if(moveScore>alpha){
					alpha=moveScore;
				}
			}
		}
		return alpha;
	}

	public double qMin(Game game, double alpha, double beta){
		double standPat=eval(game);
		if(standPat<=alpha){
			return alpha;
		}
		if(standPat<beta){
			beta=standPat;
		}
		for(int square=0; square<64; square++){
			if(!game.squareToColor.containsKey(square) || game.squareToColor.get(square)!=1){
				continue;
			}
			int piece=game.squareToPiece.get(square);
			for(int moveIndex=0; moveIndex<game.pieceMoves[square].getMoves().size(); moveIndex++){
				if(!keepSearching){
					return Double.MAX_VALUE;
				}
				int[] move=new int[]{square, game.pieceMoves[square].getMove(moveIndex)};
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				if(!game.squareToColor.containsKey(move[1])){
					continue;
				}
				if(!validMove(game, move, 1, piece, specialMove)){
					continue;
				}
				if(see(game, move, specialMove)>0){
					continue;
				}
				PrevMoveGameState prevMoveState=game.makeMove(move, 1, piece, specialMove);
				double moveScore=qMax(game, alpha, beta);
				game.unMakeMove(move, 1, piece, specialMove, prevMoveState);
				if(moveScore<=alpha){
					return alpha;
				}
				if(moveScore<beta){
					beta=moveScore;
				}
			}
		}
		return beta;
	}

	public double see(Game game, int[] move, boolean special){
		int color=game.squareToColor.get(move[0]);
		int piece=game.squareToPiece.get(move[0]);
		double score=pieceScores[game.squareToPiece.get(move[1])]*(-color*2+1);
		int lowestAttackerSquare=-1;
		PrevMoveGameState prevMoveState=game.makeMove(move, color, piece, special);
		for(int square=0; square<64; square++){
			if(game.squareToColor.containsKey(square) && game.squareToColor.get(square)!=color){
				int moveIndex=game.pieceMoves[square].getMoves().indexOf(move[1]);
				if(moveIndex==-1){
					continue;
				}
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				int squareColor=game.squareToColor.get(square);
				int squarePiece=game.squareToPiece.get(square);
				if(validMove(game, new int[]{square, move[1]}, squareColor, squarePiece, specialMove)){
					if(lowestAttackerSquare==-1 || game.squareToPiece.get(square)<game.squareToPiece.
							get(lowestAttackerSquare)){
						lowestAttackerSquare=square;
						if(game.squareToPiece.get(square)==0){
							break;
						}
					}
				}
			}
		}
		if(lowestAttackerSquare!=-1){
			int moveIndex=game.pieceMoves[lowestAttackerSquare].getMoves().indexOf(move[1]);
			boolean specialMove=game.pieceMoves[lowestAttackerSquare].isSpecial(moveIndex);
			score+=see(game, new int[]{lowestAttackerSquare, move[1]}, specialMove);
		}
		game.unMakeMove(move, color, piece, special, prevMoveState);
		return score;
	}

	public double eval(Game game){
		if(inCheckmate(game, 0)){
			return -10000;
		}
		if(inCheckmate(game, 1)){
			return 10000;
		}
		double score=0;
		double[] totalMaterial=new double[2];
		for(int piece=0; piece<game.piecePositions[0].length; piece++){
			double wScore=Long.bitCount(game.piecePositions[0][piece].getBitboard())*pieceScores[piece];
			double bScore=Long.bitCount(game.piecePositions[1][piece].getBitboard())*pieceScores[piece];
			score+=wScore;
			score-=bScore;
			if(piece!=5){
				totalMaterial[0]+=wScore;
				totalMaterial[1]+=bScore;
			}
		}
		int[] moveCount=new int[2];
		for(int square=0; square<64; square++){
			if(game.squareToColor.containsKey(square)){
				if(game.squareToColor.get(square)==0 && totalMaterial[1]>=10){
					score+=PSTables.psTables[game.squareToPiece.get(square)][square]/15;
				}else if(game.squareToColor.get(square)==1 && totalMaterial[0]>=10){
					score-=PSTables.psTables[game.squareToPiece.get(square)][63-square]/15;
				}
				moveCount[game.squareToColor.get(square)]+=game.pieceMoves[square].getMoves().size();
			}
		}
		score+=moveCount[0]/100;
		score-=moveCount[1]/100;
		return score;
	}

	public boolean validMove(Game game, int[] move, int color, int piece, boolean specialMove){
		if(game.squareToColor.containsKey(move[1]) && game.squareToColor.get(move[1])==color){
			return false;
		}
		if(piece==5 && specialMove){
			if(move[1]>move[0]){
				//kingside
				if(game.squareToColor.containsKey(move[0]+1)||
						game.squareToColor.containsKey(move[0]+2)){
					return false;
				}
				PrevMoveGameState midCastleState=game.makeMove(new int[]{move[0], move[0]+1}, color, piece,
						false);
				if(midCastleState.getCapturePiece()!=-1 || inCheck(game, color)){
					game.unMakeMove(new int[]{move[0], move[0]+1}, color, piece, false, midCastleState);
					return false;
				}
				game.unMakeMove(new int[]{move[0], move[0]+1}, color, piece, false, midCastleState);
			}else{
				//queenside
				if(game.squareToColor.containsKey(move[0]-1)||
						game.squareToColor.containsKey(move[0]-2) ||
						game.squareToColor.containsKey(move[0]-3)){
					return false;
				}
				PrevMoveGameState midCastleState=game.makeMove(new int[]{move[0], move[0]-1}, color, piece,
						false);
				if(midCastleState.getCapturePiece()!=-1 || inCheck(game, color)){
					game.unMakeMove(new int[]{move[0], move[0]-1}, color, piece, false, midCastleState);
					return false;
				}
				game.unMakeMove(new int[]{move[0], move[0]-1}, color, piece, false, midCastleState);
			}
		}
		PrevMoveGameState prevMoveState=game.makeMove(move, color, piece, specialMove);
		if(inCheck(game, color)){
			game.unMakeMove(move, color, piece, specialMove, prevMoveState);
			return false;
		}
		game.unMakeMove(move, color, piece, specialMove, prevMoveState);
		return true;
	}

	public boolean inCheck(Game game, int color){
		int opponentColor=color ^ 1;
		for(int square=0; square<64; square++){
			if(game.squareToColor.containsKey(square) && game.squareToColor.get(square)==opponentColor){
				if((BitboardUtility.pieceMovesToBitboard(game.pieceMoves[square]).getBitboard()
						& game.piecePositions[color][5].getBitboard())!=0){
					return true;
				}
			}
		}
		return false;
	}

	public boolean inCheckmate(Game game, int color){
		if(!inCheck(game, color)){
			return false;
		}
		for(int square=0; square<64; square++){
			if(!game.squareToColor.containsKey(square) || game.squareToColor.get(square)!=color){
				continue;
			}
			int piece=game.squareToPiece.get(square);
			for(int moveIndex=0; moveIndex<game.pieceMoves[square].getMoves().size(); moveIndex++){
				int[] move=new int[]{square, game.pieceMoves[square].getMove(moveIndex)};
				boolean specialMove=game.pieceMoves[square].isSpecial(moveIndex);
				if(!validMove(game, move, color, piece, specialMove)){
					continue;
				}
				PrevMoveGameState prevMoveState=game.makeMove(move, color, piece, specialMove);
				if(!inCheck(game, color)){
					game.unMakeMove(move, color, piece, specialMove, prevMoveState);
					return false;
				}
				game.unMakeMove(move, color, piece, specialMove, prevMoveState);
			}
		}
		return true;
	}

}
