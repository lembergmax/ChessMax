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

	private int moveNumber;
	private String moveAlgebraic;
	private Move move;
	private FenNotation fenNotation;

	public HistoryMove(int moveNumber, String moveAlgebraic, Move move, FenNotation fenNotation) {
		this.moveNumber = moveNumber;
		this.moveAlgebraic = moveAlgebraic;
		this.move = move;
		this.fenNotation = fenNotation;
	}
}
