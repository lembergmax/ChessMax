/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai.v1;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.engine.ai.Ai;
import com.mlprograms.chess.utils.ConfigFetcher;

public class Hikaru extends Ai {

	private static final int BOT_NUMBER = 2;

	public Hikaru() {
		super(
			ConfigFetcher.fetchStringConfig("Bots", "BOT_" + BOT_NUMBER + "_NAME"),
			ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_SPRITE"),
			ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_ELO"),
			ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + BOT_NUMBER + "_DEPTH")
		);
	}

	@Override
	protected Move findcStrategicMove() {
		return null;
	}
}
