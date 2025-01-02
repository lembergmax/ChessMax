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
		setFenChar(isWhite ? 'K' : 'k');

		int sheetScale = getSheetScale();
		setSprite(getSheet().getSubimage(0, isWhite ? 0 : sheetScale, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	@Override
	public boolean isValidMovement(int column, int row, boolean checkForKingSafety) {
		if (!isValidPieceMove(column, row, checkForKingSafety)) {
			return false;
		}

		// Check the normal king movements (1 square in any direction)
		if (Math.abs(column - getColumn()) <= 1 && Math.abs(row - getRow()) <= 1) {
			return true;
		}

		// Check if castling is possible (king moves 2 squares)
		return Math.abs(column - getColumn()) == 2 && getRow() == row && canCastle(column, row);
	}

	public boolean canCastle(int column, int row) {
		// Check if the king is moving on the same row
		if (getRow() == row && isFirstMove() /*&& !getBoard().getMoveValidator().wouldMovePutKingInCheck(new Move(getBoard(), this, getColumn(), getRow()))*/) {
			Piece rook;

			// Short castling (kingside)
			if (column == 6) {
				rook = getBoard().getPieceAt(7, row);

				// Check if the rook exists, has the same color, is its first move, and all squares in between are empty
				if (rook instanceof Rook && rook.isWhite() == isWhite() && rook.isFirstMove()) {
					return getBoard().getPieceAt(5, row) == null &&
						       getBoard().getPieceAt(6, row) == null &&

						       !targetsEnemyPieceSpecificTile(5, row) &&
						       !targetsEnemyPieceSpecificTile(6, row);
				}
			}

			// Long castling (queenside)
			else if (column == 2) {
				rook = getBoard().getPieceAt(0, row);

				// Check if the rook exists, has the same color, is its first move, and all squares in between are empty
				if (rook instanceof Rook && rook.isWhite() == isWhite() && rook.isFirstMove()) {
					return getBoard().getPieceAt(1, row) == null &&
						       getBoard().getPieceAt(2, row) == null &&
						       getBoard().getPieceAt(3, row) == null &&

						       !targetsEnemyPieceSpecificTile(3, row) &&
						       !targetsEnemyPieceSpecificTile(2, row);
				}
			}
		}

		return false;
	}

	private boolean targetsEnemyPieceSpecificTile(int column, int row) {
		return getBoard().getPieceList().stream().anyMatch(piece -> piece.isWhite() != isWhite() && piece.isValidMovement(column, row, false));
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

}
