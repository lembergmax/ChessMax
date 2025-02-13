/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.pieces.King;
import com.mlprograms.chess.game.pieces.Pawn;
import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.utils.Sounds;
import com.mlprograms.chess.utils.Logger;
import lombok.Getter;

import java.awt.*;
import java.util.List;

import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorConfig;
import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorWithAlphaConfig;

@Getter
public class BoardPainter {

	private final Board board;

	public BoardPainter(Board board) {
		this.board = board;
	}

	/**
	 * Creates a polygon representing an arrowhead for a given end point and angle.
	 * <p>
	 * This method calculates the coordinates of a triangular arrowhead based on the specified
	 * end point (endX, endY) and the angle of the arrow. The arrowhead is created with a fixed
	 * spread angle and length, forming a triangle with the tip at the end point and the other
	 * two points calculated using trigonometric functions.
	 * </p>
	 *
	 * @param endX
	 * 	the x-coordinate of the arrow's end point
	 * @param endY
	 * 	the y-coordinate of the arrow's end point
	 * @param angle
	 * 	the angle of the arrow in radians
	 *
	 * @return a Polygon object representing the arrowhead
	 */
	private static Polygon getPolygon(int endX, int endY, double angle) {
		double arrowHeadAngle = Math.toRadians(35);  // Spread angle
		double arrowHeadLength = 35;                 // Increased from 25 to 35

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint(endX, endY);

		// Left point of the arrowhead
		int x1 = (int) (endX - arrowHeadLength * Math.cos(angle - arrowHeadAngle));
		int y1 = (int) (endY - arrowHeadLength * Math.sin(angle - arrowHeadAngle));
		arrowHead.addPoint(x1, y1);

		// Right point of the arrowhead
		int x2 = (int) (endX - arrowHeadLength * Math.cos(angle + arrowHeadAngle));
		int y2 = (int) (endY - arrowHeadLength * Math.sin(angle + arrowHeadAngle));
		arrowHead.addPoint(x2, y2);

		return arrowHead;
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
		for (Piece piece : getBoard().getPieceList()) {
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
		List<Move> possibleMoves = getBoard().getPossibleMoves();
		if (possibleMoves == null || getBoard().getSelectedPiece() == null) {
			return; // No moves or no selected piece, nothing to highlight
		}

		for (Move move : possibleMoves) {
			int column = move.getNewColumn();
			int row = move.getNewRow();

			// Determine highlight color based on the move type
			if (isCaptureMove(column, row)) {
				graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_CAPTURE", 135)); // Capture move color
			} else {
				graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT", 135));
			}

			// Draw the highlight on the corresponding tile
			graphics2D.fillRect(column * getBoard().getTileSize(), row * getBoard().getTileSize(), getBoard().getTileSize(), getBoard().getTileSize());
		}
	}

	/**
	 * Highlights the last move made on the chessboard.
	 * <p>
	 * This method retrieves the last move from the move history and highlights both the source and destination tiles
	 * using a semi-transparent color. If no moves have been made, the method returns without performing any action.
	 *
	 * @param graphics2D
	 * 	the graphics context used for drawing
	 */
	public void highlightMadeMove(Graphics2D graphics2D) {
		if (getBoard().getMoveHistory().isEmpty()) {
			return;
		}

		// Get the last move made on the board
		Move lastMove = getBoard().getMoveHistory().getLast().getMadeMove();

		// Set the highlight color based on the move type
		graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_MOVE_FROM_TO", 135));

		// Draw the highlight on the source tile
		graphics2D.fillRect(lastMove.getOldColumn() * getBoard().getTileSize(), lastMove.getOldRow() * getBoard().getTileSize(), getBoard().getTileSize(), getBoard().getTileSize());
		graphics2D.fillRect(lastMove.getNewColumn() * getBoard().getTileSize(), lastMove.getNewRow() * getBoard().getTileSize(), getBoard().getTileSize(), getBoard().getTileSize());
	}

	/**
	 * Blinks the tile of the specified king (white or black) to indicate an illegal move.
	 * <p>
	 * This method finds the king's position on the board and creates a new thread to blink the tile
	 * for a total duration of 1.2 seconds. The tile is highlighted with a semi-transparent color,
	 * and an illegal move sound is played. The blinking effect is achieved by alternating between
	 * filling the tile with the highlight color and repainting the board.
	 *
	 * @param graphics2D
	 * 	the graphics context used for drawing
	 * @param whiteKing
	 * 	true if the white king's tile should be blinked, false for the black king
	 */
	public void blinkKingsTile(Graphics2D graphics2D, boolean whiteKing) {
		King king = getBoard().getMoveValidator().findKing(whiteKing);
		int column = king.getColumn();
		int row = king.getRow();

		// Let the king's tile blink a few times
		new Thread(() -> {
			graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_ILLEGAL", 105));

			getBoard().getSoundPlayer().play(Sounds.ILLEGAL_MOVE);

			for (int i = 0; i < 3; i++) {
				sleep(400);
				graphics2D.fillRect(column * getBoard().getTileSize(), row * getBoard().getTileSize(), getBoard().getTileSize(), getBoard().getTileSize());
				paintPieces(graphics2D);
				sleep(400);
				getBoard().repaint();
			}
		}).start();
	}

	/**
	 * Pauses the current thread for the specified number of milliseconds.
	 *
	 * @param millis
	 * 	the duration in milliseconds for which the thread should sleep
	 */
	private void sleep(int millis) {
		try {
			Thread.sleep(millis);
		} catch (InterruptedException e) {
			Logger.logError(e.getMessage());
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
		Piece targetPiece = getBoard().getPieceAt(column, row);

		// Check for direct captures or special en passant moves
		return targetPiece != null ||
			       (getBoard().getSelectedPiece() instanceof Pawn && getBoard().getTileNumber(column, row) == getBoard().getEnPassantTile());
	}

	/**
	 * Draws coordinates around the chessboard for player reference.
	 * <p>
	 * This method draws column labels ('a' to 'h' or 'h' to 'a') below the board
	 * and row labels ('1' to '8' or '8' to '1') to the left of the board,
	 * depending on the orientation.
	 */
	protected void drawCoordinates(Graphics2D graphics2D) {
		// Set the font and color for the coordinates
		graphics2D.setColor(Color.BLACK);
		graphics2D.setFont(new Font("SansSerif", Font.BOLD, 12));

		boolean isWhiteAtBottom = getBoard().isWhiteAtBottom();
		drawColumnLabels(graphics2D, isWhiteAtBottom);
		drawRowLabels(graphics2D, isWhiteAtBottom);
	}

	/**
	 * Draws the column labels ('a' to 'h' or 'h' to 'a') below the board.
	 */
	private void drawColumnLabels(Graphics2D graphics2D, boolean isWhiteAtBottom) {
		int tileSize = getBoard().getTileSize();
		int padding = getBoard().getPadding();
		int boardRows = getBoard().getRows();
		int boardColumns = getBoard().getColumns();

		for (int col = 0; col < boardColumns; col++) {
			char colLabel = (char) ((isWhiteAtBottom ? 'a' : 'h') + (isWhiteAtBottom ? col : -col));
			int xPos = col * tileSize + tileSize - graphics2D.getFontMetrics().stringWidth(String.valueOf(colLabel)) / 2 - padding / 4;
			int yPos = boardRows * tileSize - (padding / 4) / 2;

			graphics2D.drawString(String.valueOf(colLabel), xPos, yPos);
		}
	}

	/**
	 * Draws the row labels ('1' to '8' or '8' to '1') to the left of the board.
	 */
	private void drawRowLabels(Graphics2D graphics2D, boolean isWhiteAtBottom) {
		int tileSize = getBoard().getTileSize();
		int padding = getBoard().getPadding();
		int boardRows = getBoard().getRows();

		for (int row = 0; row < boardRows; row++) {
			int rowLabel = isWhiteAtBottom ? (boardRows - row) : (row + 1);
			int xPos = padding / 7;
			int yPos = row * tileSize + padding / 3 + graphics2D.getFontMetrics().getHeight() / 2 - 3;

			graphics2D.drawString(String.valueOf(rowLabel), xPos, yPos);
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
		for (int rows = 0; rows < getBoard().getRows(); rows++) {
			// Loop through each column of the board
			for (int columns = 0; columns < getBoard().getColumns(); columns++) {
				// Alternate between light and dark colors based on tile position
				if ((rows + columns) % 2 == 0) {
					graphics2D.setColor(fetchColorConfig("Colors", "TILE_LIGHT")); // Light tile color
				} else {
					graphics2D.setColor(fetchColorConfig("Colors", "TILE_DARK")); // Dark tile color
				}
				// Draw the rectangle representing the tile
				graphics2D.fillRect(columns * getBoard().getTileSize(), rows * getBoard().getTileSize(),
					getBoard().getTileSize(), getBoard().getTileSize());
			}
		}
	}

	/**
	 * Draws an arrow with a style similar to Chess.com:
	 * - Thicker, more transparent line
	 * - Larger, filled triangular arrowhead
	 *
	 * @param g2d
	 * 	the Graphics2D context used for drawing
	 * @param startX
	 * 	x-coordinate of the arrow's start
	 * @param startY
	 * 	y-coordinate of the arrow's start
	 * @param endX
	 * 	x-coordinate of the arrow's end
	 * @param endY
	 * 	y-coordinate of the arrow's end
	 */
	private void drawArrow(Graphics2D g2d, int startX, int startY, int endX, int endY) {
		Stroke oldStroke = g2d.getStroke();
		Color oldColor = g2d.getColor();

		float arrowThickness = 20.0f;
		g2d.setStroke(new BasicStroke(
			arrowThickness,
			BasicStroke.CAP_SQUARE,
			BasicStroke.JOIN_MITER
		));
		g2d.setColor(new Color(255, 165, 0, 255)); // Orange

		// Winkel des Pfeils berechnen
		double angle = Math.atan2(endY - startY, endX - startX);

		// --- 1) Pfeilschaft verkürzen ---
		// Länge der Pfeilspitze (muss zur Berechnung bekannt sein)
		double arrowHeadLength = 35.0;
		// Neue "Linienendpunkte" berechnen, sodass die Spitze frei bleibt
		int lineEndX = (int) (endX - arrowHeadLength * Math.cos(angle));
		int lineEndY = (int) (endY - arrowHeadLength * Math.sin(angle));

		// --- 2) Zuerst den verkürzten Schaft zeichnen ---
		g2d.drawLine(startX, startY, lineEndX, lineEndY);

		// --- 3) Dann Pfeilspitze am tatsächlichen Endpunkt ---
		Polygon arrowHead = getPolygon(endX, endY, angle);
		g2d.fillPolygon(arrowHead);

		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}


	/**
	 * Draws all arrows stored on the board in a Chess.com-like style.
	 *
	 * @param g2d
	 * 	the Graphics2D context used for drawing
	 */
	public void drawArrows(Graphics2D g2d) {
		// For each arrow in your board's list
		for (Arrow arrow : board.getArrows()) {
			if (!isValidArrow(arrow)) {
				continue;
			}

			drawSingleArrow(g2d, arrow);
		}

		// Draw the temporary arrow (if it exists) using a dashed stroke or similar
		if (board.getTempArrow() != null) {
			// For the temporary arrow, you might want a dashed stroke or a different color.
			// But if you want it to look the same, just call the same method:
			drawSingleArrow(g2d, board.getTempArrow());
		}
	}

	private boolean isValidArrow(Arrow arrow) {
		return arrow.getEndColumn() >= 0 && arrow.getEndColumn() <= 7 && arrow.getEndRow() >= 0 && arrow.getEndRow() <= 7;
	}

	private void drawSingleArrow(Graphics2D g2d, Arrow arrow) {
		int tileSize = board.getTileSize();
		int startX = arrow.getStartColumn() * tileSize + tileSize / 2;
		int startY = arrow.getStartRow() * tileSize + tileSize / 2;
		int endX = arrow.getEndColumn() * tileSize + tileSize / 2;
		int endY = arrow.getEndRow() * tileSize + tileSize / 2;

		// Prüfe, ob es sich um einen Springerzug handelt
		int dx = Math.abs(arrow.getEndColumn() - arrow.getStartColumn());
		int dy = Math.abs(arrow.getEndRow() - arrow.getStartRow());
		if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
			// Bei Springerzug L-Shape Pfeil zeichnen
			// Wir wählen hier: wenn der horizontale Unterschied größer ist, zeichne zuerst horizontal
			boolean horizontalFirst = (dx > dy);
			drawKnightArrow(g2d, startX, startY, endX, endY, horizontalFirst);
		} else {
			// Andernfalls normaler Pfeil
			drawArrow(g2d, startX, startY, endX, endY);
		}
	}

	private void drawKnightArrow(Graphics2D g2d, int startX, int startY, int endX, int endY, boolean horizontalFirst) {
		Stroke oldStroke = g2d.getStroke();
		Color oldColor = g2d.getColor();

		float arrowThickness = 20.0f;
		g2d.setStroke(new BasicStroke(
			arrowThickness,
			BasicStroke.CAP_SQUARE,
			BasicStroke.JOIN_MITER
		));
		g2d.setColor(new Color(255, 165, 0, 255));

		// Zwischenpunkt ermitteln
		int midX, midY;
		if (horizontalFirst) {
			midX = endX;
			midY = startY;
		} else {
			midX = startX;
			midY = endY;
		}

		// 1) Ersten (vertikalen oder horizontalen) Abschnitt zeichnen
		g2d.drawLine(startX, startY, midX, midY);

		// --- Ab hier: Letzter Abschnitt + Pfeilspitze ---
		double arrowHeadLength = 35.0;

		// 2) Winkel für das letzte Segment
		double angle = Math.atan2(endY - midY, endX - midX);

		// 3) Linie verkürzen, damit die Pfeilspitze nicht überdeckt wird
		int lineEndX = (int) (endX - arrowHeadLength * Math.cos(angle));
		int lineEndY = (int) (endY - arrowHeadLength * Math.sin(angle));

		// 4) Zweiten Abschnitt (verkürzt) zeichnen
		g2d.drawLine(midX, midY, lineEndX, lineEndY);

		// 5) Pfeilspitze am eigentlichen Endpunkt
		Polygon arrowHead = getPolygon(endX, endY, angle);
		g2d.fillPolygon(arrowHead);

		// Alte Einstellungen wiederherstellen
		g2d.setStroke(oldStroke);
		g2d.setColor(oldColor);
	}


}
