package com.planktonengine;

import java.util.Scanner;

public class UCIInterface{

	private volatile static PlanktonEngine engine=new PlanktonEngine();
	private static Game game=new Game();

	public static void main(String[] args){
		Scanner s=new Scanner(System.in);
		String[] input=s.nextLine().split(" ");
		int color=0;
		inputLoop: while(true){
			switch(input[0]){
				case "uci":
					System.out.println("id name Plankton Engine");
					System.out.println("id author Anonymous Anonymous");
					System.out.println("uciok");
					break;
				case "isready":
					System.out.println("readyok");
					break;
				case "position":
					game.resetGame();
					if(input.length<=2){
						color=0;
					}else{
						color=input.length%2==0 ? 1 : 0;
					}
					for(int i=3; i<input.length; i++){
						int startPos=(input[i].charAt(0)-'a')+(Character.getNumericValue(input[i].charAt(1))-1)*8;
						int move=(input[i].charAt(2)-'a')+(Character.getNumericValue(input[i].charAt(3))-1)*8;
						int moveColor=i%2==0 ? 1 : 0;
						int piece=-1;
						boolean special=false;
						for(int j=0; j<game.piecePositions[0].length; j++){
							if(game.piecePositions[0][j].getSquare(startPos)){
								piece=j;
								break;
							}
						}
						if(piece==-1){
							for(int j=0; j<game.piecePositions[1].length; j++){
								if(game.piecePositions[1][j].getSquare(startPos)){
									piece=j;
									break;
								}
							}
						}
						if(piece==5){
							if(Math.abs(startPos-move)==2){
								special=true;
							}
						}else if(piece==0){
							if((move>=0 && move<8) || (move>=56 && move<64)){
								special=true;
							}else if(Math.abs(move-startPos)==7 || Math.abs(move-startPos)==9){
								boolean enPassantExists=true;
								for(int j=0; j<game.piecePositions[color ^ 1].length; j++){
									if(game.piecePositions[color ^ 1][j].getSquare(move)){
										enPassantExists=false;
									}
								}
								special=special || enPassantExists;
							}
						}
						game.makeMove(new int[]{startPos, move}, moveColor, piece, special);
					}
					Bitboard pieces=new Bitboard();
					for(int i=0; i<game.piecePositions[0].length; i++){
						pieces=new Bitboard(pieces.getBitboard() | game.piecePositions[0][i].getBitboard());
						pieces=new Bitboard(pieces.getBitboard() | game.piecePositions[1][i].getBitboard());
					}
					BitboardUtility.printBoard(pieces);
					System.out.println();
					game.setMoves();
					break;
				case "go":
					long[] times=new long[2];
					long moveTime=-1;
					boolean infinite=false;
					for(int i=1; i<input.length; i+=2){
						switch(input[i]){
							case "wtime":
								times[0]=Integer.valueOf(input[i+1]);
								break;
							case "btime":
								times[1]=Integer.valueOf(input[i+1]);
								break;
							case "movetime":
								moveTime=Integer.valueOf(input[i+1]);
								break;
							case "infinite":
								infinite=true;
								break;
						}
					}
					long startTime=System.currentTimeMillis();
					long time=moveTime==-1 ? (color==0 ? times[0]/20 : times[1]/20) : moveTime;
					time+=2000;
					double[] bestMove=new double[4];
					engine.keepSearching=true;
					Thread waitThread=new Thread(new TellEngineStop(time));
					waitThread.start();
					for(int i=1; System.currentTimeMillis()-startTime<time || infinite; i++){
						double[] bestMoveTemp=engine.bestMove(game, color, i);
						if(bestMoveTemp[0]!=-1){
							bestMove=bestMoveTemp;
						}else{
							break;
						}
					}
					int[] startPos=new int[]{(int)(bestMove[0]%8), (int)(bestMove[0]/8)};
					int[] endPos=new int[]{(int)(bestMove[1]%8), (int)(bestMove[1]/8)};
					System.out.println("bestmove "+(char)(startPos[0]+'a')+(startPos[1]+1)+
							(char)(endPos[0]+'a')+(endPos[1]+1));
					Bitboard pieces1=new Bitboard();
					for(int i=0; i<game.piecePositions[0].length; i++){
						pieces1=new Bitboard(pieces1.getBitboard() | game.piecePositions[0][i].getBitboard());
						pieces1=new Bitboard(pieces1.getBitboard() | game.piecePositions[1][i].getBitboard());
					}
					BitboardUtility.printBoard(pieces1);
					System.out.println();
					break;
				case "quit":
					break inputLoop;
			}
			input=s.nextLine().split(" ");
		}
		s.close();
	}

	static class TellEngineStop implements Runnable{

		private long waitTime;

		public TellEngineStop(long waitTime){
			this.waitTime=waitTime;
		}

		@Override
		public void run(){
			System.out.println(waitTime);
			try{
				Thread.sleep(waitTime);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			engine.keepSearching=false;
		}

	}

}
