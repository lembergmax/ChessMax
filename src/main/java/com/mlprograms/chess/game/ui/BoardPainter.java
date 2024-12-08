/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import com.mlprograms.chess.game.action.Move;
import com.mlprograms.chess.game.pieces.Pawn;
import com.mlprograms.chess.game.pieces.Piece;

import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorConfig;
import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorWithAlphaConfig;

public class BoardPainter {

	private final Board BOARD;

	public BoardPainter(Board board) {
		this.BOARD = board;
	}

	/**
	 * Paints all the pieces currently on the board.
	 * Iterates through the list of pieces managed by the board and calls their respective paint methods.
	 *
	 * @param graphics2D
	 * 	the Graphics2D object used to render the pieces on the board
	 */
	public void paintPieces(Graphics2D graphics2D) {
		// Iterate through all pieces on the board
		for (Piece piece : BOARD.getPieceList()) {
			// Delegate the painting of each piece to its paint method
			piece.paint(graphics2D);
		}
	}

	/**
	 * Highlights all possible moves for the currently selected piece on the board.
	 *
	 * @param graphics2D
	 * 	the graphics context used for drawing
	 */
	public void highlightPossibleMoves(Graphics2D graphics2D) {
		if (BOARD.getPossibleMoves() == null || BOARD.getSelectedPiece() == null) {
			return; // No moves or no selected piece, nothing to highlight
		}

		for (Move move : BOARD.getPossibleMoves()) {
			int column = move.getNewColumn();
			int row = move.getNewRow();

			// Determine highlight color based on the move type
			if (isCaptureMove(column, row)) {
				graphics2D.setColor(fetchColorWithAlphaConfig("TILE_HIGHLIGHT_CAPTURE", 135)); // Capture move color
			} else {
				graphics2D.setColor(fetchColorWithAlphaConfig("TILE_HIGHLIGHT", 135));
			}

			// Draw the highlight on the corresponding tile
			graphics2D.fillRect(column * BOARD.getTileSize(), row * BOARD.getTileSize(), BOARD.getTileSize(), BOARD.getTileSize());
		}
	}

	/**
	 * Checks if the move to the given tile is a capture move.
	 *
	 * @param column
	 * 	the target column of the move
	 * @param row
	 * 	the target row of the move
	 *
	 * @return true if the move is a capture, false otherwise
	 */
	private boolean isCaptureMove(int column, int row) {
		Piece targetPiece = BOARD.getPieceAt(column, row);

		// Check for direct captures or special en passant moves
		return targetPiece != null ||
			       (BOARD.getSelectedPiece() instanceof Pawn && BOARD.getTileNumber(column, row) == BOARD.getEnPassantTile());
	}

	/**
	 * Draws coordinates around the chessboard for player reference.
	 * <p>
	 * This method draws column labels ('a' to 'h') below the board and row labels ('1' to '8')
	 * to the left of the board. It ensures the labels are positioned correctly based on the
	 * board's dimensions and tile size.
	 */
	protected void drawCoordinates(Graphics2D graphics2D) {
		// Set the font and color for the coordinates
		graphics2D.setColor(Color.BLACK);
		graphics2D.setFont(new Font("SansSerif", Font.BOLD, 12));

		// Draw column labels (a-h) below the board
		for (int col = 0; col < BOARD.getColumns(); col++) {
			String colLabel = String.valueOf((char) ('a' + col)); // Convert column index to character
			int xPos = col * BOARD.getTileSize() + BOARD.getTileSize()
				           - graphics2D.getFontMetrics().stringWidth(colLabel) / 2 - BOARD.getPadding() / 4;
			int yPos = BOARD.getRows() * BOARD.getTileSize()
				           - (BOARD.getPadding() / 4) / 2; // Slightly below the board
			graphics2D.drawString(colLabel, xPos, yPos);
		}

		// Draw row labels (1-8) to the left of the board
		for (int row = 0; row < BOARD.getRows(); row++) {
			String rowLabel = String.valueOf(BOARD.getRows() - row); // Convert row index to number
			int xPos = BOARD.getPadding() / 7; // Slightly left of the board
			int yPos = row * BOARD.getTileSize() + BOARD.getPadding() / 3
				           + graphics2D.getFontMetrics().getHeight() / 2 - 3;
			graphics2D.drawString(rowLabel, xPos, yPos);
		}
	}

	/**
	 * Draws the chessboard on the provided graphics context.
	 * <p>
	 * This method iterates through each tile on the board and paints it with the
	 * appropriate light or dark color based on its position. The colors are fetched
	 * from the configuration settings.
	 */
	protected void drawChessBoard(Graphics2D graphics2D) {
		// Loop through each row of the board
		for (int rows = 0; rows < BOARD.getRows(); rows++) {
			// Loop through each column of the board
			for (int columns = 0; columns < BOARD.getColumns(); columns++) {
				// Alternate between light and dark colors based on tile position
				if ((rows + columns) % 2 == 0) {
					graphics2D.setColor(fetchColorConfig("TILE_LIGHT")); // Light tile color
				} else {
					graphics2D.setColor(fetchColorConfig("TILE_DARK")); // Dark tile color
				}
				// Draw the rectangle representing the tile
				graphics2D.fillRect(columns * BOARD.getTileSize(), rows * BOARD.getTileSize(),
					BOARD.getTileSize(), BOARD.getTileSize());
			}
		}
	}
}
