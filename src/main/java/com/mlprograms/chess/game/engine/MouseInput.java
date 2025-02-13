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
 * Handles mouse interactions for the chessboard, including piece selection, dragging, and moving.
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
	 * Handles mouse press events to manage piece selection and initial move validation.
	 *
	 * @param event
	 * 	the mouse press event
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			int column = event.getX() / board.getTileSize();
			int row = event.getY() / board.getTileSize();
			// Erstelle einen temporären Pfeil, der als Startpunkt und vorläufiger Endpunkt gilt
			board.setTempArrow(new Arrow(column, row, column, row));
			return; // Für Rechtsklick keine weitere Verarbeitung (wie z. B. Figurenwahl)
		}

		// Linksklick: Bestehende Logik
		board.setMouseDragged(false);
		int column = event.getX() / board.getTileSize();
		int row = event.getY() / board.getTileSize();

		Piece clickedPieceAtTile = board.getPieceAt(column, row);
		Piece selectedPiece = board.getSelectedPiece();

		if (clickedPieceAtTile != null && clickedPieceAtTile == selectedPiece) {
			clearSelection(); // Deselect the piece if it's already selected
		} else if (clickedPieceAtTile != null && clickedPieceAtTile.isWhite() == board.isWhiteTurn()) {
			selectPiece(clickedPieceAtTile); // Select the clicked piece if it matches the turn
		} else if (selectedPiece != null) {
			attemptMove(selectedPiece, column, row); // Attempt to move the selected piece
		}

		board.repaint(); // Redraw the board to reflect changes
	}

	/**
	 * Handles mouse drag events to update the position of the selected piece.
	 *
	 * @param event
	 * 	the mouse drag event
	 */
	@Override
	public void mouseDragged(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			// Right-click: update temporary arrow end coordinates.
			Arrow tempArrow = board.getTempArrow();
			if (tempArrow != null) {
				int column = event.getX() / board.getTileSize();
				int row = event.getY() / board.getTileSize();
				tempArrow.setEndColumn(column);
				tempArrow.setEndRow(row);
				board.repaint();
			}
			return;
		}

		// Left-click: update piece position during drag.
		Piece selectedPiece = board.getSelectedPiece();
		if (selectedPiece != null) {
			board.setMouseDragged(true);
			updatePiecePositionDuringDrag(selectedPiece, event); // Update piece position based on drag
			board.repaint(); // Redraw the board during dragging
		}
	}

	/**
	 * Handles the mouse release event during a piece drag or arrow drawing operation.
	 * <p>
	 * For right-click:
	 * - If the temporary arrow's start and end differ, search for an existing arrow with the same
	 * coordinates. If found, remove it; otherwise, add the new arrow.
	 * <p>
	 * For left-click:
	 * - If the drag distance is less than or equal to 25 pixels, reset the piece to its original position
	 * and show possible moves.
	 * - Otherwise, attempt to move the piece to the target tile.
	 *
	 * @param event
	 * 	the MouseEvent triggered upon releasing the mouse button.
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		if (SwingUtilities.isRightMouseButton(event)) {
			Arrow tempArrow = board.getTempArrow();
			if (tempArrow != null) {
				// Check if start and end coordinates are different (if not, no action is taken)
				if (tempArrow.getStartColumn() != tempArrow.getEndColumn() ||
					    tempArrow.getStartRow() != tempArrow.getEndRow()) {
					// Search for an existing arrow with the same start and end coordinates.
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
					// If an identical arrow exists, remove it; otherwise, add the new arrow.
					if (duplicateArrow != null) {
						board.getArrows().remove(duplicateArrow);
					} else {
						board.getArrows().add(tempArrow);
					}
				}
				board.setTempArrow(null);
				board.repaint();
			}
			return;
		}

		// Left-click: handle piece movement
		if (!board.isMouseDragged()) {
			return;
		}

		Piece selectedPiece = board.getSelectedPiece();

		if (board.getMoveValidator().isKingInCheck() && selectedPiece.getLegalMoves(board).isEmpty()) {
			board.getSoundPlayer().play(Sounds.ILLEGAL_MOVE);
			board.getBoardPainter().blinkKingsTile((Graphics2D) board.getGraphics(), board.isWhiteTurn());
		}

		int column = event.getX() / board.getTileSize();
		int row = event.getY() / board.getTileSize();

		int draggedX = event.getX();
		int draggedY = event.getY();
		int originalX = getOriginalColumn() * board.getTileSize() + board.getTileSize() / 2;
		int originalY = getOriginalRow() * board.getTileSize() + board.getTileSize() / 2;
		double distance = Math.sqrt(Math.pow(draggedX - originalX, 2) + Math.pow(draggedY - originalY, 2));

		if (distance <= 25) {
			resetPiecePosition(selectedPiece);
			board.showPossibleMoves(selectedPiece);
		} else {
			attemptMove(selectedPiece, column, row);
		}

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
