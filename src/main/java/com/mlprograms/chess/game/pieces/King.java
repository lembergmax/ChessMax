package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.ui.Board;

import java.awt.image.BufferedImage;

public class King extends Piece {

	public King(Board board, int column, int row, boolean isWhite) {
		super(board);

		setColumn(column);
		setRow(row);
		setXPos(column * getBoard().getTileSize());
		setYPos(row * getBoard().getTileSize());

		setWhite(isWhite);
		setName("King");

		int sheetScale = getSheetScale();
		setSprite(getSheet().getSubimage(0, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	@Override
	public boolean isValidMovement(int column, int row) {
		if(!isValidPieceMove(column, row)) {
			return false;
		}

		return false;
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

}
