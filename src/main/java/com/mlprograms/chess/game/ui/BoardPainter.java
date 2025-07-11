/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
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

import static com.mlprograms.chess.utils.ConfigFetcher.*;

@Getter
public class BoardPainter {

	private static final float HEAD_LENGTH = ConfigFetcher.fetchFloatConfig("Arrow", "HEAD_LENGTH");
	private static final double HEAD_ANGLE = ConfigFetcher.fetchFloatConfig("Arrow", "HEAD_ANGLE");
	private static final double START_MARGIN = ConfigFetcher.fetchFloatConfig("Arrow", "START_MARGIN");
	private static final double START_MARGIN_DIAGONAL = ConfigFetcher.fetchFloatConfig("Arrow", "START_MARGIN_DIAGONAL");
	private final int ALPHA = ConfigFetcher.fetchIntegerConfig("Colors", "ARROW_ALPHA");
	private final Color ARROW_COLOR = ConfigFetcher.fetchColorWithAlphaConfig("Colors", "ARROW_COLOR", ALPHA);
	private final float THICKNESS = ConfigFetcher.fetchFloatConfig("Arrow", "THICKNESS");
	private final Board BOARD;

	private boolean isBlinkingActive = false;

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

	public void highlightDragFromTile(Graphics2D graphics2D) {
		if (BOARD.getSelectedPiece() == null || !BOARD.isMouseDragged()) {
			return;
		}

		Color dragFromTile = fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_DRAG_FROM", fetchIntegerConfig("Colors", "TILE_HIGHLIGHT_DRAG_FROM_ALPHA"));
		graphics2D.setColor(dragFromTile);

		// Draw the highlight on the specified tile
		graphics2D.fillRect(BOARD.getSelectedPiece().getColumn() * BOARD.getTileSize(), BOARD.getSelectedPiece().getRow() * BOARD.getTileSize(),
			BOARD.getTileSize(), BOARD.getTileSize());
	}

	/**
	 * Paints all the pieces currently on the board.
	 * Iterates through the list of pieces managed by the board and calls their respective paint methods.
	 *
	 * @param graphics2D
	 * 	the Graphics2D object used to render the pieces on the board
	 */
	public void paintPieces(Graphics2D graphics2D) {
		Piece draggedPiece = BOARD.getSelectedPiece();

		for (Piece piece : BOARD.getPieceList()) {
			if (BOARD.isMouseDragged() && piece.equals(draggedPiece)) {
				continue;
			}

			piece.paint(graphics2D);
		}

		if (draggedPiece != null) {
			draggedPiece.paint(graphics2D);
		}
	}

	/**
	 * Paints red highlights on the board.
	 * <p>
	 * This method iterates through all points in the board's red highlights list
	 * and calls the paintRedHighlight method to draw each highlight.
	 * </p>
	 *
	 * @param graphics2D
	 * 	the Graphics2D object used for drawing
	 */
	public void paintRedHighlights(Graphics2D graphics2D) {
		for (Point point : BOARD.getRedHighlights()) {
			paintRedHighlight(graphics2D, point);
		}
	}

	/**
	 * Highlights a tile with a red overlay to indicate a critical situation,
	 * such as a king in check or an invalid move.
	 *
	 * @param graphics2D
	 * 	the Graphics2D object used for drawing
	 * @param point
	 * 	the coordinates of the tile to highlight
	 */
	private void paintRedHighlight(Graphics2D graphics2D, Point point) {
		// Define the color for the red highlight with transparency
		Color redHighlight = fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_MARKED", 150);
		graphics2D.setColor(redHighlight);

		// Draw the highlight on the specified tile
		graphics2D.fillRect(point.x * BOARD.getTileSize(), point.y * BOARD.getTileSize(),
			BOARD.getTileSize(), BOARD.getTileSize());
	}

	/**
	 * Draws a border around the tile currently being hovered over by the mouse.
	 * <p>
	 * This method checks if there is a tile being hovered over. If so, it sets the color and stroke
	 * for the border and draws a rectangle around the hovered tile.
	 * </p>
	 *
	 * @param graphics2D
	 * 	the Graphics2D context used for drawing
	 */
	public void drawTileHoverBorder(Graphics2D graphics2D) {
		if (BOARD.getHoveredTile() == null || BOARD.getSelectedPiece() == null) {
			return;
		}

		graphics2D.setColor(fetchColorConfig("Colors", "TILE_HOVER_BORDER"));
		graphics2D.setStroke(new BasicStroke(fetchIntegerConfig("Colors", "TILE_HOVER_BORDER_THICKNESS")));
		graphics2D.drawRect(BOARD.getHoveredTile().x * BOARD.getTileSize(), BOARD.getHoveredTile().y * BOARD.getTileSize(),
			BOARD.getTileSize(), BOARD.getTileSize());
	}

	/**
	 * Highlights all possible moves for the currently selected piece on the board.
	 *
	 * @param graphics2D
	 * 	the graphics context used for drawing
	 */
	public void highlightPossibleMoves(Graphics2D graphics2D) {
		List<Move> possibleMoves = BOARD.getPossibleMoves();
		if (possibleMoves == null || BOARD.getSelectedPiece() == null) {
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
			graphics2D.fillRect(column * BOARD.getTileSize(), row * BOARD.getTileSize(), BOARD.getTileSize(), BOARD.getTileSize());
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
	 * @param moveToHighlight
	 * 	the move to highlight; if null, the last move from the move history is used
	 */
	public void highlightMadeMove(Graphics2D graphics2D, Move moveToHighlight) {
		if (BOARD.getMoveHistory().isEmpty()) {
			return;
		}

		// Get the last move made on the board
		Move lastMove;

		if (moveToHighlight == null) {
			lastMove = BOARD.getMoveHistory().getLast().getMove();
		} else {
			lastMove = moveToHighlight;
		}

		// Set the highlight color based on the move type
		graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_MOVE_FROM_TO", 135));

		// Draw the highlight on the source tile
		graphics2D.fillRect(lastMove.getOldColumn() * BOARD.getTileSize(), lastMove.getOldRow() * BOARD.getTileSize(), BOARD.getTileSize(), BOARD.getTileSize());
		graphics2D.fillRect(lastMove.getNewColumn() * BOARD.getTileSize(), lastMove.getNewRow() * BOARD.getTileSize(), BOARD.getTileSize(), BOARD.getTileSize());
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
		highlightMadeMove(graphics2D, null);
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
		King king = BOARD.getMoveValidator().findKing(whiteKing);
		int column = king.getColumn();
		int row = king.getRow();

		if (isBlinkingActive) {
			return;
		}
		isBlinkingActive = true;

		// Let the king's tile blink a few times
		new Thread(() -> {
			graphics2D.setColor(fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT_ILLEGAL", 105));

			BOARD.getSoundPlayer().play(Sounds.ILLEGAL_MOVE);

			for (int i = 0; i < 3; i++) {
				sleep400();
				graphics2D.fillRect(column * BOARD.getTileSize(), row * BOARD.getTileSize(), BOARD.getTileSize(), BOARD.getTileSize());
				paintPieces(graphics2D);
				sleep400();
				BOARD.repaint();
			}

			isBlinkingActive = false;
		}).start();
	}

	/**
	 * Pauses the current thread for 400 milliseconds.
	 */
	private void sleep400() {
		try {
			Thread.sleep(400);
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
		Piece targetPiece = BOARD.getPieceAt(column, row);

		// Check for direct captures or special en passant moves
		return targetPiece != null ||
			       (BOARD.getSelectedPiece() instanceof Pawn && BOARD.getTileNumber(column, row) == BOARD.getEnPassantTile());
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

		boolean isWhiteAtBottom = BOARD.isWhiteAtBottom();
		drawColumnLabels(graphics2D, isWhiteAtBottom);
		drawRowLabels(graphics2D, isWhiteAtBottom);
	}

	/**
	 * Draws the column labels ('a' to 'h' or 'h' to 'a') below the board.
	 */
	private void drawColumnLabels(Graphics2D graphics2D, boolean isWhiteAtBottom) {
		int tileSize = BOARD.getTileSize();
		int padding = BOARD.getPadding();
		int boardRows = BOARD.getRows();
		int boardColumns = BOARD.getColumns();

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
		int tileSize = BOARD.getTileSize();
		int padding = BOARD.getPadding();
		int boardRows = BOARD.getRows();

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
		for (int rows = 0; rows < BOARD.getRows(); rows++) {
			// Loop through each column of the board
			for (int columns = 0; columns < BOARD.getColumns(); columns++) {
				// Alternate between light and dark colors based on tile position
				if ((rows + columns) % 2 == 0) {
					graphics2D.setColor(fetchColorConfig("Colors", "TILE_LIGHT")); // Light tile color
				} else {
					graphics2D.setColor(fetchColorConfig("Colors", "TILE_DARK")); // Dark tile color
				}
				// Draw the rectangle representing the tile
				graphics2D.fillRect(columns * BOARD.getTileSize(), rows * BOARD.getTileSize(),
					BOARD.getTileSize(), BOARD.getTileSize());
			}
		}
	}

	/**
	 * Draws all arrows on the board.
	 * <p>
	 * This method enables antialiasing and stroke control for smoother rendering,
	 * iterates through the list of arrows on the board, and draws each valid arrow.
	 * If a temporary arrow exists, it is also drawn.
	 * </p>
	 *
	 * @param graphics2D
	 * 	the Graphics2D context used for drawing
	 */
	public void drawArrows(Graphics2D graphics2D) {
		// For smoother arrow rendering, enable antialiasing and stroke control
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		graphics2D.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

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

		// Enable antialiasing and stroke control for smoother arrow rendering
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g2d.setRenderingHint(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);

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
		boolean isStraightArrow = isStraightAngle(angle);

		// Calculate the distance to the tile edge along the given direction.
		double distanceToEdgeX = (tileSize / 2.0) / Math.abs(Math.cos(angle));
		double distanceToEdgeY = (tileSize / 2.0) / Math.abs(Math.sin(angle));
		double distanceToEdge = Math.min(distanceToEdgeX, distanceToEdgeY);
		// Subtract the margin to avoid starting exactly at the edge.
		double adjustedDistance = Math.max(distanceToEdge - (isStraightArrow ? START_MARGIN : START_MARGIN_DIAGONAL), 0);

		int adjustedX = (int) Math.round(tileCenterX + adjustedDistance * Math.cos(angle));
		int adjustedY = (int) Math.round(tileCenterY + adjustedDistance * Math.sin(angle));
		return new Point(adjustedX, adjustedY);
	}

	/**
	 * Checks if 'angle' (in radians) is close to 0, PI/2, PI, or 3*PI/2.
	 */
	private boolean isStraightAngle(double angle) {
		// Normalize the angle to [0, 2π).
		double normalized = normalizeAngle(angle);
		// We use 1e-6 as epsilon.
		double eps = 1e-6;

		// Check for 0, π/2, π, 3π/2:
		return isClose(normalized, 0.0, eps)
			       || isClose(normalized, Math.PI / 2, eps)
			       || isClose(normalized, Math.PI, eps)
			       || isClose(normalized, 3.0 * Math.PI / 2, eps);
	}

	/**
	 * Normalizes an angle to the range [0, 2π).
	 */
	private double normalizeAngle(double angle) {
		double twoPi = 2.0 * Math.PI;
		// Java modulo can be negative, so double it:
		return (angle % twoPi + twoPi) % twoPi;
	}

	/**
	 * Helper function to compare two double values with tolerance.
	 */
	private boolean isClose(double value, double target, double eps) {
		return Math.abs(value - target) < eps;
	}


}
