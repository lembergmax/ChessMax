/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@EqualsAndHashCode
@ToString
public class Arrow {
	private int startColumn;
	private int startRow;
	private int endColumn;
	private int endRow;

	public Arrow(int startColumn, int startRow, int endColumn, int endRow) {
		this.startColumn = startColumn;
		this.startRow = startRow;
		this.endColumn = endColumn;
		this.endRow = endRow;
	}

}
