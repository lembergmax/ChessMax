/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils.ui;

import com.mlprograms.chess.utils.ConfigReader;

public class ConfigFetcher {

	private static final ConfigReader CONFIG_READER = new ConfigReader();

	/**
	 * Fetches a string configuration value from the ChessGame section.
	 *
	 * @param key
	 * 	The key of the configuration value.
	 * @param section
	 * 	The section of the configuration file.
	 *
	 * @return The string value associated with the key.
	 */
	public static String fetchStringConfig(String section, String key) {
		return CONFIG_READER.getValue(section, key);
	}

	/**
	 * Fetches an integer configuration value from the ChessGame section.
	 *
	 * @param key
	 * 	The key of the configuration value.
	 * @param section
	 * 	The section of the configuration file.
	 *
	 * @return The integer value associated with the key.
	 *
	 * @throws NumberFormatException
	 * 	if the configuration value is not a valid integer.
	 */
	public static int fetchIntegerConfig(String section, String key) {
		return Integer.parseInt(fetchStringConfig(section, key));
	}

}
