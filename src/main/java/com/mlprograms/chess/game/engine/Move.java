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

	private Board board;
	private Piece piece;
	private Piece capturedPiece;

	public Move(Board board, Piece selectedPiece, int newColumn, int newRow) {
		Piece capturedPiece = board.getPieceList().stream().filter(piece -> piece.getColumn() == newColumn && piece.getRow() == newColumn && piece.isWhite() != selectedPiece.isWhite()).findFirst().orElse(null);
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

}
