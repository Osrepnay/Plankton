package com.planktonengine;

import java.util.ArrayList;
import java.util.List;

public class PieceMoves{
	private List<Integer> moves=new ArrayList<Integer>();
	private List<Boolean> specialMove=new ArrayList<Boolean>();

	public PieceMoves(){
	}

	public PieceMoves(List<Integer> moves, List<Boolean> specialMove){
		this.moves=moves;
		this.specialMove=specialMove;
	}

	public void addMove(int move){
		moves.add(move);
		specialMove.add(false);
	}

	public void addMove(int move, boolean special){
		moves.add(move);
		specialMove.add(special);
	}

	public void setMoves(PieceMoves newMoves){
		this.moves=newMoves.getMoves();
		this.specialMove=newMoves.getSpecial();
	}

	public Integer getMove(int index){
		return moves.get(index);
	}

	public Boolean isSpecial(int index){
		return specialMove.get(index);
	}

	public List<Integer> getMoves(){
		return moves;
	}

	public List<Boolean> getSpecial(){
		return specialMove;
	}
}
