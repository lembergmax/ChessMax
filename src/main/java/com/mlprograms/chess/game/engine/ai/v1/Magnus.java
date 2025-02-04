/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai.v1;

import com.mlprograms.chess.game.engine.ai.Ai;
import com.mlprograms.chess.utils.ConfigFetcher;

public class Magnus extends Ai {

	private static final int BOT_NUMBER = 30;

	public Magnus() {
		super(
			ConfigFetcher.fetchStringConfig("Bots", "BOT_" + BOT_NUMBER + "_NAME"),
			ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_SPRITE"),
			ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_ELO"),
			ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_DEPTH")
		);
	}

}
