/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class FenNotation {

	private String fenString;
	private String castlingRights;
	private String enPassantTile;
	private int halfMoveClock;
	private int fullMoveNumber;
	private boolean whiteToMove;

}
