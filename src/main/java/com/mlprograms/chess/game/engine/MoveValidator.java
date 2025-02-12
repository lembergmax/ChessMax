/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

import com.mlprograms.chess.game.pieces.Bishop;
import com.mlprograms.chess.game.pieces.King;
import com.mlprograms.chess.game.pieces.Knight;
import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.utils.Logger;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
// TODO: write many tests for this class
public class MoveValidator {

	private final Board board;

	public MoveValidator(Board board) {
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
		return isKingInCheck() && !canPlayerDoAnyValidMove();
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
		return !isKingInCheck() && !canPlayerDoAnyValidMove();
	}

	/**
	 * Determines whether the current player can make any valid moves.
	 * A valid move is a move that adheres to the game's rules and does not leave
	 * the player's king in check.
	 *
	 * @return true if the current player can make at least one valid move, false otherwise.
	 */
	public boolean canPlayerDoAnyValidMove() {
		List<Piece> pieces = new ArrayList<>(getBoard().getPieceList());
		return pieces.stream().anyMatch(piece -> piece.isWhite() == getBoard().isWhiteTurn() && !piece.getLegalMoves(getBoard()).isEmpty());
	}

	/**
	 * Determines if there is insufficient material on the board to continue the game.
	 * Insufficient material means that neither player has enough pieces to deliver a checkmate.
	 *
	 * @return true if there is insufficient material, false otherwise.
	 */
	public boolean isInsufficientMaterial() {
		// List of all current pieces on the board
		List<Piece> pieces = getBoard().getPieceList();

		// Count the remaining pieces
		int countKings = 0;
		int countBishops = 0;
		int countKnights = 0;
		int countOthers = 0;

		for (Piece piece : pieces) {
			switch (piece) {
				case King _ -> countKings++;
				case Bishop _ -> countBishops++;
				case Knight _ -> countKnights++;
				case null, default -> countOthers++;
			}
		}

		// Check the conditions for insufficient material
		if (countOthers > 0) {
			return false; // There are pieces on the board which can cause a checkmate
		}

		if (countKings == pieces.size()) {
			return true; // Only Kings left
		}

		if (countKings == 1 && (countBishops == 1 || countKnights == 1)) {
			return true; // Only one King and one Knight or one Bishop left
		}

		if (countKings == pieces.size() - 2 && countBishops == 2) {
			return areBishopsSameColor(pieces);
		}

		return false;
	}

	/**
	 * Checks if all bishops on the board are of the same color.
	 *
	 * @param pieces
	 * 	The list of pieces on the board.
	 *
	 * @return true if all bishops are of the same color, false otherwise.
	 */
	private boolean areBishopsSameColor(List<Piece> pieces) {
		boolean isFirstBishopWhite = false;
		boolean isFirstBishopSet = false;

		for (Piece piece : pieces) {
			if (piece instanceof Bishop) {
				if (!isFirstBishopSet) {
					isFirstBishopWhite = piece.isWhite();
					isFirstBishopSet = true;
				} else if (isFirstBishopWhite != piece.isWhite()) {
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * Checks if the fifty-move rule applies. The fifty-move rule states that a player can claim a draw
	 * if no pawn has been moved and no capture has been made in the last fifty moves by each player.
	 *
	 * @return true if the fifty-move rule applies, false otherwise.
	 */
	public boolean isFiftyMoveRule() {
		return getBoard().getHalfMoveClock() >= 50;
	}

	/**
	 * Determines if the current board position has occurred three times.
	 * The threefold repetition rule allows a player to claim a draw if the same position
	 * occurs three times with the same player to move and all possible moves.
	 *
	 * @return true if the current position has occurred three times, false otherwise.
	 */
	public boolean isThreefoldRepetition() {
		// Count the number of repetitions of the current position
		int repetitions = (int) getBoard().getMoveHistory().stream()
			                        .map(historyMove -> historyMove.getFenNotation().getFenString())
			                        .filter(fenString -> fenString.equals(getBoard().getCurrentPositionsFenNotation().getFenString()))
			                        .count();
		return repetitions >= 3;
	}

	// TODO: implement this method later
	public boolean isTimeForfeit() {
		return false;
	}

	// TODO: implement this method later
	public boolean isResignation() {
		return false;
	}

	// TODO: implement this method later
	public boolean isAgreedDraw() {
		return false;
	}

	/**
	 * Determines if making the specified move would put the player's own king in check.
	 * <p>
	 * This method temporarily applies the move to the board, checks if the king
	 * is in check after the move, and then restores the board to its original state.
	 *
	 * @param move
	 * 	The move to evaluate.
	 *
	 * @return true if the move would result in the player's king being in check, false otherwise.
	 */
	public boolean wouldMovePutKingInCheck(Move move) {
		// The piece currently located on the target square (if any)
		Piece targetPiece = getBoard().getPieceAt(move.getNewColumn(), move.getNewRow());

		// The piece that is being moved
		Piece movingPiece = move.getPiece();

		// The original position of the piece being moved
		int movingPieceOriginalColumn = movingPiece.getColumn();
		int movingPieceOriginalRow = movingPiece.getRow();

		// Simulating the capture of the target piece
		if (targetPiece != null) {
			getBoard().getPieceList().remove(targetPiece);
		}

		// Move the piece to the target square
		getBoard().setPieceAt(move.getNewColumn(), move.getNewRow(), movingPiece);
		getBoard().setPieceAt(movingPieceOriginalColumn, movingPieceOriginalRow, null);
		movingPiece.setPosition(move.getNewColumn(), move.getNewRow());

		// Check if the king is in check after the move
		boolean isKingInCheckAfterMove = isKingInCheck();

		// Restore the board to its original state
		if (targetPiece != null) {
			getBoard().getPieceList().add(targetPiece);
		}

		getBoard().setPieceAt(move.getNewColumn(), move.getNewRow(), targetPiece);
		movingPiece.setPosition(movingPieceOriginalColumn, movingPieceOriginalRow);

		return isKingInCheckAfterMove;
	}

	/**
	 * Checks if the current player's king is in check.
	 *
	 * @return true if the current player's king is in check, false otherwise.
	 */
	public boolean isKingInCheck() {
		return isKingInCheck(getBoard().isWhiteTurn());
	}

	/**
	 * Checks if the specified king is in check on the given getBoard().
	 *
	 * @param whiteKing
	 * 	true if evaluating the white king, false for the black king.
	 *
	 * @return true if the specified king is in check, false otherwise.
	 */
	public boolean isKingInCheck(boolean whiteKing) {
		King king = findKing(whiteKing);
		if (king == null) {
			return false;
		}

		int kingColumn = king.getColumn();
		int kingRow = king.getRow();

		return getBoard().getPieceList().stream()
			       .filter(piece -> piece.isWhite() != whiteKing)
			       .anyMatch(piece -> piece.isValidMovement(kingColumn, kingRow, false));
	}

	/**
	 * Finds the king of the specified color on the getBoard().
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
