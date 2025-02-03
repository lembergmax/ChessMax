/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.utils.Logger;

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
	public boolean isValidMovement(int targetColumn, int targetRow, boolean checkForKingSafety) {
		if (!isValidPieceMove(targetColumn, targetRow, checkForKingSafety)) {
			return false;
		}

		int direction = isWhite() == getBoard().isWhiteAtBottom() ? 1 : -1;
		boolean isWhiteAtBottom = getBoard().isWhiteAtBottom();

		// push on 1
		if (getColumn() == targetColumn && targetRow == getRow() - direction && getBoard().getPieceAt(targetColumn, targetRow) == null) {
			return true;
		}

		Logger.logDebug(getBoard().getEnPassantTile());

		// push on 2
		if (getRow() == (isWhiteAtBottom ? (isWhite() ? 6 : 1) : (isWhite() ? 1 : 6)) && getColumn() == targetColumn && targetRow == getRow() - direction * 2 &&
				getBoard().getPieceAt(targetColumn, targetRow) == null && getBoard().getPieceAt(targetColumn, targetRow + direction) == null) {
			return true;
		}

		// capture to left
		if (targetColumn == getColumn() - 1 && targetRow == getRow() - direction && getBoard().getPieceAt(targetColumn, targetRow) != null) {
			return true;
		}

		// capture to right
		if (targetColumn == getColumn() + 1 && targetRow == getRow() - direction && getBoard().getPieceAt(targetColumn, targetRow) != null) {
			return true;
		}

		// en passant left
		if (getBoard().getTileNumber(targetColumn, targetRow) == getBoard().getEnPassantTile() && targetColumn == getColumn() - 1 && targetRow == getRow() - direction) {
			return true;
		}

		// en passant right
		return getBoard().getTileNumber(targetColumn, targetRow) == getBoard().getEnPassantTile() && targetColumn == getColumn() + 1 && targetRow == getRow() - direction;
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

}
