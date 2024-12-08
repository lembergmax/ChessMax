package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.ui.Board;

import java.awt.image.BufferedImage;

public class Pawn extends Piece {

	public Pawn(Board board, int column, int row, boolean isWhite) {
		super(board);

		setColumn(column);
		setRow(row);
		setXPos(column * getBoard().getTileSize());
		setYPos(row * getBoard().getTileSize());

		setPieceValue(1);
		setWhite(isWhite);
		setName("Pawn");

		int sheetScale = getSheetScale();
		setSprite(getSheet().getSubimage(5 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	// TODO: EnPassant does not work properly

	@Override
	public boolean isValidMovement(int column, int row) {
		// Validate that the target coordinates are within the chessboard's bounds.
		// The board is a 8x8 grid where valid indices for columns and rows are 0-7.
		if (column < 0 || column > 7 || row < 0 || row > 7) {
			return false; // If the target position is out of bounds, the move is invalid.
		}

		// Determine the direction of movement based on the piece's color.
		// White pawns move "up" the board (decreasing row index), while black pawns move "down" (increasing row index).
		int colorIndex = isWhite() ? 1 : -1;

		// Check for a single-step forward move.
		// A pawn can move forward by one row if:
		// - It stays in the same column.
		// - The target square is exactly one step forward in the direction based on the color.
		// - The target square is unoccupied (no piece at the target position).
		if (this.getColumn() == column // Same column
			    && row == this.getRow() - colorIndex // One row forward
			    && getBoard().getPieceAt(column, row) == null) { // Target square is empty
			return true;
		}

		// Check for a double-step forward move.
		// A pawn can move forward by two rows if:
		// - It is on its starting row (row 6 for white, row 1 for black).
		// - It stays in the same column.
		// - The target square (two steps forward) is unoccupied.
		// - The square directly in front of the pawn (one step forward) is also unoccupied.
		if (this.getRow() == (isWhite() ? 6 : 1) // On starting row
			    && this.getColumn() == column // Same column
			    && row == this.getRow() - colorIndex * 2 // Two rows forward
			    && getBoard().getPieceAt(column, row) == null // Target square is empty
			    && getBoard().getPieceAt(column, this.getRow() - colorIndex) == null) { // Intermediate square is empty
			return true;
		}

		// Check for diagonal capture.
		// A pawn can capture an opponent's piece diagonally if:
		// - The target square is one row forward in the correct direction.
		// - The target column is exactly one column to the left or right of the pawn's current column.
		// - There is an opponent's piece at the target square.
		if (Math.abs(column - this.getColumn()) == 1 // One column left or right
			    && row == this.getRow() - colorIndex // One row forward
			    && getBoard().getPieceAt(column, row) != null) { // Target square contains a piece
			return true;
		}

		// Check for en passant capture.
		// En passant allows a pawn to capture an opponent's pawn that has moved two steps forward
		// from its starting position, provided the capture occurs immediately after the opponent's move.
		// To validate an en passant move:
		// - The target square must match the en passant square provided by the board.
		// - The target square must be one row forward and one column to the left or right.
		return getBoard().getTileNumber(column, row) == getBoard().getEnPassantTile() // Target is the en passant square
			       && Math.abs(column - this.getColumn()) == 1 // One column left or right
			       && row == this.getRow() - colorIndex; // One row forward
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

	@Override
	boolean inCheck(int column, int row) {
		return false;
	}
}
