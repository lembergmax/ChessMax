/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
public class FenNotation {

	private String fenString;
	private String castlingRights;
	private String enPassantTile;
	private int halfMoveClock;
	private int fullMoveNumber;
	private boolean whiteToMove;

	@Override
	public String toString() {
		return fenString + castlingRights + " " + enPassantTile + " " + halfMoveClock + " " + fullMoveNumber;
	}
}
