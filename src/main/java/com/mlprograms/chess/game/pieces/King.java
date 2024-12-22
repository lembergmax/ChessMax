package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.action.Move;
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
		if (!isValidPieceMove(column, row)) {
			return false;
		}

		// Check the normal king movements (1 square in any direction)
		if (Math.abs(column - this.getColumn()) <= 1 && Math.abs(row - this.getRow()) <= 1) {
			return true;
		}

		// Check if castling is possible (king moves 2 squares)
		// if (Math.abs(column - this.getColumn()) == 2 && this.getRow() == row && canCastle(column, row)) {
		// return true;
		// }

		return false;
	}

	public boolean canCastle(int column, int row) {
		// Check if the king is moving on the same row
		if (this.getRow() == row && this.isFirstMove() && !getBoard().getCheckScanner().wouldMovePutKingInCheck(new Move(getBoard(), this, this.getColumn(), this.getRow()))) {
			Piece rook;

			// Short castling (kingside)
			if (column == 6) {
				rook = getBoard().getPieceAt(7, row);

				// Check if the rook exists, has the same color, is its first move, and all squares in between are empty
				if (rook instanceof Rook && rook.isWhite() == this.isWhite() && rook.isFirstMove()) {
					return getBoard().getPieceAt(5, row) == null &&
						       getBoard().getPieceAt(6, row) == null &&
						       !getBoard().getCheckScanner().wouldMovePutKingInCheck(new Move(getBoard(), this, 5, row)) && // Ensure the king is not in check when passing through square 5
						       !getBoard().getCheckScanner().wouldMovePutKingInCheck(new Move(getBoard(), this, 6, row));  // Ensure the destination square is not under threat
				}
			}

			// Long castling (queenside)
			else if (column == 2) {
				rook = getBoard().getPieceAt(0, row);

				// Check if the rook exists, has the same color, is its first move, and all squares in between are empty
				if (rook instanceof Rook && rook.isWhite() == this.isWhite() && rook.isFirstMove()) {
					return getBoard().getPieceAt(1, row) == null &&
						       getBoard().getPieceAt(2, row) == null &&
						       getBoard().getPieceAt(3, row) == null &&
						       !getBoard().getCheckScanner().wouldMovePutKingInCheck(new Move(getBoard(), this, 3, row)) && // Ensure the king is not in check when passing through square 3
						       !getBoard().getCheckScanner().wouldMovePutKingInCheck(new Move(getBoard(), this, 2, row));  // Ensure the destination square is not under threat
				}
			}
		}

		return false;
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

}
