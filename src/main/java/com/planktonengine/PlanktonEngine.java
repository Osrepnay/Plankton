package com.planktonengine;

/**
 * A simple chess engine with minimax and alpha-beta pruning. Note that squares in the engine are represented by a single number, so for example a1 would be 1, b1 would be 2, and so on.
 */
public class PlanktonEngine{

	public volatile boolean keepSearching=true;

	/**
	 * @param color The color to search
	 * @param depth The depth to search to
	 * @return A double array of length 3 that holds the square of the piece to move, the square the piece should move to, and score of the move as evaluated by the engine, respectively.
	 */
	public double[] bestMove(Game game, int color, int depth){
		double[] bestMove=new double[]{-1, -1, 0};
		double bestMoveScore=color==0 ? Integer.MIN_VALUE : Integer.MAX_VALUE;
		for(int piece=0; piece<game.piecePositions[color].length; piece++){
			for(int square=0; square<64; square++){
				if(!game.piecePositions[color][piece].getSquare(square)){
					continue;
				}
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
		}
		return bestMove;
	}

	/**
	 * @param game Current state of the game
	 * @param alpha Current alpha value
	 * @param beta Current beta value
	 * @param depth Depth to keep searching for
	 * @return A double of the score the engine evaluates the position to be.
	 */
	public double max(Game game, double alpha, double beta, int depth){
		if(depth<=0){
			return eval(game);
		}
		for(int piece=0; piece<game.piecePositions[0].length; piece++){
			for(int square=0; square<64; square++){
				if(!game.piecePositions[0][piece].getSquare(square)){
					continue;
				}
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
		}
		return alpha;
	}

	/**
	 * @param game Current state of the game
	 * @param alpha Current alpha value
	 * @param beta Current beta value
	 * @param depth Depth to keep searching for
	 * @return A double of the score the engine evaluates the position to be.
	 */
	public double min(Game game, double alpha, double beta, int depth){
		if(depth<=0){
			return eval(game);
		}
		for(int piece=0; piece<game.piecePositions[1].length; piece++){
			for(int square=0; square<64; square++){
				if(!game.piecePositions[1][piece].getSquare(square)){
					continue;
				}
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
		}
		return beta;
	}

	/**
	 * @param game Current state of the game
	 * @return The score based purely off of the pieces, moves, and if it is checkmate.
	 */
	public double eval(Game game){
		if(inCheckmate(game, 0)){
			return -10000;
		}
		if(inCheckmate(game, 1)){
			return 10000;
		}
		double[] pieceScores=new double[]{1, 3, 3.25, 5, 9, 10000};
		double score=0;
		double totalScore=0;
		for(int piece=0; piece<game.piecePositions[0].length; piece++){
			double wScore=Long.bitCount(game.piecePositions[0][piece].getBitboard())*pieceScores[piece];
			double bScore=Long.bitCount(game.piecePositions[1][piece].getBitboard())*pieceScores[piece];
			score+=wScore;
			score-=bScore;
			totalScore+=wScore+bScore;
		}
		int[] moveCount=new int[2];
		if(totalScore>=10){
			for(int square=0; square<64; square++){
				int color=-1;
				for(int piece=0; piece<game.piecePositions[0].length; piece++){
					if(game.piecePositions[0][piece].getSquare(square)){
						color=0;
						score+=PSTables.psTables[piece][square]/10;
					}else if(game.piecePositions[1][piece].getSquare(square)){
						color=1;
						score-=PSTables.psTables[piece][63-square]/10;
					}
				}
				if(color!=-1){
					moveCount[color]+=game.pieceMoves[square].getMoves().size();
				}
			}
		}
		score+=moveCount[0]/100;
		score-=moveCount[1]/100;
		return score;
	}

	/**
	 * @param game Current state of the game
	 * @param move Move to check
	 * @param moveIndex Index in PieceMoves of piece of move
	 * @param color Color to check
	 * @param piece Piece to check
	 * @return Returns a boolean that tells if the move is legal or not
	 */
	public boolean validMove(Game game, int[] move, int color, int piece, boolean specialMove){
		for(int i=0; i<game.piecePositions[color].length; i++){
			if(game.piecePositions[color][i].getSquare(move[1])){
				return false;
			}
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

	/**
	 * @param game Current state of the game
	 * @param color Color to check
	 * @return Returns a boolean that tells whether the king of the color is in check
	 */
	public boolean inCheck(Game game, int color){
		int opponentColor=color ^ 1;
		for(int square=0; square<64; square++){
			for(int piece=0; piece<game.piecePositions[opponentColor].length; piece++){
				if(game.piecePositions[opponentColor][piece].getSquare(square)){
					if((BitboardUtility.pieceMovesToBitboard(game.pieceMoves[square]).getBitboard()
							& game.piecePositions[color][5].getBitboard())!=0){
						return true;
					}
				}
			}
		}
		return false;
	}

	/**
	 * @param game Current state of the game
	 * @param color Color to check
	 * @return Returns a boolean that tells whether the king of the color is checkmated
	 */
	public boolean inCheckmate(Game game, int color){
		if(!inCheck(game, color)){
			return false;
		}
		for(int piece=0; piece<game.piecePositions[color].length; piece++){
			for(int square=0; square<64; square++){
				if(!game.piecePositions[color][piece].getSquare(square)){
					continue;
				}
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
		}
		return true;
	}

}
