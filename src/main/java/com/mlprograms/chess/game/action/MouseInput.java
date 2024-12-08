/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.action;

import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import lombok.Getter;
import lombok.Setter;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * Handles mouse interactions for the chessboard, including selecting and moving pieces.
 */
@Getter
@Setter
public class MouseInput extends MouseAdapter {

	private final Board board;
	private int originalColumn;
	private int originalRow;
	private boolean isDragging = false;

	/**
	 * Constructor to initialize the MouseInput with the game board.
	 *
	 * @param board
	 * 	the game board instance
	 */
	public MouseInput(Board board) {
		this.board = board;
	}

	/**
	 * Handles mouse press events. Selects a piece or attempts to move a selected piece.
	 *
	 * @param event
	 * 	the mouse event
	 */
	@Override
	public void mousePressed(MouseEvent event) {
		int column = event.getX() / board.getTileSize();
		int row = event.getY() / board.getTileSize();

		Piece clickedPiece = board.getPieceAt(column, row);
		Piece selectedPiece = board.getSelectedPiece();

		if (!isDragging) {
			if (selectedPiece == null) {
				selectPiece(clickedPiece);
			} else {
				attemptMove(selectedPiece, column, row);
			}
		}

		board.repaint();
	}

	/**
	 * Handles mouse drag events. Updates the position of the selected piece for a dragging effect.
	 *
	 * @param event
	 * 	the mouse event
	 */
	@Override
	public void mouseDragged(MouseEvent event) {
		Piece selectedPiece = board.getSelectedPiece();
		if (selectedPiece != null) {
			isDragging = true;
			updatePiecePositionDuringDrag(selectedPiece, event);
			board.repaint();
		}
	}

	/**
	 * Handles mouse release events. Finalizes the move if dragging, or resets piece position if invalid.
	 *
	 * @param event
	 * 	the mouse event
	 */
	@Override
	public void mouseReleased(MouseEvent event) {
		if (isDragging) {
			finalizeMove(event);
			isDragging = false;
			board.repaint();
		}
	}

	/**
	 * Selects a piece on the board and highlights possible moves.
	 *
	 * @param piece
	 * 	the piece to select
	 */
	private void selectPiece(Piece piece) {
		if (piece != null) {
			board.setSelectedPiece(piece);
			originalColumn = piece.getColumn();
			originalRow = piece.getRow();
			board.showPossibleMoves(piece);
		}
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
		Move move = new Move(board, selectedPiece, column, row);
		if (board.isValidMove(move)) {
			executeMove(selectedPiece, move);
		} else {
			resetPiecePosition(selectedPiece);
		}
		clearSelection();
	}

	/**
	 * Updates the position of a piece during a drag operation.
	 *
	 * @param piece
	 * 	the piece being dragged
	 * @param event
	 * 	the mouse event
	 */
	private void updatePiecePositionDuringDrag(Piece piece, MouseEvent event) {
		int offsetX = board.getTileSize() / 2;
		int offsetY = board.getTileSize() / 2;
		piece.setXPos(event.getX() - offsetX);
		piece.setYPos(event.getY() - offsetY);
	}

	/**
	 * Finalizes the move after releasing the mouse. Validates and processes the move.
	 *
	 * @param event
	 * 	the mouse event
	 */
	private void finalizeMove(MouseEvent event) {
		Piece selectedPiece = board.getSelectedPiece();
		if (selectedPiece != null) {
			int column = event.getX() / board.getTileSize();
			int row = event.getY() / board.getTileSize();
			attemptMove(selectedPiece, column, row);
		}
	}

	/**
	 * Executes a valid move, capturing an opponent piece if necessary.
	 *
	 * @param selectedPiece
	 * 	the piece being moved
	 * @param move
	 * 	the move to execute
	 */
	private void executeMove(Piece selectedPiece, Move move) {
		Piece capturedPiece = board.getPieceAt(move.getNewColumn(), move.getNewRow());
		if (capturedPiece != null && capturedPiece.isWhite() != selectedPiece.isWhite()) {
			board.getPieceList().remove(capturedPiece);
		}
		selectedPiece.setPosition(move.getNewColumn(), move.getNewRow());
		board.makeMove(move);
	}

	/**
	 * Resets the position of a piece to its original position if the move is invalid.
	 *
	 * @param piece
	 * 	the piece to reset
	 */
	private void resetPiecePosition(Piece piece) {
		piece.setPosition(originalColumn, originalRow);
	}

	/**
	 * Clears the current selection and possible moves from the board.
	 */
	private void clearSelection() {
		board.setSelectedPiece(null);
		board.getPossibleMoves().clear();
	}
}
