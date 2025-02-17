/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
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

	private int oldColumn;
	private int oldRow;
	private int newColumn;
	private int newRow;

	@ToString.Exclude
	private Board board;

	private Piece piece;
	private Piece capturedPiece;

	public Move(Board board, Piece selectedPiece, int newColumn, int newRow) {
		Piece capturedPiece = board.getPieceList().stream().filter(piece -> piece.getColumn() == newColumn && piece.getRow() == newRow && piece.isWhite() != selectedPiece.isWhite()).findFirst().orElse(null);
		this(board, selectedPiece, newColumn, newRow, capturedPiece);
	}

	public Move(Board board, Piece piece, int newColumn, int newRow, Piece capturedPiece) {
		this.board = board;
		this.oldColumn = piece.getColumn();
		this.oldRow = piece.getRow();
		this.newColumn = newColumn;
		this.newRow = newRow;
		this.piece = piece;

		this.capturedPiece = capturedPiece;
	}

	/**
	 * Converts the move to algebraic notation.
	 * Example: e2, e4 -> "e4" or for pieces "Nxe4" etc.
	 */
	public String toAlgebraicNotation() {
		// Special case: Castling (assumption: King moves 2 columns)
		if (piece.getClass().getSimpleName().equals("King")) {
			if (newColumn - oldColumn == 2) {
				return "O-O";    // Kingside
			} else if (oldColumn - newColumn == 2) {
				return "O-O-O";  // Queenside
			}
		}

		StringBuilder notation = new StringBuilder();

		// Determine the piece abbreviation (usually omitted for pawns)
		String pieceAbbr = getPieceAbbreviation(piece);

		// For pawns capturing, add the origin file letter.
		if (pieceAbbr.isEmpty() && capturedPiece != null) {
			pieceAbbr = String.valueOf((char) ('a' + oldColumn));
		}
		notation.append(pieceAbbr);

		// If a capture occurs, add an "x".
		if (capturedPiece != null) {
			notation.append("x");
		}

		// Add the destination square (e.g., "e4").
		notation.append(convertToSquare(newColumn, newRow));

		return notation.toString();
	}

	/**
	 * Returns the typical abbreviation of the piece.
	 * For pawns, an empty string is returned.
	 */
	private String getPieceAbbreviation(Piece piece) {
		String pieceName = piece.getClass().getSimpleName();
		return switch (pieceName) {
			case "Knight" -> "N";
			case "Bishop" -> "B";
			case "Rook" -> "R";
			case "Queen" -> "Q";
			case "King" -> "K";
			default -> "";
		};
	}

	/**
	 * Converts column and row indices to a chess square.
	 * Assumption: Column 0 corresponds to "a" and row 0 corresponds to "1".
	 */
	private String convertToSquare(int column, int row) {
		char file = (char) ('a' + column);  // 0 -> 'a', 1 -> 'b', etc.
		int rank = row + 1;                // 0 -> 1, 1 -> 2, etc.
		return "" + file + rank;
	}

}
