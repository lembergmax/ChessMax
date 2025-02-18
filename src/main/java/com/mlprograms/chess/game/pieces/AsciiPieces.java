/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.pieces;

import lombok.Getter;

@Getter
public enum AsciiPieces {

	WHITE_KING("♔"),
	WHITE_QUEEN("♕"),
	WHITE_ROOK("♖"),
	WHITE_BISHOP("♗"),
	WHITE_KNIGHT("♘"),
	WHITE_PAWN("♙"),

	BLACK_KING("♚"),
	BLACK_QUEEN("♛"),
	BLACK_ROOK("♜"),
	BLACK_BISHOP("♝"),
	BLACK_KNIGHT("♞"),
	BLACK_PAWN("♟");

	private final String SYMBOL;

	AsciiPieces(String symbol) {
		this.SYMBOL = symbol;
	}
}
