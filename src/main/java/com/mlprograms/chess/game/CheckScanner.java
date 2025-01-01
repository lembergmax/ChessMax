/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game;

import com.mlprograms.chess.game.action.Move;
import com.mlprograms.chess.game.pieces.King;
import com.mlprograms.chess.game.ui.Board;
import lombok.Getter;

@Getter
// TODO: write many tests for this class
public class CheckScanner {

	private final Board board;

	public CheckScanner(Board board) {
		this.board = board;
	}

	/**
	 * Determines if the current player is in checkmate. A player is in checkmate if:
	 * <p>
	 * 1. Their king is in check.
	 * <p>
	 * 2. No legal moves are available for any of their pieces.
	 *
	 * @return true if the current player is in checkmate, false otherwise.
	 */
	public boolean isCheckmate() {
		return isKingInCheck() && canPlayerDoAnyValidMove();
	}

	/**
	 * Determines if the current player is in stalemate. A player is in stalemate if:
	 * <p>
	 * 1. Their king is not in check.
	 * <p>
	 * 2. No legal moves are available for any of their pieces.
	 *
	 * @return true if the current player is in stalemate, false otherwise.
	 */
	public boolean isStalemate() {
		return !isKingInCheck() && canPlayerDoAnyValidMove();
	}

	/**
	 * Determines whether the current player can make any valid moves.
	 * A valid move is a move that adheres to the game's rules and does not leave
	 * the player's king in check.
	 *
	 * @return true if the current player can make at least one valid move, false otherwise.
	 */
	public boolean canPlayerDoAnyValidMove() {
		return getBoard().getPieceList().stream()
			       .allMatch(piece -> piece.getLegalMoves(getBoard()).isEmpty());
	}

	// TODO: implement
	public boolean wouldMovePutKingInCheck(Move move) {
		return false;
	}

	/**
	 * Checks if the current player's king is in check.
	 * A king is in check if it is under direct attack by an opponent's piece.
	 *
	 * @return true if the current player's king is in check, false otherwise.
	 */
	public boolean isKingInCheck() {
		return isKingInCheck(getBoard(), getBoard().isWhiteTurn());
	}

	/**
	 * Checks if the specified king is in check on the given board.
	 * A king is in check if it is under direct attack by an opponent's piece.
	 *
	 * @param board
	 * 	the board state to evaluate.
	 * @param whiteKing
	 * 	true if evaluating the white king, false for the black king.
	 *
	 * @return true if the specified king is in check, false otherwise.
	 */
	public boolean isKingInCheck(Board board, boolean whiteKing) {
		King king = findKing(whiteKing);
		if (king == null) {
			return false;
		}

		int kingColumn = king.getColumn();
		int kingRow = king.getRow();

		return board.getPieceList().stream()
			       .filter(piece -> piece.isWhite() != whiteKing)
			       .anyMatch(piece -> piece.isValidMovement(kingColumn, kingRow));
	}

	/**
	 * Finds the current player's king on the board.
	 *
	 * @return the current player's king, or null if the king is not found.
	 */
	public King findKing() {
		return findKing(getBoard().isWhiteTurn());
	}

	/**
	 * Finds the king of the specified color on the board.
	 *
	 * @param whiteKing
	 * 	true if searching for the white king, false for the black king.
	 *
	 * @return the specified king, or null if the king is not found.
	 */
	public King findKing(boolean whiteKing) {
		return getBoard().getPieceList().stream()
			       .filter(piece -> piece instanceof King && piece.isWhite() == whiteKing)
			       .map(piece -> (King) piece)
			       .findFirst()
			       .orElse(null);
	}

}
