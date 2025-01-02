/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

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
		setFenChar(isWhite ? 'P' : 'p');

		int sheetScale = getSheetScale();
		setSprite(getSheet().getSubimage(5 * sheetScale, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	@Override
	public boolean isValidMovement(int column, int row, boolean checkForKingSafety) {
		if(!isValidPieceMove(column, row, checkForKingSafety)) {
			return false;
		}

		int colorIndex = isWhite() ? 1 : -1;

		// push on 1
		if (getColumn() == column && row == getRow() - colorIndex && getBoard().getPieceAt(column, row) == null) {
			return true;
		}

		// push on 2
		if (getRow() == (isWhite() ? 6 : 1) && getColumn() == column && row == getRow() - colorIndex * 2 && getBoard().getPieceAt(column, row) == null && getBoard().getPieceAt(column, row + colorIndex) == null) {
			return true;
		}

		// capture to left
		if (column == getColumn() - 1 && row == getRow() - colorIndex && getBoard().getPieceAt(column, row) != null) {
			return true;
		}

		// capture to right
		if (column == getColumn() + 1 && row == getRow() - colorIndex && getBoard().getPieceAt(column, row) != null) {
			return true;
		}

		// en passant left
		if (getBoard().getTileNumber(column, row) == getBoard().getEnPassantTile() && column == getColumn() - 1 && row == getRow() - colorIndex) {
			return true;
		}

		// en passant right
		return getBoard().getTileNumber(column, row) == getBoard().getEnPassantTile() && column == getColumn() + 1 && row == getRow() - colorIndex;
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

}
