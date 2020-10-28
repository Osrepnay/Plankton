package com.planktonengine;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;

public class UCIInterface{

	private volatile static PlanktonEngine engine=new PlanktonEngine();
	private static Game game=new Game();

	public static void main(String[] args) throws IOException{
		System.out.println("PlanktonEngine");
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		String[] input=reader.readLine().split(" ");
		int color=0;
		boolean debug=false;
		inputLoop: while(true){
			if(debug){
				System.out.printf("info string %s\n", input[0]);
			}
			switch(input[0]){
				case "uci":
					System.out.println("id name Plankton Engine");
					System.out.println("id author Anonymous Anonymous");
					System.out.println("uciok");
					break;
				case "isready":
					System.out.println("readyok");
					break;
				case "ucinewgame":
					game.resetGame();
					break;
				case "position":
					game.resetGame();
					int offset=3;
					if(input.length<=2){
						color=0;
					}else{
						color=input.length%2==0 ? 1 : 0;
					}
					if(input[1].equals("fen")){
						color=input[3].equals("w") ? 0 : 1;
						parseFEN(game, String.join(" ", Arrays.copyOfRange(input, 2, input.length)));
						offset=9;
						if(input.length<=7){
							color=0;
						}else{
							color=input.length%2==0 ? 0 : 1;
						}
					}
					for(int i=offset; i<input.length; i++){
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
					String printString="bestmove "+(char)(startPos[0]+'a')+(startPos[1]+1)+(char)(endPos[0]+'a')+(endPos[1]+1);
					if(game.piecePositions[color][0].getSquare((int)bestMove[0]) && (endPos[1]==0 || endPos[1]==7)){
						printString+="q";
					}
					System.out.println(printString);
					break;
				case "debug":
					if(input[1].equals("on")){
						debug=true;
						System.out.println("info string debug on");
					}else if(input[1].equals("off")){
						debug=false;
					}
					break;
				case "setoption":
					break;
				case "quit":
					break inputLoop;
				default:
					System.out.printf("Invalid command: %s\n", input[0]);
			}
			input=reader.readLine().split(" ");
		}
		reader.close();
	}

	public static void parseFEN(Game game, String fen){
		String[] fenSections=fen.split(" ");
		String[] boardRows=fenSections[0].split("/");
		HashMap<Character, Integer> pieceToInt=new HashMap<>();
		pieceToInt.put('p', 0);
		pieceToInt.put('n', 1);
		pieceToInt.put('b', 2);
		pieceToInt.put('r', 3);
		pieceToInt.put('q', 4);
		pieceToInt.put('k', 5);
		for(int i=boardRows.length-1; i>=0; i--){
			for(int j=0; j<8; j++){
				if(Character.isDigit(boardRows[i].charAt(j))){
					j+=boardRows[i].charAt(j)-'1';
				}else{
					int color=Character.isUpperCase(boardRows[i].charAt(j)) ? 0 : 1;
					game.piecePositions[color][pieceToInt.get(Character.toLowerCase(boardRows[i].charAt(j)))].setSquare(j, 7-i, true);
				}
			}
		}
		if(!fenSections[2].equals("-")){
			for(int i=0; i<fenSections[2].length(); i++){
				switch(fenSections[2].charAt(i)){
					case 'K':
						game.castleAvailable[0]=true;
						break;
					case 'k':
						game.castleAvailable[2]=true;
						break;
					case 'Q':
						game.castleAvailable[1]=true;
						break;
					case 'q':
						game.castleAvailable[3]=true;
						break;
				}
			}
		}
	}

	static class TellEngineStop implements Runnable{

		private long waitTime;

		public TellEngineStop(long waitTime){
			this.waitTime=waitTime;
		}

		@Override
		public void run(){
			try{
				Thread.sleep(waitTime);
			}catch(InterruptedException e){
				e.printStackTrace();
			}
			engine.keepSearching=false;
		}

	}

}
