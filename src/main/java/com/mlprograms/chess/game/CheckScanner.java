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

	// TODO: write java doc
	public boolean canPlayerDoAnyValidMove() {
		return getBoard().getPieceList().stream()
			       .allMatch(piece -> piece.getLegalMoves(getBoard()).isEmpty());
	}

	// TODO: check before each move if king is after move in check
	public boolean wouldMovePutKingInCheck(Move move) {
		return false;
	}

	// TODO: write java doc
	public boolean isKingInCheck() {
		return isKingInCheck(getBoard(), getBoard().isWhiteTurn());
	}

	// TODO: write java doc
	public boolean isKingInCheck(Board board, boolean whiteKing) {
		int kingColumn = findKing(whiteKing).getColumn();
		int kingRow = findKing(whiteKing).getRow();

		return board.getPieceList().stream()
			       .filter(piece -> piece.isWhite() != whiteKing)
			       .anyMatch(piece -> piece.isValidMovement(kingColumn, kingRow));
	}

	// TODO: write java doc
	public King findKing() {
		return findKing(getBoard().isWhiteTurn());
	}

	// TODO: write java doc
	public King findKing(boolean whiteKing) {
		return getBoard().getPieceList().stream()
			       .filter(piece -> piece instanceof King && piece.isWhite() == whiteKing)
			       .map(piece -> (King) piece)
			       .findFirst()
			       .orElse(null);
	}

}
