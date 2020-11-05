package com.planktonengine;

import java.util.HashMap;

public class Game{
	public Bitboard[][] piecePositions=new Bitboard[2][6];
	public HashMap<Integer, Integer> squareToColor=new HashMap<>();
	public HashMap<Integer, Integer> squareToPiece=new HashMap<>();
	public PieceMoves[] pieceMoves=new PieceMoves[64];
	public boolean[] castleAvailable=new boolean[]{true, true, true, true};
	public MoveGen moveGen=new MoveGen();

	public Game(){
		resetGame();
	}

	public PrevMoveGameState makeMove(int[] move, int color, int piece, boolean special){
		squareToColor.remove(move[0]);
		squareToPiece.remove(move[0]);
		int opponentColor=color ^ 1;
		int capturePiece=-1;
		if(squareToColor.containsKey(move[1])){
			capturePiece=squareToPiece.get(move[1]);
			piecePositions[opponentColor][capturePiece].setSquare(move[1], false);
			squareToColor.remove(move[1]);
			squareToPiece.remove(move[1]);
		}
		squareToColor.put(move[1], color);
		squareToPiece.put(move[1], piece);
		piecePositions[color][piece].setSquare(move[0], false);
		piecePositions[color][piece].setSquare(move[1], true);
		int promotion=-1;
		if(piece==5){
			castleAvailable=new boolean[4];
			if(special){
				if(move[1]-move[0]>0){
					//kingside
					piecePositions[color][3].setSquare(move[1]+1, false);
					piecePositions[color][3].setSquare(move[1]-1, true);
					squareToColor.remove(move[1]+1);
					squareToPiece.remove(move[1]+1);
					squareToColor.put(move[1]-1, color);
					squareToPiece.put(move[1]-1, 3);
				}else{
					//queenside
					piecePositions[color][3].setSquare(move[1]-2, false);
					piecePositions[color][3].setSquare(move[1]+1, true);
					squareToColor.remove(move[1]-2);
					squareToPiece.remove(move[1]-2);
					squareToColor.put(move[1]+1, color);
					squareToPiece.put(move[1]+1, 3);
				}
			}
		}else if(piece==3){
			switch(move[0]){
				case 0:
					castleAvailable[1]=false;
					break;
				case 7:
					castleAvailable[0]=false;
					break;
				case 56:
					castleAvailable[3]=false;
					break;
				case 63:
					castleAvailable[2]=false;
					break;
			}
		}else if(piece==0){
			if(special){
				if(move[1]>=56 || move[1]<8){
					piecePositions[color][piece].setSquare(move[1], false);
					piecePositions[color][4].setSquare(move[1], true);
					squareToPiece.remove(move[1]);
					squareToPiece.put(move[1], 4);
					promotion=4;
				}else{
					capturePiece=0;
					if(color==0){
						if(move[1]-move[0]==7){
							piecePositions[opponentColor][0].setSquare(move[0]-1, false);
							squareToColor.remove(move[0]-1);
							squareToPiece.remove(move[0]-1);
						}else{
							piecePositions[opponentColor][0].setSquare(move[0]+1, false);
							squareToColor.remove(move[0]+1);
							squareToPiece.remove(move[0]+1);
						}
					}else{
						if(move[0]-move[1]==7){
							piecePositions[opponentColor][0].setSquare(move[0]+1, false);
							squareToColor.remove(move[0]+1);
							squareToPiece.remove(move[0]+1);
						}else{
							piecePositions[opponentColor][0].setSquare(move[0]-1, false);
							squareToColor.remove(move[0]-1);
							squareToPiece.remove(move[0]-1);
						}
					}
				}
			}
		}
		PrevMoveGameState prevMoveState=new PrevMoveGameState(capturePiece, castleAvailable, promotion);
		setMoves();
		return prevMoveState;
	}

	public PrevMoveGameState makeMove(int[] move, int color, int promotePiece){
		squareToColor.remove(move[0]);
		squareToPiece.remove(move[0]);
		int opponentColor=color ^ 1;
		int capturePiece=-1;
		if(squareToColor.containsKey(move[1])){
			capturePiece=squareToPiece.get(move[1]);
			piecePositions[opponentColor][capturePiece].setSquare(move[1], false);
			squareToColor.remove(move[1]);
			squareToPiece.remove(move[1]);
		}
		squareToColor.put(move[1], color);
		squareToPiece.put(move[1], promotePiece);
		piecePositions[color][0].setSquare(move[0], false);
		piecePositions[color][promotePiece].setSquare(move[1], true);
		PrevMoveGameState prevMoveGameState=new PrevMoveGameState(capturePiece, castleAvailable, promotePiece);
		return prevMoveGameState;
	}

	public void unMakeMove(int[] move, int color, int piece, boolean special, PrevMoveGameState prevMoveState){
		squareToColor.remove(move[1]);
		squareToPiece.remove(move[1]);
		int opponentColor=color ^ 1;
		if(prevMoveState.getCapturePiece()!=-1){
			piecePositions[opponentColor][prevMoveState.getCapturePiece()].setSquare(move[1], true);
			squareToColor.put(move[1], opponentColor);
			squareToPiece.put(move[1], prevMoveState.getCapturePiece());
		}
		squareToColor.put(move[0], color);
		squareToPiece.put(move[0], piece);
		piecePositions[color][piece].setSquare(move[0], true);
		piecePositions[color][piece].setSquare(move[1], false);
		castleAvailable=prevMoveState.getCastleAvailable();
		if(piece==5){
			if(special){
				if(move[1]-move[0]>0){
					//kingside
					piecePositions[color][3].setSquare(move[1]+1, true);
					piecePositions[color][3].setSquare(move[1]-1, false);
					squareToColor.remove(move[1]-1);
					squareToPiece.remove(move[1]-1);
					squareToColor.put(move[1]+1, color);
					squareToPiece.put(move[1]+1, 3);
				}else{
					//queenside
					piecePositions[color][3].setSquare(move[1]-2, true);
					piecePositions[color][3].setSquare(move[1]+1, false);
					squareToColor.remove(move[1]+1);
					squareToPiece.remove(move[1]+1);
					squareToColor.put(move[1]-2, color);
					squareToPiece.put(move[1]-2, 3);
				}
			}
		}else if(piece==0){
			if(special){
				if(move[1]>=56 || move[1]<8){
					piecePositions[color][piece].setSquare(move[0], true);
					piecePositions[color][prevMoveState.getPromotion()].setSquare(move[1], false);
					squareToPiece.remove(move[0]);
					squareToPiece.put(move[0], 0);
				}else{
					if(color==0){
						if(move[1]-move[0]==7){
							piecePositions[opponentColor][0].setSquare(move[0]-1, true);
							squareToColor.put(move[0]-1, opponentColor);
							squareToPiece.put(move[0]-1, 0);
						}else{
							piecePositions[opponentColor][0].setSquare(move[0]+1, true);
							squareToColor.put(move[0]+1, opponentColor);
							squareToPiece.put(move[0]+1, 0);
						}
					}else{
						if(move[0]-move[1]==7){
							piecePositions[opponentColor][0].setSquare(move[0]+1, true);
							squareToColor.put(move[0]+1, opponentColor);
							squareToPiece.put(move[0]+1, 0);
						}else{
							piecePositions[opponentColor][0].setSquare(move[0]-1, true);
							squareToColor.put(move[0]-1, opponentColor);
							squareToPiece.put(move[0]-1, 0);
						}
					}
				}
			}
		}
		setMoves();
	}

	public void createPiece(int color, int piece, int[] position){
		piecePositions[color][piece].setSquare(position[0]+position[1]*8, true);
		squareToColor.put(position[0]+position[1]*8, color);
		squareToPiece.put(position[0]+position[1]*8, piece);
	}

	public void deletePiece(int color, int piece, int[] position){
		piecePositions[color][piece].setSquare(position[0]+position[1]*8, false);
		squareToColor.remove(position[0]+position[1]*8);
		squareToPiece.remove(position[0]+position[1]*8);
	}

	public void setMoves(){
		Bitboard blockers=new Bitboard();
		for(int i=0; i<piecePositions.length; i++){
			for(int j=0; j<piecePositions[i].length; j++){
				blockers=new Bitboard(piecePositions[i][j].getBitboard() | blockers.getBitboard());
			}
		}
		for(int i=0; i<pieceMoves.length; i++){
			pieceMoves[i]=new PieceMoves();
		}
		for(int color=0; color<piecePositions.length; color++){
			for(int piece=0; piece<piecePositions[color].length; piece++){
				for(int position=0; position<64; position++){
					if(piecePositions[color][piece].getSquare(position)){
						pieceMoves[position]=moveGen.genMove(position, blockers, castleAvailable, color, piece);
					}
				}
			}
		}
	}

	public void resetGame(){
		for(int i=0; i<piecePositions.length; i++){
			for(int j=0; j<piecePositions[i].length; j++){
				piecePositions[i][j]=new Bitboard();
			}
		}
		squareToColor=new HashMap<>();
		squareToPiece=new HashMap<>();

		for(int i=0; i<8; i++){
			createPiece(0, 0, new int[]{i, 1});
			createPiece(1, 0, new int[]{i, 6});
		}
		createPiece(0, 1, new int[]{1, 0});
		createPiece(0, 1, new int[]{6, 0});
		createPiece(0, 2, new int[]{2, 0});
		createPiece(0, 2, new int[]{5, 0});
		createPiece(0, 3, new int[]{0, 0});
		createPiece(0, 3, new int[]{7, 0});
		createPiece(0, 4, new int[]{3, 0});
		createPiece(0, 5, new int[]{4, 0});

		createPiece(1, 1, new int[]{1, 7});
		createPiece(1, 1, new int[]{6, 7});
		createPiece(1, 2, new int[]{2, 7});
		createPiece(1, 2, new int[]{5, 7});
		createPiece(1, 3, new int[]{0, 7});
		createPiece(1, 3, new int[]{7, 7});
		createPiece(1, 4, new int[]{3, 7});
		createPiece(1, 5, new int[]{4, 7});
		castleAvailable=new boolean[]{true, true, true, true};
		setMoves();
	}

	public void blankGame(){
		for(int i=0; i<piecePositions.length; i++){
			for(int j=0; j<piecePositions[i].length; j++){
				piecePositions[i][j]=new Bitboard();
			}
		}
		squareToColor=new HashMap<>();
		squareToPiece=new HashMap<>();
		castleAvailable=new boolean[]{true, true, true, true};
		setMoves();
	}

}
