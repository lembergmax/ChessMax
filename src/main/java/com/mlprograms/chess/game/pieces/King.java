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
	public boolean isValidMovement(int targetColumn, int targetRow, boolean checkForKingSafety) {
		if (!isValidPieceMove(targetColumn, targetRow, checkForKingSafety)) {
			return false;
		}

		// Check the normal king movements (1 square in any direction)
		if (Math.abs(targetColumn - getColumn()) <= 1 && Math.abs(targetRow - getRow()) <= 1) {
			return true;
		}

		// Check if castling is possible (king moves 2 squares)
		return Math.abs(targetColumn - getColumn()) == 2 && getRow() == targetRow && canCastle(targetColumn, targetRow) && !getBoard().getMoveValidator().isKingInCheck();
	}

	public boolean canCastle(int column, int row) {
		// Check if the king is moving on the same row and is making its first move
		if (getRow() != row || !isFirstMove()) {
			return false;
		}

		Piece rook = getRookForCastling(column, row);
		if (!(rook instanceof Rook) || rook.isWhite() != isWhite() || !rook.isFirstMove()) {
			return false;
		}

		// Check the squares between the king and rook
		return areCastlingSquaresEmpty(column, row) && !isCastlingMoveUnderAttack(column, row);
	}

	/**
	 * Retrieves the rook involved in castling based on the target column.
	 *
	 * @param column
	 * 	the target column for the king's move
	 * @param row
	 * 	the row of the king and rook
	 *
	 * @return the rook involved in castling, or null if the column is invalid
	 */
	private Piece getRookForCastling(int column, int row) {
		if (column == 6) {
			return getBoard().getPieceAt(7, row); // Kingside (Short Castling)
		} else if (column == 2) {
			return getBoard().getPieceAt(0, row); // Queenside (Long Castling)
		}
		return null; // Invalid column for castling
	}

	/**
	 * Checks if the squares between the king and rook are empty for castling.
	 *
	 * @param column
	 * 	the target column for the king's move
	 * @param row
	 * 	the row of the king and rook
	 *
	 * @return true if the squares are empty, false otherwise
	 */
	private boolean areCastlingSquaresEmpty(int column, int row) {
		if (column == 6) {
			return getBoard().getPieceAt(5, row) == null && getBoard().getPieceAt(6, row) == null;
		} else if (column == 2) {
			return getBoard().getPieceAt(1, row) == null && getBoard().getPieceAt(2, row) == null && getBoard().getPieceAt(3, row) == null;
		}
		return false;
	}

	/**
	 * Checks if the castling move would place the king under attack.
	 *
	 * @param column
	 * 	the target column for the king's move
	 * @param row
	 * 	the row of the king and rook
	 *
	 * @return true if the castling move is under attack, false otherwise
	 */
	private boolean isCastlingMoveUnderAttack(int column, int row) {
		if (column == 6) {
			return targetsEnemyPieceSpecificTile(5, row) || targetsEnemyPieceSpecificTile(6, row);
		} else if (column == 2) {
			return targetsEnemyPieceSpecificTile(2, row) || targetsEnemyPieceSpecificTile(3, row);
		}
		return false;
	}

	/**
	 * Checks if an enemy piece targets a specific tile.
	 *
	 * @param column
	 * 	the column of the tile
	 * @param row
	 * 	the row of the tile
	 *
	 * @return true if an enemy piece targets the tile, false otherwise
	 */
	private boolean targetsEnemyPieceSpecificTile(int column, int row) {
		return getBoard().getPieceList().stream().anyMatch(piece -> piece.isWhite() != isWhite() && piece.isValidMovement(column, row, false));
	}

	@Override
	public boolean moveCollidesWithPiece(int column, int row) {
		return false;
	}

}
