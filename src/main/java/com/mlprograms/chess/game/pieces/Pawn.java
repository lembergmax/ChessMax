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
		int colorIndex = isWhite() ? 1 : -1;

		// push on 1
		if (this.getColumn() == column && row == this.getRow() - colorIndex && getBoard().getPieceAt(column, row) == null) {
			return true;
		}

		// push on 2
		if (this.getRow() == (isWhite() ? 6 : 1) && this.getColumn() == column && row == this.getRow() - colorIndex * 2 && getBoard().getPieceAt(column, row) == null && getBoard().getPieceAt(column, row + colorIndex) == null) {
			return true;
		}

		// capture to left
		if (column == this.getColumn() - 1 && row == this.getRow() - colorIndex && getBoard().getPieceAt(column, row) != null) {
			return true;
		}

		// capture to right
		if (column == this.getColumn() + 1 && row == this.getRow() - colorIndex && getBoard().getPieceAt(column, row) != null) {
			return true;
		}

		// en passant left
		if (getBoard().getTileNumber(column, row) == getBoard().getEnPassantTile() && column == this.getColumn() - 1 && row == this.getRow() - colorIndex) {
			return true;
		}

		// en passant right
		return getBoard().getTileNumber(column, row) == getBoard().getEnPassantTile() && column == this.getColumn() + 1 && row == this.getRow() - colorIndex;
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
