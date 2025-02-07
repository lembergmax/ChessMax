package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.ui.Board;

import java.awt.image.BufferedImage;

public class Queen extends Piece {

	public Queen(Board board, int column, int row, boolean isWhite) {
		super(board);

		setColumn(column);
		setRow(row);
		setXPos(column * getBoard().getTileSize());
		setYPos(row * getBoard().getTileSize());

		setPieceValue(9);
		setWhite(isWhite);
		setName("Queen");
		setFenChar(isWhite ? 'Q' : 'q');

		int sheetScale = getSheetScale();
		setSprite(getSheet().getSubimage(sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	@Override
	public boolean isValidMovement(int targetColumn, int targetRow, boolean checkForKingSafety) {
		if (!isValidPieceMove(targetColumn, targetRow, checkForKingSafety)) {
			return false;
		}

		return getColumn() == targetColumn || getRow() == targetRow || Math.abs(getColumn() - targetColumn) == Math.abs(getRow() - targetRow);
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		if (getColumn() == column || getRow() == row) {
			return linearCollision(column, row);
		} else {
			return diagonalCollision(column, row);
		}
	}

}
