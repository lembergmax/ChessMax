/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import java.awt.*;

public class ConfigFetcher {

	private static final ConfigReader CONFIG_READER = new ConfigReader();

	/**
	 * Fetches a string configuration value based on the given section and key.
	 *
	 * @param section
	 * 	the configuration section to look into (e.g., "General", "Settings").
	 * @param key
	 * 	the key whose value is to be fetched from the specified section.
	 *
	 * @return the configuration value as a String.
	 */
	public static String fetchStringConfig(String section, String key) {
		return CONFIG_READER.getValue(section, key);
	}

	/**
	 * Fetches an integer configuration value based on the given section and key.
	 * <p>
	 * This method retrieves the value as a string and converts it to an integer.
	 *
	 * @param section
	 * 	the configuration section to look into (e.g., "General", "Settings").
	 * @param key
	 * 	the key whose value is to be fetched from the specified section.
	 *
	 * @return the configuration value as an integer.
	 *
	 * @throws NumberFormatException
	 * 	if the fetched value is not a valid integer.
	 */
	public static int fetchIntegerConfig(String section, String key) {
		return Integer.parseInt(fetchStringConfig(section, key));
	}

	/**
	 * Fetches an integer configuration value from the "Colors" section based on the provided key.
	 * <p>
	 * This method is specialized for retrieving color-related configuration values.
	 *
	 * @param key
	 * 	the key whose value is to be fetched from the "Colors" section.
	 *
	 * @return the configuration value as an integer.
	 *
	 * @throws NumberFormatException
	 * 	if the fetched value is not a valid integer.
	 */
	public static Color fetchColorConfig(String key) {
		return Color.decode(fetchStringConfig("Colors", key));
	}

}
