/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.utils;

import com.mlprograms.chess.utils.ConfigReader;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ConfigReaderTest {

	private ConfigReader configReader;

	@BeforeEach
	void setUp() {
		configReader = new ConfigReader();
	}

	@Test
	void testGetValue() {
		String value = configReader.getValue("ChessGame", "TITLE");
		assertEquals("ChessMax", value, "Der Wert sollte 'ChessMax' sein.");
	}

	@Test
	void testInvalidSection() {
		String value = configReader.getValue("InvalidSection", "TITLE");
		assertNull(value, "Der Wert sollte null sein, wenn die Sektion ungültig ist.");
	}

	@Test
	void testInvalidKey() {
		String value = configReader.getValue("ChessGame", "INVALID_KEY");
		assertNull(value, "Der Wert sollte null sein, wenn der Schlüssel ungültig ist.");
	}
}