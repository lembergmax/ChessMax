/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.pieces;

import com.mlprograms.chess.game.action.Move;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.utils.Logger;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * Represents an abstract chess piece, providing common properties and methods for all pieces.
 */
@Getter
@Setter
@ToString
@EqualsAndHashCode
public abstract class Piece {

	// Name of the piece, e.g., "Pawn", "Rook"
	private String name;

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

		try {
			// Load the sprite sheet for the chess pieces
			setSheet(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream("/images/Chess_Pieces_Sprite.png"))));
			// Calculate the scale factor based on the sprite sheet dimensions
			setSheetScale(getSheet().getWidth() / 6);
		} catch (IOException e) {
			// Log error if the image fails to load
			Logger.logError("Error loading image: " + e.getMessage());
		} catch (NullPointerException e) {
			// Log error if the image file is not found
			Logger.logError("Image file not found: " + e.getMessage());
		}
	}

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
	protected boolean isValidPieceMove(int column, int row) {
		if (column < 0 || column >= getBoard().getColumns() || row < 0 || row >= getBoard().getRows()) {
			return false;
		}

		if (this.getColumn() == column && this.getRow() == row) {
			return false;
		}

		if (this.getBoard().sameTeam(this, getBoard().getPieceAt(column, row))) {
			return false;
		}

		return true;
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
	 * Retrieves the list of all legal moves for this piece.
	 *
	 * @param board
	 * 	the board to evaluate moves on
	 *
	 * @return a list of legal moves or null if not implemented
	 */
	public List<Move> getLegalMoves(Board board) {
		// TODO: Implement logic to calculate possible moves for this piece
		return null;
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
	public abstract boolean isValidMovement(int column, int row);

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
	 * Renders the piece on the board using its sprite.
	 *
	 * @param graphics2D
	 * 	the graphics context used for rendering
	 */
	public void paint(Graphics2D graphics2D) {
		// Draw the sprite of the piece at its current position
		graphics2D.drawImage(sprite, xPos, yPos, board.getTileSize(), board.getTileSize(), null);
	}
}
