/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.utils;

import com.mlprograms.chess.utils.ConfigFetcher;
import org.junit.jupiter.api.Test;

import java.awt.*;

import static org.junit.jupiter.api.Assertions.*;

class ConfigFetcherTest {

	@Test
	void testFetchStringConfig() {
		String title = ConfigFetcher.fetchStringConfig("ChessGame", "TITLE");
		assertEquals("ChessMax", title, "Der Titel sollte 'ChessMax' sein.");
	}

	@Test
	void testFetchIntegerConfig() {
		int width = ConfigFetcher.fetchIntegerConfig("ChessGame", "WIDTH");
		assertEquals(1200, width, "Die Breite sollte 1200 sein.");
	}

	@Test
	void testFetchColorConfig() {
		Color color = ConfigFetcher.fetchColorConfig("Colors", "TILE_LIGHT");
		assertEquals(new Color(0xFFE19C), color, "Die Farbe sollte #FFE19C sein.");
	}

	@Test
	void testFetchColorWithAlphaConfig() {
		Color color = ConfigFetcher.fetchColorWithAlphaConfig("Colors", "TILE_HIGHLIGHT", 135);
		assertEquals(new Color(106, 191, 105, 135), color, "Die Farbe sollte #6ABF69 mit Alpha 135 sein.");
	}
}