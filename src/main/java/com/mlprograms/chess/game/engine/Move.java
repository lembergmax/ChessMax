/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Move {

	// 0-based indices: column 0 corresponds to 'a' and row 0 corresponds to '1'
	private int oldColumn;
	private int oldRow;
	private String oldAlgebraicColumn; // e.g., "e2"

	private int newColumn;
	private int newRow;
	private String newAlgebraicColumn; // e.g., "e4"

	@ToString.Exclude
	private Board board;

	private Piece piece;
	private Piece capturedPiece;

	/**
	 * Constructs a Move by automatically determining if an opponent's piece is captured at the destination.
	 *
	 * @param board
	 * 	The chess board.
	 * @param selectedPiece
	 * 	The piece to be moved.
	 * @param newColumn
	 * 	The destination column (0-based).
	 * @param newRow
	 * 	The destination row (0-based).
	 */
	public Move(Board board, Piece selectedPiece, int newColumn, int newRow) {
		Piece capturedPiece = board.getPieceList().stream()
			                      .filter(piece -> piece.getColumn() == newColumn && piece.getRow() == newRow
				                                       && piece.isWhite() != selectedPiece.isWhite())
			                      .findFirst()
			                      .orElse(null);
		this(board, selectedPiece, newColumn, newRow, capturedPiece);
	}

	/**
	 * Constructs a Move with explicit details, including a captured piece if any.
	 *
	 * @param board
	 * 	The chess board.
	 * @param piece
	 * 	The piece being moved.
	 * @param newColumn
	 * 	The destination column (0-based).
	 * @param newRow
	 * 	The destination row (0-based).
	 * @param capturedPiece
	 * 	The piece captured during the move, if any.
	 */
	public Move(Board board, Piece piece, int newColumn, int newRow, Piece capturedPiece) {
		this.board = board;
		this.oldColumn = piece.getColumn();
		this.oldRow = piece.getRow();
		this.newColumn = newColumn;
		this.newRow = newRow;
		this.piece = piece;
		this.capturedPiece = capturedPiece;

		// Convert indices to algebraic notation (e.g., "e2")
		this.oldAlgebraicColumn = convertToSquare(oldColumn, oldRow);
		this.newAlgebraicColumn = convertToSquare(newColumn, newRow);
	}

	/**
	 * Calls toAlgebraicNotation(promotedTo: String, boolean: isKingInCheck) Converts the move into algebraic notation.
	 *
	 * @return The move in algebraic notation.
	 */
	public String toAlgebraicNotation() {
		return toAlgebraicNotation("");
	}

	/**
	 * Converts the move into algebraic notation.
	 * For example, a move from e2 to e4 is represented as "e4" for pawns or "Nxe4" for knights.
	 * Also handles castling as a special case.
	 *
	 * @param promotedTo
	 * 	The piece to which a pawn is promoted, if any (e.g., "Q" for queen).
	 *
	 * @return The move in algebraic notation.
	 */
	public String toAlgebraicNotation(String promotedTo) {
		// Special case: Castling (assumption: King moves 2 columns)
		if (piece.getClass().getSimpleName().equals("King")) {
			if (newColumn - oldColumn == 2) {
				return "O-O";    // Kingside castling
			} else if (oldColumn - newColumn == 2) {
				return "O-O-O";  // Queenside castling
			}
		}

		StringBuilder notation = new StringBuilder();

		// Determine the piece abbreviation (empty for pawns)
		String pieceAbbr = getPieceAbbreviation(piece);

		// For pawn captures, include the originating file letter.
		if (pieceAbbr.isEmpty() && capturedPiece != null) {
			pieceAbbr = String.valueOf(
				getBoard().isWhiteAtBottom()
					? (char) ('a' + oldColumn)
					: (char) ('a' + getBoard().getColumns() - 1 - oldColumn)
			);
		}

		notation.append(pieceAbbr);

		// If a capture occurs, add an "x".
		if (capturedPiece != null) {
			notation.append("x");
		}

		// Append the destination square (e.g., "e4").
		notation.append(convertToSquare(newColumn, newRow));

		// Handle promotion, if applicable
		if (!promotedTo.isEmpty()) {
			notation.append("=").append(promotedTo);
		}

		// Append check or checkmate symbols, if applicable
		if (getBoard().getGameEnding() == GameEnding.CHECKMATE) {
			notation.append("#");
		} else if (getBoard().isMoveHistoryKingInCheck()) {
			getBoard().setMoveHistoryKingInCheck(false);
			notation.append("+");
		}

		return notation.toString();
	}

	/**
	 * Returns the standard abbreviation for the given piece.
	 * For pawns, an empty string is returned.
	 *
	 * @param piece
	 * 	The chess piece.
	 *
	 * @return The abbreviation for the piece.
	 */
	private String getPieceAbbreviation(Piece piece) {
		String character = String.valueOf(piece.getFenChar()).toUpperCase();
		return character.equals("P") ? "" : character;
	}

	/**
	 * Converts the given column and row indices to a square in algebraic notation.
	 * The file (column) and rank (row) are determined based on the board's orientation and the current player's turn.
	 *
	 * @param column
	 * 	the column index (0-based)
	 * @param row
	 * 	the row index (0-based)
	 *
	 * @return the square in algebraic notation (e.g., "e4")
	 */
	private String convertToSquare(int column, int row) {
		char file = (char) ('a' + getBoard().getColumns() - 1 - column);
		int rank = row + 1;

		if (getBoard().isWhiteAtBottom()) {
			file = (char) ('a' + column);
			rank = getBoard().getRows() - row;
		}

		return "" + file + rank;
	}
}
