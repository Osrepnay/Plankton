package com.planktonengine;

public class PlanktonEngine{

	public volatile boolean keepSearching=true;

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

	public double eval(Game game){
		if(inCheckmate(game, 0)){
			return -10000;
		}
		if(inCheckmate(game, 1)){
			return 10000;
		}
		double[] pieceScores=new double[]{1, 3, 3.25, 5, 9, 10000};
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
					score+=PSTables.psTables[game.squareToPiece.get(square)][square]/10;
				}else if(game.squareToColor.get(square)==1 && totalMaterial[0]>=10){
					score-=PSTables.psTables[game.squareToPiece.get(square)][63-square]/10;
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
		PrevMoveGameState prevMoveState=game.makeMove(move, color, piece, specialMove);
		if(inCheck(game, color)){
			game.unMakeMove(move, color, piece, specialMove, prevMoveState);
			return false;
		}
		if(piece==5 && specialMove){
			if(prevMoveState.getCapturePiece()!=-1 ||
					(game.castleAvailable[0] && !game.piecePositions[color][3].getSquare(7)) ||
					(game.castleAvailable[1] && !game.piecePositions[color][3].getSquare(0)) ||
					(game.castleAvailable[2] && !game.piecePositions[color][3].getSquare(63)) ||
					(game.castleAvailable[3] && !game.piecePositions[color][3].getSquare(56))){
				return false;
			}
			if(move[1]-move[0]>0){
				//kingside
				PrevMoveGameState midCastleState=game.makeMove(new int[]{move[0], move[1]-1}, color, piece,
						specialMove);
				if(midCastleState.getCapturePiece()!=-1 || inCheck(game, color)){
					game.unMakeMove(new int[]{move[0], move[1]-1}, color, piece, specialMove, midCastleState);
					return false;
				}
			}else{
				//queenside
				PrevMoveGameState midCastleState=game.makeMove(new int[]{move[0], move[1]-1}, color, piece,
						specialMove);
				if(midCastleState.getCapturePiece()!=-1 || inCheck(game, color)){
					game.unMakeMove(new int[]{move[0], move[1]-1}, color, piece, specialMove, midCastleState);
					return false;
				}
			}
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
