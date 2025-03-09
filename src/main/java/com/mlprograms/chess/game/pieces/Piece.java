/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.game.utils.SpriteSheetCache;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents an abstract chess piece, providing common properties and methods for all pieces.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class Piece implements Cloneable {

	// Name of the piece, e.g., "Pawn", "Rook"
	private String name;

	// FEN character representing the piece
	private char fenChar;

	// Current column position of the piece on the board
	private int column;

	// Current row position of the piece on the board
	private int row;

	// Pixel-based X position for rendering
	private int xPos;

	// Pixel-based Y position for rendering
	private int yPos;

	// Value of the piece, useful for evaluation functions
	private int pieceValue;

	// Reference to the UI representation of the board
	private Board board;

	// Indicates whether the piece belongs to the white player
	private boolean isWhite;

	// Indicates whether this piece has moved yet
	private boolean isFirstMove = true;

	// Scale factor for the sprite sheet
	private int sheetScale;

	// The sprite sheet containing the piece image
	private BufferedImage sheet;

	// The specific sprite for this piece
	private Image sprite;

	/**
	 * Constructor for initializing a chess piece. Loads the sprite sheet for piece images.
	 *
	 * @param board
	 * 	the board to which this piece belongs
	 */
	public Piece(Board board) {
		this.board = board;
		setPieceValue(0);

		// Load the sprite sheet for the chess pieces
		setSheet(SpriteSheetCache.getPiecesSpriteSheet());
		// Calculate the scale factor based on the sprite sheet dimensions
		setSheetScale(getSheet().getWidth() / 6);
	}

	/**
	 * Determines whether the piece can move to a specific position.
	 *
	 * @param column
	 * 	the target column
	 * @param row
	 * 	the target row
	 *
	 * @return true if the movement is valid, false otherwise
	 */
	public boolean isValidMovement(int column, int row) {
		return isValidMovement(column, row, true);
	}

	/**
	 * Determines whether the piece can move to a specific position.
	 *
	 * @param targetColumn
	 * 	the target targetColumn
	 * @param targetRow
	 * 	the target targetRow
	 * @param checkForKingSafety
	 * 	whether to check if the move puts the king in check
	 *
	 * @return true if the movement is valid, false otherwise
	 */
	public abstract boolean isValidMovement(int targetColumn, int targetRow, boolean checkForKingSafety);

	/**
	 * Checks if the movement to the target position collides with another piece. Only needed for Bishop, Rook, and
	 * Queen.
	 *
	 * @param column
	 * 	the target column
	 * @param row
	 * 	the target row
	 *
	 * @return true if a collision occurs, false otherwise
	 */
	public abstract boolean moveCollidesWithPiece(int column, int row);

	/**
	 * Checks if the target position is a valid move for the piece.
	 *
	 * @param column
	 * 	the target column
	 * @param row
	 * 	the target row
	 *
	 * @return true if the move is valid, false otherwise
	 */
	protected boolean isValidPieceMove(int column, int row, boolean checkForKingSafety) {
		if (column < 0 || column >= getBoard().getColumns() || row < 0 || row >= getBoard().getRows()) {
			return false;
		}

		if (this.getColumn() == column && this.getRow() == row) {
			return false;
		}

		if (this.getBoard().sameTeam(this, getBoard().getPieceAt(column, row))) {
			return false;
		}

		if (moveCollidesWithPiece(column, row)) {
			return false;
		}

		if (checkForKingSafety) {
			return !getBoard().getMoveValidator().wouldMovePutKingInCheck(new Move(getBoard(), this, column, row));
		}

		return true;
	}

	/**
	 * Retrieves a list of all legal moves for this piece on the given board.
	 *
	 * @param board
	 * 	the board to evaluate moves on
	 *
	 * @return a list of legal moves; an empty list if none exist
	 */
	public List<Move> getLegalMoves(Board board) {
		List<Move> legalMoves = new ArrayList<>();

		for (int col = 0; col < board.getColumns(); col++) {
			for (int row = 0; row < board.getRows(); row++) {
				Move move = new Move(board, this, col, row);
				if (isLegalMove(board, move)) {
					legalMoves.add(move);
				}
			}
		}

		return legalMoves;
	}

	/**
	 * Checks if a move is legal, i.e., it does not put the king in check.
	 *
	 * @param board
	 * 	the board to evaluate the move on
	 * @param move
	 * 	the move to validate
	 *
	 * @return true if the move is legal; false otherwise
	 */
	private boolean isLegalMove(Board board, Move move) {
		return !board.getMoveValidator().wouldMovePutKingInCheck(move) && isValidMovement(move.getNewColumn(), move.getNewRow());
	}

	/**
	 * Sets the position of the piece on the board.
	 * Updates the pixel-based coordinates and repaints the board.
	 *
	 * @param column
	 * 	the new column position
	 * @param row
	 * 	the new row position
	 */
	public void setPosition(int column, int row) {
		this.column = column;
		this.row = row;
		this.xPos = column * board.getTileSize();
		this.yPos = row * board.getTileSize();

		// Trigger a repaint of the board to reflect the new position
		board.repaint();
	}

	/**
	 * Renders the piece on the board using its sprite.
	 *
	 * @param graphics2D
	 * 	the graphics context used for rendering
	 */
	public void paint(Graphics2D graphics2D) {
		// Draw the sprite of the piece at its current position
		graphics2D.drawImage(sprite, xPos, yPos, board.getTileSize(), board.getTileSize(), null);
	}

	/**
	 * Checks for collisions along the diagonal path between the current position
	 * and the target position. Iterates over all tiles on the diagonal to ensure
	 * no pieces block the path.
	 *
	 * @param column
	 * 	the target column
	 * @param row
	 * 	the target row
	 *
	 * @return true if a collision is detected along the diagonal, false otherwise
	 */
	protected boolean diagonalCollision(int column, int row) {
		// up left
		if (this.getColumn() > column && this.getRow() > row) {
			for (int i = 1; i < Math.abs(this.getColumn() - column); i++) {
				if (getBoard().getPieceAt(this.getColumn() - i, this.getRow() - i) != null) {
					return true;
				}
			}
		}

		// up right
		if (this.getColumn() < column && this.getRow() > row) {
			for (int i = 1; i < Math.abs(this.getColumn() - column); i++) {
				if (getBoard().getPieceAt(this.getColumn() + i, this.getRow() - i) != null) {
					return true;
				}
			}
		}

		// down left
		if (this.getColumn() > column && this.getRow() < row) {
			for (int i = 1; i < Math.abs(this.getColumn() - column); i++) {
				if (getBoard().getPieceAt(this.getColumn() - i, this.getRow() + i) != null) {
					return true;
				}
			}
		}

		// down right
		if (this.getColumn() < column && this.getRow() < row) {
			for (int i = 1; i < Math.abs(this.getColumn() - column); i++) {
				if (getBoard().getPieceAt(this.getColumn() + i, this.getRow() + i) != null) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * Checks for collisions along the linear path (horizontal or vertical)
	 * between the current position and the target position. Iterates over all
	 * tiles in the path to ensure no pieces block the way.
	 *
	 * @param column
	 * 	the target column
	 * @param row
	 * 	the target row
	 *
	 * @return true if a collision is detected along the linear path, false otherwise
	 */
	protected boolean linearCollision(int column, int row) {
		// left
		if (this.getColumn() > column) {
			for (int c = this.getColumn() - 1; c > column; c--) {
				if (getBoard().getPieceAt(c, this.getRow()) != null) {
					return true;
				}
			}
		}

		// right
		if (this.getColumn() < column) {
			for (int c = this.getColumn() + 1; c < column; c++) {
				if (getBoard().getPieceAt(c, this.getRow()) != null) {
					return true;
				}
			}
		}

		// up
		if (this.getRow() > row) {
			for (int r = this.getRow() - 1; r > row; r--) {
				if (getBoard().getPieceAt(this.getColumn(), r) != null) {
					return true;
				}
			}
		}

		// down
		if (this.getRow() < row) {
			for (int r = this.getRow() + 1; r < row; r++) {
				if (getBoard().getPieceAt(this.getColumn(), r) != null) {
					return true;
				}
			}
		}

		return false;
	}

	@Override
	public Piece clone() {
		try {
			return (Piece) super.clone();
		} catch (CloneNotSupportedException e) {
			throw new AssertionError();
		}
	}

}
