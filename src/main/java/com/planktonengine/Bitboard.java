package com.planktonengine;

public class Bitboard{
	private long bitboard=0;
	private String errorMessage="Bitboard index out of range: %s.";

	public Bitboard(){
	}

	public Bitboard(long bitboard){
		this.bitboard=bitboard;
	}

	public boolean getSquare(int position){
		if(position<0 || position>=64){
			throw new IllegalArgumentException(String.format(errorMessage, position));
		}
		return ((bitboard >> position) & 1)!=0;
	}

	public boolean getSquare(int column, int row){
		if(column<0 || column>=8){
			throw new IllegalArgumentException(String.format(errorMessage, column));
		}
		if(row<0 || row>=8){
			throw new IllegalArgumentException(String.format(errorMessage, row));
		}
		return ((bitboard >> column+row*8) & 1)!=0;
	}

	public void setSquare(int position, boolean value){
		if(position<0 || position>=64){
			throw new IllegalArgumentException(String.format(errorMessage, position));
		}
		if(value){
			bitboard|=(1L << position);
		}else{
			bitboard&=~(1L << position);
		}
	}

	public void setSquare(int column, int row, boolean value){
		if(column<0 || column>=8){
			throw new IllegalArgumentException(String.format(errorMessage, column));
		}
		if(row<0 || row>=8){
			throw new IllegalArgumentException(String.format(errorMessage, row));
		}
		if(value){
			bitboard|=(1L << column+row*8);
		}else{
			bitboard&=~(1L << column+row*8);
		}
	}

	@Override
	public boolean equals(Object object){
		if(!(object instanceof Bitboard)){
			return false;
		}
		Bitboard board=(Bitboard)object;
		return board.getBitboard()==bitboard;
	}

	@Override
	public int hashCode(){
		return Long.hashCode(bitboard);
	}

	public long getBitboard(){
		return bitboard;
	}

}
