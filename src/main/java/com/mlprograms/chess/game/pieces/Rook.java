package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.ui.Board;

import java.awt.image.BufferedImage;

public class Rook extends Piece {

	public Rook(Board board, int column, int row, boolean isWhite) {
		super(board);

		setColumn(column);
		setRow(row);
		setXPos(column * getBoard().getTileSize());
		setYPos(row * getBoard().getTileSize());

		setPieceValue(5);
		setWhite(isWhite);
		setName("Rook");
		setFenChar(isWhite ? 'R' : 'r');

		int sheetScale = getSheetScale();
		setSprite(getSheet().getSubimage(4 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	@Override
	public boolean isValidMovement(int targetColumn, int targetRow, boolean checkForKingSafety) {
		if(!isValidPieceMove(targetColumn, targetRow, checkForKingSafety)) {
			return false;
		}

		return getColumn() == targetColumn || getRow() == targetRow;
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return linearCollision(column, row);
	}

}
