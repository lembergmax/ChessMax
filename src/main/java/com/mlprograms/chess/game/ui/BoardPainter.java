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
import com.mlprograms.chess.utils.ConfigFetcher;
import com.mlprograms.chess.utils.Logger;
import lombok.Getter;

import java.awt.*;
import java.util.List;

import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorConfig;
import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorWithAlphaConfig;

@Getter
public class BoardPainter {

	private static final float HEAD_LENGTH = ConfigFetcher.fetchFloatConfig("Arrow", "HEAD_LENGTH");
	private static final double HEAD_ANGLE = ConfigFetcher.fetchFloatConfig("Arrow", "HEAD_ANGLE");
	private final Board BOARD;
	private final int ALPHA = ConfigFetcher.fetchIntegerConfig("Colors", "ARROW_ALPHA");
	private final Color ARROW_COLOR = ConfigFetcher.fetchColorWithAlphaConfig("Colors", "ARROW_COLOR", ALPHA);
	private final float THICKNESS = ConfigFetcher.fetchFloatConfig("Arrow", "THICKNESS");

	public BoardPainter(Board board) {
		this.BOARD = board;
	}

	/**
	 * Generates a triangular polygon that represents an arrowhead.
	 * <p>
	 * The arrowhead is constructed based on the given endpoint (endX, endY) and the angle
	 * of the arrow. Two additional points are calculated using a fixed spread angle and length,
	 * forming a triangle with its tip at the endpoint.
	 * </p>
	 *
	 * @param endX
	 * 	the x-coordinate of the arrow's endpoint.
	 * @param endY
	 * 	the y-coordinate of the arrow's endpoint.
	 * @param angle
	 * 	the angle of the arrow in radians.
	 *
	 * @return a Polygon object representing the arrowhead.
	 */
	private static Polygon getPolygon(int endX, int endY, double angle) {
		double arrowHeadAngle = Math.toRadians(HEAD_ANGLE); // Spread angle for the arrowhead.
		double arrowHeadLength = Double.parseDouble(String.valueOf(HEAD_LENGTH)); // Length of the arrowhead.

		Polygon arrowHead = new Polygon();
		arrowHead.addPoint(endX, endY); // Tip of the arrowhead.

		// Calculate the left point of the arrowhead.
		int x1 = (int) (endX - arrowHeadLength * Math.cos(angle - arrowHeadAngle));
		int y1 = (int) (endY - arrowHeadLength * Math.sin(angle - arrowHeadAngle));
		arrowHead.addPoint(x1, y1);

		// Calculate the right point of the arrowhead.
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
		for (Piece piece : getBOARD().getPieceList()) {
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
		List<Move> possibleMoves = getBOARD().getPossibleMoves();
		if (possibleMoves == null || getBOARD().getSelectedPiece() == null) {
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
			graphics2D.fillRect(column * getBOARD().getTileSize(), row * getBOARD().getTileSize(), getBOARD().getTileSize(), getBOARD().getTileSize());
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
		if (getBOARD().getMoveHistory().isEmpty()) {
			return;
		}

		// Get the last move made on the board
		Move lastMove = getBOARD().getMoveHistory().getLast().getMadeMove();

		// Set the highlight color based on the move type
		graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_MOVE_FROM_TO", 135));

		// Draw the highlight on the source tile
		graphics2D.fillRect(lastMove.getOldColumn() * getBOARD().getTileSize(), lastMove.getOldRow() * getBOARD().getTileSize(), getBOARD().getTileSize(), getBOARD().getTileSize());
		graphics2D.fillRect(lastMove.getNewColumn() * getBOARD().getTileSize(), lastMove.getNewRow() * getBOARD().getTileSize(), getBOARD().getTileSize(), getBOARD().getTileSize());
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
		King king = getBOARD().getMoveValidator().findKing(whiteKing);
		int column = king.getColumn();
		int row = king.getRow();

		// Let the king's tile blink a few times
		new Thread(() -> {
			graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_ILLEGAL", 105));

			getBOARD().getSoundPlayer().play(Sounds.ILLEGAL_MOVE);

			for (int i = 0; i < 3; i++) {
				sleep(400);
				graphics2D.fillRect(column * getBOARD().getTileSize(), row * getBOARD().getTileSize(), getBOARD().getTileSize(), getBOARD().getTileSize());
				paintPieces(graphics2D);
				sleep(400);
				getBOARD().repaint();
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
		Piece targetPiece = getBOARD().getPieceAt(column, row);

		// Check for direct captures or special en passant moves
		return targetPiece != null ||
			       (getBOARD().getSelectedPiece() instanceof Pawn && getBOARD().getTileNumber(column, row) == getBOARD().getEnPassantTile());
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

		boolean isWhiteAtBottom = getBOARD().isWhiteAtBottom();
		drawColumnLabels(graphics2D, isWhiteAtBottom);
		drawRowLabels(graphics2D, isWhiteAtBottom);
	}

	/**
	 * Draws the column labels ('a' to 'h' or 'h' to 'a') below the board.
	 */
	private void drawColumnLabels(Graphics2D graphics2D, boolean isWhiteAtBottom) {
		int tileSize = getBOARD().getTileSize();
		int padding = getBOARD().getPadding();
		int boardRows = getBOARD().getRows();
		int boardColumns = getBOARD().getColumns();

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
		int tileSize = getBOARD().getTileSize();
		int padding = getBOARD().getPadding();
		int boardRows = getBOARD().getRows();

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
		for (int rows = 0; rows < getBOARD().getRows(); rows++) {
			// Loop through each column of the board
			for (int columns = 0; columns < getBOARD().getColumns(); columns++) {
				// Alternate between light and dark colors based on tile position
				if ((rows + columns) % 2 == 0) {
					graphics2D.setColor(fetchColorConfig("Colors", "TILE_LIGHT")); // Light tile color
				} else {
					graphics2D.setColor(fetchColorConfig("Colors", "TILE_DARK")); // Dark tile color
				}
				// Draw the rectangle representing the tile
				graphics2D.fillRect(columns * getBOARD().getTileSize(), rows * getBOARD().getTileSize(),
					getBOARD().getTileSize(), getBOARD().getTileSize());
			}
		}
	}

	/**
	 * Draws all arrows stored on the board in a Chess.com-like style.
	 * <p>
	 * This method renders both permanent arrows and a temporary arrow (if one exists).
	 * </p>
	 *
	 * @param graphics2D
	 * 	the Graphics2D context used for drawing.
	 */
	public void drawArrows(Graphics2D graphics2D) {
		// Draw each arrow from the board's list.
		for (Arrow arrow : BOARD.getArrows()) {
			if (!isValidArrow(arrow)) {
				continue;
			}
			drawSingleArrow(graphics2D, arrow);
		}

		// Draw the temporary arrow, if it exists.
		if (BOARD.getTempArrow() != null) {
			drawSingleArrow(graphics2D, BOARD.getTempArrow());
		}
	}

	/**
	 * Checks if the given arrow's end coordinates are valid (within the board boundaries).
	 *
	 * @param arrow
	 * 	the arrow to check.
	 *
	 * @return true if the arrow's end coordinates are within valid range; false otherwise.
	 */
	private boolean isValidArrow(Arrow arrow) {
		return arrow.getEndColumn() >= 0 && arrow.getEndColumn() <= 7 &&
			       arrow.getEndRow() >= 0 && arrow.getEndRow() <= 7;
	}

	/**
	 * Draws a single arrow on the board.
	 * <p>
	 * It calculates the pixel positions of the start and end points based on the board's tile size,
	 * and then determines if the arrow should be drawn as a standard arrow or as an L-shaped (knight) arrow.
	 * </p>
	 *
	 * @param graphics2D
	 * 	the Graphics2D context used for drawing.
	 * @param arrow
	 * 	the Arrow object containing start and end tile coordinates.
	 */
	private void drawSingleArrow(Graphics2D graphics2D, Arrow arrow) {
		int tileSize = BOARD.getTileSize();
		int startX = arrow.getStartColumn() * tileSize + tileSize / 2;
		int startY = arrow.getStartRow() * tileSize + tileSize / 2;
		int endX = arrow.getEndColumn() * tileSize + tileSize / 2;
		int endY = arrow.getEndRow() * tileSize + tileSize / 2;

		// Determine if the arrow represents a knight's move (L-shape).
		int dx = Math.abs(arrow.getEndColumn() - arrow.getStartColumn());
		int dy = Math.abs(arrow.getEndRow() - arrow.getStartRow());
		if ((dx == 2 && dy == 1) || (dx == 1 && dy == 2)) {
			// Draw an L-shaped arrow for knight moves.
			boolean horizontalFirst = (dx > dy);
			drawKnightArrow(graphics2D, startX, startY, endX, endY, horizontalFirst);
		} else {
			// Draw a standard arrow.
			drawArrow(graphics2D, startX, startY, endX, endY);
		}
	}

	/**
	 * Executes the given drawing action using arrow-specific graphics settings.
	 * It saves the current stroke and color, sets the arrow style, executes the drawing action,
	 * and finally restores the original graphics settings.
	 *
	 * @param g2d
	 * 	the Graphics2D context used for drawing.
	 * @param action
	 * 	the drawing action to execute.
	 */
	private void withArrowGraphics(Graphics2D g2d, Runnable action) {
		Stroke previousStroke = g2d.getStroke();
		Color previousColor = g2d.getColor();

		// Set arrow style (verwende ggf. Konstante wie THICKNESS und ARROW_COLOR)
		g2d.setStroke(new BasicStroke(
			THICKNESS,
			BasicStroke.CAP_SQUARE,
			BasicStroke.JOIN_MITER
		));
		g2d.setColor(ARROW_COLOR);

		// Execute the provided drawing action.
		action.run();

		// Restore the original settings.
		g2d.setStroke(previousStroke);
		g2d.setColor(previousColor);
	}

	/**
	 * Draws a standard arrow with a Chess.com–like style.
	 * The arrow's shaft starts from an adjusted point on the starting tile's border (rather than the center)
	 * and extends toward the endpoint with a triangular arrowhead.
	 *
	 * @param g2d
	 * 	the Graphics2D context used for drawing
	 * @param startTileCenterX
	 * 	the x-coordinate of the starting tile's center
	 * @param startTileCenterY
	 * 	the y-coordinate of the starting tile's center
	 * @param endX
	 * 	the x-coordinate of the arrow's endpoint
	 * @param endY
	 * 	the y-coordinate of the arrow's endpoint
	 */
	private void drawArrow(Graphics2D g2d, int startTileCenterX, int startTileCenterY, int endX, int endY) {
		withArrowGraphics(g2d, () -> {
			// Calculate the angle from the start tile center toward the endpoint.
			double angle = Math.atan2(endY - startTileCenterY, endX - startTileCenterX);
			// Adjust the starting point so the arrow begins near the tile edge.
			Point adjustedStart = getAdjustedStartPoint(startTileCenterX, startTileCenterY, angle, BOARD.getTileSize());

			double arrowHeadLength = HEAD_LENGTH;
			// Determine the endpoint of the arrow shaft (leaving space for the arrowhead).
			int shaftEndX = (int) (endX - arrowHeadLength * Math.cos(angle));
			int shaftEndY = (int) (endY - arrowHeadLength * Math.sin(angle));

			// Draw the arrow shaft from the adjusted start point to the shaft end.
			g2d.drawLine(adjustedStart.x, adjustedStart.y, shaftEndX, shaftEndY);
			// Draw the arrowhead at the endpoint.
			Polygon arrowHead = getPolygon(endX, endY, angle);
			g2d.fillPolygon(arrowHead);
		});
	}

	/**
	 * Draws an L-shaped arrow for knight moves.
	 * The arrow is composed of two segments:
	 * <ul>
	 *   <li>The first segment goes from an adjusted point on the starting tile's edge to an intermediate point.</li>
	 *   <li>The second segment goes from the intermediate point to the endpoint with an arrowhead.</li>
	 * </ul>
	 *
	 * @param g2d
	 * 	the Graphics2D context used for drawing
	 * @param startTileCenterX
	 * 	the x-coordinate of the starting tile's center
	 * @param startTileCenterY
	 * 	the y-coordinate of the starting tile's center
	 * @param endX
	 * 	the x-coordinate of the arrow's endpoint
	 * @param endY
	 * 	the y-coordinate of the arrow's endpoint
	 * @param horizontalFirst
	 * 	true if the horizontal segment is drawn first; false if vertical first
	 */
	private void drawKnightArrow(Graphics2D g2d, int startTileCenterX, int startTileCenterY, int endX, int endY, boolean horizontalFirst) {
		withArrowGraphics(g2d, () -> {
			// Determine the intermediate point based on the drawing order.
			int midX = horizontalFirst ? endX : startTileCenterX;
			int midY = horizontalFirst ? startTileCenterY : endY;

			// Adjust the starting point for the first segment.
			double firstSegmentAngle = Math.atan2(midY - startTileCenterY, midX - startTileCenterX);
			Point adjustedStart = getAdjustedStartPoint(startTileCenterX, startTileCenterY, firstSegmentAngle, BOARD.getTileSize());
			// Draw the first segment from the adjusted start point to the intermediate point.
			g2d.drawLine(adjustedStart.x, adjustedStart.y, midX, midY);

			// Calculate the angle for the second segment.
			double secondSegmentAngle = Math.atan2(endY - midY, endX - midX);
			double arrowHeadLength = HEAD_LENGTH;
			// Determine the endpoint of the second segment (leaving space for the arrowhead).
			int secondSegmentEndX = (int) (endX - arrowHeadLength * Math.cos(secondSegmentAngle));
			int secondSegmentEndY = (int) (endY - arrowHeadLength * Math.sin(secondSegmentAngle));

			// Draw the second segment from the intermediate point to near the endpoint.
			g2d.drawLine(midX, midY, secondSegmentEndX, secondSegmentEndY);
			// Draw the arrowhead at the endpoint.
			Polygon arrowHead = getPolygon(endX, endY, secondSegmentAngle);
			g2d.fillPolygon(arrowHead);
		});
	}

	/**
	 * Computes the adjusted starting point for an arrow drawn from a tile.
	 * The starting point is shifted from the tile center toward its edge in the given direction,
	 * ensuring that the arrow does not originate exactly from the center.
	 *
	 * @param tileCenterX
	 * 	the x-coordinate of the tile's center
	 * @param tileCenterY
	 * 	the y-coordinate of the tile's center
	 * @param angle
	 * 	the direction (in radians) from the tile center toward the target
	 * @param tileSize
	 * 	the size of the tile (assumed square)
	 *
	 * @return a Point representing the adjusted starting position for the arrow
	 */
	private Point getAdjustedStartPoint(int tileCenterX, int tileCenterY, double angle, int tileSize) {
		final int ARROW_START_MARGIN = ConfigFetcher.fetchIntegerConfig("Arrow", "START_MARGIN"); // Margin (in pixels) from the tile edge
		// Calculate the distance to the tile edge along the given direction.
		double distanceToEdgeX = (tileSize / 2.0) / Math.abs(Math.cos(angle));
		double distanceToEdgeY = (tileSize / 2.0) / Math.abs(Math.sin(angle));
		double distanceToEdge = Math.min(distanceToEdgeX, distanceToEdgeY);
		// Subtract the margin to avoid starting exactly at the edge.
		double adjustedDistance = Math.max(distanceToEdge - ARROW_START_MARGIN, 0);

		int adjustedX = (int) Math.round(tileCenterX + adjustedDistance * Math.cos(angle));
		int adjustedY = (int) Math.round(tileCenterY + adjustedDistance * Math.sin(angle));
		return new Point(adjustedX, adjustedY);
	}

}
