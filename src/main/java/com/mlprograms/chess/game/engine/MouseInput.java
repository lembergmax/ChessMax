/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Arrow;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.game.utils.Sounds;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import java.util.Optional;

/**
 * MouseInput class handles all mouse interactions on the chessboard.
 * It manages selecting pieces, dragging them to move, and drawing/removing arrows.
 */
@Getter
@Setter
public class MouseInput extends MouseAdapter {

	private final Board board;
	private int originalColumn;
	private int originalRow;
	private Point tempRedHighlight;

	private boolean allowRightClick = true;

	/**
	 * Initializes the MouseInput with the game board.
	 *
	 * @param board
	 * 	the game board instance
	 */
	public MouseInput(Board board) {
		this.board = board;
	}

	/**
	 * Called when a mouse button is pressed.
	 * <p>
	 * For right-click:
	 * - Computes the board tile based on the click position.
	 * - Creates a temporary arrow with the same start and end coordinates.
	 * - No further processing (like piece selection) is done.
	 * </p>
	 * <p>
	 * For left-click:
	 * - Clears any existing arrows.
	 * - Resets drag flags.
	 * - Determines if a piece should be selected, deselected, or moved based on the clicked tile.
	 * </p>
	 *
	 * @param event
	 * 	the mouse press event.
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		// Handle right-click (typically used for drawing arrows).
		if (SwingUtilities.isRightMouseButton(event) && allowRightClick) {
			int column = event.getX() / board.getTileSize();
			int row = event.getY() / board.getTileSize();
			// Create a temporary arrow with starting and ending points equal to the clicked tile.
			board.setTempArrow(new Arrow(column, row, column, row));

			// Add temporary red highlight.
			tempRedHighlight = new Point(column, row);

			// Exit early for right-click events.
			return;
		}

		// For left-click actions:
		// Clear any pre-existing arrows on the board.
		board.getArrows().clear();
		board.getRedHighlights().clear();
		board.setHoveredTile(null);

		// Reset the flag indicating if a piece is being dragged.
		board.setMouseDragged(false);

		// Calculate the tile coordinates from the mouse click.
		int column = event.getX() / board.getTileSize();
		int row = event.getY() / board.getTileSize();

		// Determine the piece (if any) located at the clicked tile.
		Piece clickedPieceAtTile = board.getPieceAt(column, row);
		// Retrieve the piece that is currently selected on the board.
		Piece selectedPiece = board.getSelectedPiece();

		// If the clicked piece is already selected, clear the selection.
		if (clickedPieceAtTile != null && clickedPieceAtTile == selectedPiece) {
			clearSelection();
		}
		// If the clicked piece belongs to the current player (matching the turn), select it.
		else if (clickedPieceAtTile != null && clickedPieceAtTile.isWhite() == board.isWhiteTurn()) {
			selectPiece(clickedPieceAtTile);
		}
		// Otherwise, if a piece is already selected, attempt to move it to the clicked tile.
		else if (selectedPiece != null) {
			if (getBoard().isHistoryLookup() || getBoard().getGameEnding() != GameEnding.IN_PROGRESS) {
				clearSelection();
				board.repaint();
				return;
			}

			attemptMove(selectedPiece, column, row);
		}

		// Repaint the board to reflect any changes made.
		board.repaint();
	}

	/**
	 * Called when the mouse is dragged.
	 * <p>
	 * For right-click drags:
	 * - Updates the temporary arrow's endpoint based on the current mouse position.
	 * </p>
	 * <p>
	 * For left-click drags:
	 * - If a piece is selected, updates its visual position to follow the mouse.
	 * </p>
	 *
	 * @param event
	 * 	the mouse drag event.
	 */
	@Override
	public void mouseDragged(MouseEvent event) {
		int column = event.getX() / board.getTileSize();
		int row = event.getY() / board.getTileSize();

		board.setHoveredTile(new Point(column, row));

		// Right-click dragging for drawing arrows and highlighting tiles.
		if (SwingUtilities.isRightMouseButton(event) && allowRightClick) {
			Arrow tempArrow = board.getTempArrow();
			if (tempArrow != null) {
				// Update the end coordinates of the temporary arrow.
				tempArrow.setEndColumn(column);
				tempArrow.setEndRow(row);
			}
			return;
		}

		if (getBoard().isHistoryLookup() || getBoard().getGameEnding() != GameEnding.IN_PROGRESS) {
			return;
		}

		// Left-click dragging for moving a piece.
		Piece selectedPiece = board.getSelectedPiece();
		if (selectedPiece != null) {
			setAllowRightClick(false);
			// Set the flag to indicate that a drag is in progress.
			board.setMouseDragged(true);
			// Update the piece's graphical position based on the current mouse location.
			updatePiecePositionDuringDrag(selectedPiece, event);
			// Repaint the board to show the dragging effect.
			board.repaint();
		}

		if (tempRedHighlight != null && tempRedHighlight.x != column && tempRedHighlight.y != row) {
			// Remove temporary red highlight.
			tempRedHighlight = null;
		}
	}

	@Override
	public void mouseReleased(MouseEvent event) {
		int boardColumn = event.getX() / board.getTileSize();
		int boardRow = event.getY() / board.getTileSize();

		if (SwingUtilities.isRightMouseButton(event) && allowRightClick) {
			handleRightClick(boardColumn, boardRow);
			return;
		}

		if (!board.isMouseDragged()) {
			return;
		}

		handleLeftClick(event, boardColumn, boardRow);
	}

	/**
	 * Handles right-click events:
	 * First toggles red highlights (if any) and then processes the temporary arrow object.
	 *
	 * @param boardColumn
	 * 	the board column where the event occurred.
	 * @param boardRow
	 * 	the board row where the event occurred.
	 */
	private void handleRightClick(int boardColumn, int boardRow) {
		board.setHoveredTile(null);
		toggleRedHighlight(boardColumn, boardRow);
		processTemporaryArrow();
	}

	/**
	 * Toggles the red highlight if a temporary highlight exists and the coordinates match.
	 *
	 * @param boardColumn
	 * 	the board column of the highlight.
	 * @param boardRow
	 * 	the board row of the highlight.
	 */
	private void toggleRedHighlight(int boardColumn, int boardRow) {
		if (tempRedHighlight == null) {
			return;
		}

		if (tempRedHighlight.x == boardColumn && tempRedHighlight.y == boardRow) {
			List<Point> redHighlights = board.getRedHighlights();
			if (redHighlights.contains(tempRedHighlight)) {
				redHighlights.remove(tempRedHighlight);
			} else {
				redHighlights.add(tempRedHighlight);
			}
		} else {
			tempRedHighlight = null;
		}
	}

	/**
	 * Validates and processes the temporary arrow object:
	 * If the arrow is valid, it checks whether an identical arrow already exists.
	 * If it exists, the duplicate is removed; otherwise, the arrow is added.
	 */
	private void processTemporaryArrow() {
		Arrow tempArrow = board.getTempArrow();
		if (tempArrow == null) {
			return;
		}

		if (isValidArrow(tempArrow)) {
			Optional<Arrow> duplicateArrow = board.getArrows().stream()
				                                 .filter(existingArrow -> arrowsAreEqual(existingArrow, tempArrow))
				                                 .findFirst();

			if (duplicateArrow.isPresent()) {
				board.getArrows().remove(duplicateArrow.get());
			} else {
				board.getArrows().add(tempArrow);
			}
		}
		board.setTempArrow(null);
		board.repaint();
	}

	/**
	 * An arrow is considered valid if its starting and ending coordinates are different.
	 *
	 * @param arrow
	 * 	the arrow to validate.
	 *
	 * @return true if the arrow is valid, false otherwise.
	 */
	private boolean isValidArrow(Arrow arrow) {
		return arrow.getStartColumn() != arrow.getEndColumn() ||
			       arrow.getStartRow() != arrow.getEndRow();
	}

	/**
	 * Compares two arrows based on their starting and ending coordinates.
	 *
	 * @param arrow1
	 * 	the first arrow.
	 * @param arrow2
	 * 	the second arrow.
	 *
	 * @return true if both arrows are equal, false otherwise.
	 */
	private boolean arrowsAreEqual(Arrow arrow1, Arrow arrow2) {
		return arrow1.getStartColumn() == arrow2.getStartColumn() &&
			       arrow1.getStartRow() == arrow2.getStartRow() &&
			       arrow1.getEndColumn() == arrow2.getEndColumn() &&
			       arrow1.getEndRow() == arrow2.getEndRow();
	}

	/**
	 * Handles left-click events and executes a move or resets the piece.
	 *
	 * @param event
	 * 	the mouse release event.
	 * @param boardColumn
	 * 	the target board column.
	 * @param boardRow
	 * 	the target board row.
	 */
	private void handleLeftClick(MouseEvent event, int boardColumn, int boardRow) {
		Piece selectedPiece = board.getSelectedPiece();
		board.setHoveredTile(null);

		if (isIllegalMove(selectedPiece)) {
			board.getSoundPlayer().play(Sounds.ILLEGAL_MOVE);
			board.getBoardPainter().blinkKingsTile((Graphics2D) board.getGraphics(), board.isWhiteTurn());
		}

		if (hasDraggedShortDistance(event)) {
			resetPiecePosition(selectedPiece);
			board.showPossibleMoves(selectedPiece);
		} else {
			attemptMove(selectedPiece, boardColumn, boardRow);
		}

		setAllowRightClick(true);
		board.setMouseDragged(false);
		board.repaint();
	}

	/**
	 * Checks if the current state indicates an illegal move
	 * (e.g., due to a check condition).
	 *
	 * @param selectedPiece
	 * 	the piece being moved.
	 *
	 * @return true if the move is illegal, false otherwise.
	 */
	private boolean isIllegalMove(Piece selectedPiece) {
		return board.getMoveValidator().isKingInCheck() &&
			       selectedPiece.getLegalMoves(board).isEmpty();
	}

	/**
	 * Determines if the drag distance is below the threshold.
	 *
	 * @param event
	 * 	the mouse event.
	 *
	 * @return true if the drag distance is short, false otherwise.
	 */
	private boolean hasDraggedShortDistance(MouseEvent event) {
		double dragDistance = calculateDragDistance(event);
		return dragDistance <= 25;
	}

	/**
	 * Calculates the distance between the original position and the current mouse position.
	 *
	 * @param event
	 * 	the mouse event.
	 *
	 * @return the distance in pixels.
	 */
	private double calculateDragDistance(MouseEvent event) {
		int draggedX = event.getX();
		int draggedY = event.getY();
		int originalX = getOriginalColumn() * board.getTileSize() + board.getTileSize() / 2;
		int originalY = getOriginalRow() * board.getTileSize() + board.getTileSize() / 2;
		return Math.hypot(draggedX - originalX, draggedY - originalY);
	}

	/**
	 * Clears the current piece selection and resets possible moves.
	 */
	private void clearSelection() {
		getBoard().setSelectedPiece(null);
		getBoard().getPossibleMoves().clear();
	}

	/**
	 * Selects the given piece and highlights its possible moves.
	 *
	 * @param piece
	 * 	the piece to select.
	 */
	private void selectPiece(Piece piece) {
		getBoard().setSelectedPiece(piece);
		setOriginalColumn(piece.getColumn());
		setOriginalRow(piece.getRow());
		getBoard().showPossibleMoves(piece); // Highlight possible moves for the selected piece.
	}

	/**
	 * Attempts to move the selected piece to the specified position.
	 *
	 * @param selectedPiece
	 * 	the currently selected piece.
	 * @param column
	 * 	the target column.
	 * @param row
	 * 	the target row.
	 */
	private void attemptMove(Piece selectedPiece, int column, int row) {
		Piece pieceToCapture = getBoard().getPieceList().stream()
			                       .filter(p -> p.getColumn() == column && p.getRow() == row && !p.equals(selectedPiece))
			                       .findFirst()
			                       .orElse(null);

		Move move = new Move(getBoard(), selectedPiece, column, row, pieceToCapture);

		if (getBoard().isValidMove(move)) {
			getBoard().makeMove(move); // Execute the move if valid.
		} else {
			resetPiecePosition(selectedPiece); // Reset the piece to its original position.
		}
		clearSelection(); // Clear selection after the move.
	}

	/**
	 * Updates the position of a piece during a drag operation.
	 *
	 * @param piece
	 * 	the piece being dragged.
	 * @param event
	 * 	the mouse drag event.
	 */
	private void updatePiecePositionDuringDrag(Piece piece, MouseEvent event) {
		int offsetX = getBoard().getTileSize() / 2;
		int offsetY = getBoard().getTileSize() / 2;
		piece.setXPos(event.getX() - offsetX); // Adjust X position based on drag.
		piece.setYPos(event.getY() - offsetY); // Adjust Y position based on drag.
	}

	/**
	 * Resets the piece's position to its original location if the move is invalid.
	 *
	 * @param piece
	 * 	the piece to reset.
	 */
	private void resetPiecePosition(Piece piece) {
		piece.setPosition(originalColumn, originalRow); // Revert to the original position.
	}

}
