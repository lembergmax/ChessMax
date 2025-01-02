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
@ToString
public class HistoryMove {

	private Move madeMove;
	private FenNotation fen;

	public HistoryMove(Move madeMove, FenNotation fen) {
		this.madeMove = madeMove;
		this.fen = fen;
	}

}
