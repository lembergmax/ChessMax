/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import lombok.Getter;
import lombok.Setter;

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
		getBoard().setMouseDragged(false);
		int column = event.getX() / getBoard().getTileSize();
		int row = event.getY() / getBoard().getTileSize();

		Piece clickedPiece = getBoard().getPieceAt(column, row);
		Piece selectedPiece = getBoard().getSelectedPiece();

		if (clickedPiece != null && clickedPiece == selectedPiece) {
			clearSelection(); // Deselect the piece if it's already selected
		} else if (clickedPiece != null && clickedPiece.isWhite() == getBoard().isWhiteTurn()) {
			selectPiece(clickedPiece); // Select the clicked piece if it matches the turn
		} else if (selectedPiece != null) {
			attemptMove(selectedPiece, column, row); // Attempt to move the selected piece
		}

		getBoard().repaint(); // Redraw the board to reflect changes
	}

	/**
	 * Handles mouse drag events to update the position of the selected piece.
	 *
	 * @param event
	 * 	the mouse drag event
	 */
	@Override
	public void mouseDragged(MouseEvent event) {
		Piece selectedPiece = getBoard().getSelectedPiece();
		if (selectedPiece != null) {
			getBoard().setMouseDragged(true);
			updatePiecePositionDuringDrag(selectedPiece, event); // Update piece position during drag
			getBoard().repaint(); // Redraw the board during dragging
		}
	}

	/**
	 * Handles the mouse release event during a piece drag operation on the chessboard.
	 * This method determines whether the drag should result in an attempted move
	 * or reset the piece to its original position based on the drag distance.
	 * <p>
	 * - If the drag distance is less than or equal to 25 pixels, the piece remains
	 * selected, and its possible moves are displayed.
	 * - If the drag distance is greater than 25 pixels, the method attempts to move
	 * the piece to the calculated destination tile.
	 *
	 * @param event
	 * 	the MouseEvent triggered upon releasing the mouse button.
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		// Check if a drag operation was in progress
		if (getBoard().isMouseDragged()) {
			// Retrieve the currently selected piece
			Piece selectedPiece = getBoard().getSelectedPiece();

			// Calculate the target tile coordinates based on the mouse release position
			int column = event.getX() / getBoard().getTileSize();
			int row = event.getY() / getBoard().getTileSize();

			// Calculate the drag distance from the piece's original position
			int draggedX = event.getX();
			int draggedY = event.getY();
			int originalX = getOriginalColumn() * getBoard().getTileSize() + getBoard().getTileSize() / 2;
			int originalY = getOriginalRow() * getBoard().getTileSize() + getBoard().getTileSize() / 2;
			double distance = Math.sqrt(Math.pow(draggedX - originalX, 2) + Math.pow(draggedY - originalY, 2));

			// If the drag distance is within 25 pixels, reset the piece's position
			if (distance <= 25) {
				resetPiecePosition(selectedPiece);
				getBoard().showPossibleMoves(selectedPiece); // Keep showing possible moves
			} else {
				// Otherwise, attempt to move the piece to the new tile
				attemptMove(selectedPiece, column, row);
			}

			// End the drag operation and repaint the board
			getBoard().setMouseDragged(false);
			getBoard().repaint();
		}
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
		Move move = new Move(getBoard(), selectedPiece, column, row);
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
