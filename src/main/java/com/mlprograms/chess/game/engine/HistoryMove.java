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
@ToString
@EqualsAndHashCode
public class HistoryMove {

	private Move madeMove;
	private FenNotation fenNotation;

	public HistoryMove(Move madeMove, FenNotation fen) {
		this.madeMove = madeMove;
		this.fenNotation = fen;
	}

}
