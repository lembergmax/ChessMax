/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess;

import com.mlprograms.chess.game.ChessMax;
import com.mlprograms.chess.game.engine.ai.presets.Martin;
import com.mlprograms.chess.human.Human;

public class Main {

	public static void main(String[] args) {
		new ChessMax(new Human(), new Martin(), true).play();
	}

}
