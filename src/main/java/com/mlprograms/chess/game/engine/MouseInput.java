/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
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
		if (SwingUtilities.isRightMouseButton(event)) {
			int column = event.getX() / board.getTileSize();
			int row = event.getY() / board.getTileSize();
			// Create a temporary arrow with starting and ending points equal to the clicked tile.
			board.setTempArrow(new Arrow(column, row, column, row));
			// Exit early for right-click events.
			return;
		}

		// For left-click actions:
		// Clear any pre-existing arrows on the board.
		board.getArrows().clear();

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
		// Right-click dragging for arrow drawing.
		if (SwingUtilities.isRightMouseButton(event)) {
			Arrow tempArrow = board.getTempArrow();
			if (tempArrow != null) {
				int column = event.getX() / board.getTileSize();
				int row = event.getY() / board.getTileSize();
				// Update the end coordinates of the temporary arrow.
				tempArrow.setEndColumn(column);
				tempArrow.setEndRow(row);
			}
			return;
		}

		// Left-click dragging for moving a piece.
		Piece selectedPiece = board.getSelectedPiece();
		if (selectedPiece != null) {
			// Set the flag to indicate that a drag is in progress.
			board.setMouseDragged(true);
			// Update the piece's graphical position based on the current mouse location.
			updatePiecePositionDuringDrag(selectedPiece, event);
			// Repaint the board to show the dragging effect.
			board.repaint();
		}
	}

	/**
	 * Called when a mouse button is released.
	 * <p>
	 * For right-click:
	 * - Finalizes the drawing of an arrow. If the temporary arrow's start and end points differ,
	 * the method checks for duplicates: if found, the arrow is removed; otherwise, it is added.
	 * </p>
	 * <p>
	 * For left-click:
	 * - If the piece was not dragged significantly, the piece is reset to its original position and
	 * possible moves are highlighted.
	 * - If the piece was dragged a significant distance, an attempt is made to move the piece to the new tile.
	 * - Additionally, if the move leaves the king in check with no legal moves, an illegal move sound is played
	 * and the king's tile is blinked.
	 * </p>
	 *
	 * @param event
	 * 	the mouse release event.
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		// Handle right-click release events for arrow finalization.
		if (SwingUtilities.isRightMouseButton(event)) {
			Arrow tempArrow = board.getTempArrow();
			if (tempArrow != null) {
				// Only proceed if the arrow has distinct start and end points.
				if (tempArrow.getStartColumn() != tempArrow.getEndColumn() ||
					    tempArrow.getStartRow() != tempArrow.getEndRow()) {

					// Look for an existing arrow with the same coordinates.
					Arrow duplicateArrow = null;
					for (Arrow arrow : board.getArrows()) {
						if (arrow.getStartColumn() == tempArrow.getStartColumn() &&
							    arrow.getStartRow() == tempArrow.getStartRow() &&
							    arrow.getEndColumn() == tempArrow.getEndColumn() &&
							    arrow.getEndRow() == tempArrow.getEndRow()) {
							duplicateArrow = arrow;
							break;
						}
					}
					// Remove the duplicate arrow if found; otherwise, add the new arrow.
					if (duplicateArrow != null) {
						board.getArrows().remove(duplicateArrow);
					} else {
						board.getArrows().add(tempArrow);
					}
				}
				// Clear the temporary arrow from the board.
				board.setTempArrow(null);
				board.repaint();
			}
			return;
		}

		// For left-click releases (related to piece movement).
		if (!board.isMouseDragged()) {
			// If the mouse was not dragged, no move needs to be processed.
			return;
		}

		// Get the currently selected piece.
		Piece selectedPiece = board.getSelectedPiece();

		// Check if the move leaves the king in check with no legal moves available.
		if (board.getMoveValidator().isKingInCheck() && selectedPiece.getLegalMoves(board).isEmpty()) {
			// Play an illegal move sound and blink the king's tile.
			board.getSoundPlayer().play(Sounds.ILLEGAL_MOVE);
			board.getBoardPainter().blinkKingsTile((Graphics2D) board.getGraphics(), board.isWhiteTurn());
		}

		// Calculate the target tile based on the release coordinates.
		int column = event.getX() / board.getTileSize();
		int row = event.getY() / board.getTileSize();

		// Calculate the drag distance from the original position.
		int draggedX = event.getX();
		int draggedY = event.getY();
		int originalX = getOriginalColumn() * board.getTileSize() + board.getTileSize() / 2;
		int originalY = getOriginalRow() * board.getTileSize() + board.getTileSize() / 2;
		double distance = Math.sqrt(Math.pow(draggedX - originalX, 2) + Math.pow(draggedY - originalY, 2));

		// If the drag distance is minimal (<= 25 pixels), consider it a click and not a move.
		if (distance <= 25) {
			resetPiecePosition(selectedPiece);
			board.showPossibleMoves(selectedPiece);
		} else {
			// Attempt to execute the move based on the drag.
			attemptMove(selectedPiece, column, row);
		}

		// Reset the dragging flag and repaint the board.
		board.setMouseDragged(false);
		board.repaint();
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
	 * 	the piece to select
	 */
	private void selectPiece(Piece piece) {
		getBoard().setSelectedPiece(piece);
		setOriginalColumn(piece.getColumn());
		setOriginalRow(piece.getRow());
		getBoard().showPossibleMoves(piece); // Highlight possible moves for the selected piece
	}

	/**
	 * Attempts to move the selected piece to the specified position.
	 *
	 * @param selectedPiece
	 * 	the currently selected piece
	 * @param column
	 * 	the target column
	 * @param row
	 * 	the target row
	 */
	private void attemptMove(Piece selectedPiece, int column, int row) {
		Piece pieceToCapture = getBoard().getPieceList().stream()
			                       .filter(p -> p.getColumn() == column && p.getRow() == row && !p.equals(selectedPiece))
			                       .findFirst()
			                       .orElse(null);

		Move move = new Move(getBoard(), selectedPiece, column, row, pieceToCapture);

		if (getBoard().isValidMove(move)) {
			getBoard().makeMove(move); // Execute the move if valid
		} else {
			resetPiecePosition(selectedPiece); // Reset the piece to its original position
		}
		clearSelection(); // Clear selection after the move
	}

	/**
	 * Updates the position of a piece during a drag operation.
	 *
	 * @param piece
	 * 	the piece being dragged
	 * @param event
	 * 	the mouse drag event
	 */
	private void updatePiecePositionDuringDrag(Piece piece, MouseEvent event) {
		int offsetX = getBoard().getTileSize() / 2;
		int offsetY = getBoard().getTileSize() / 2;
		piece.setXPos(event.getX() - offsetX); // Adjust X position based on drag
		piece.setYPos(event.getY() - offsetY); // Adjust Y position based on drag
	}

	/**
	 * Resets the piece's position to its original location if the move is invalid.
	 *
	 * @param piece
	 * 	the piece to reset
	 */
	private void resetPiecePosition(Piece piece) {
		piece.setPosition(originalColumn, originalRow); // Revert to the original position
	}

}
