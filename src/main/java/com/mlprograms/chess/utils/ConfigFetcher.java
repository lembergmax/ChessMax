/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import java.awt.*;

/**
 * Utility class for fetching configuration values from a configuration reader.
 */
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
		String value = fetchStringConfig(section, key);
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			throw new IllegalArgumentException(
				String.format("Invalid integer value for section '%s', key '%s': %s", section, key, value), e
			);
		}
	}

	/**
	 * Fetches a color configuration value from the "Colors" section based on the provided key.
	 *
	 * @param key
	 * 	the key whose value is to be fetched from the "Colors" section.
	 *
	 * @return the configuration value as a {@link Color}.
	 *
	 * @throws NumberFormatException
	 * 	if the fetched value is not a valid hexadecimal color code.
	 */
	public static Color fetchColorConfig(String section, String key) {
		return decodeColor(fetchStringConfig(section, key));
	}

	/**
	 * Fetches a color configuration value from the "Colors" section with a specific alpha value.
	 *
	 * @param key
	 * 	the key whose value is to be fetched from the "Colors" section.
	 * @param alpha
	 * 	the alpha value for the color (0-255).
	 *
	 * @return the configuration value as a {@link Color} with the specified alpha value.
	 *
	 * @throws IllegalArgumentException
	 * 	if the alpha value is out of the valid range (0-255).
	 */
	public static Color fetchColorWithAlphaConfig(String section, String key, int alpha) {
		if (alpha < 0 || alpha > 255) {
			throw new IllegalArgumentException("Alpha value must be between 0 and 255: " + alpha);
		}

		Color baseColor = decodeColor(fetchStringConfig(section, key));
		return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), alpha);
	}

	/**
	 * Decodes a color from a hexadecimal string.
	 *
	 * @param hex
	 * 	the hexadecimal color string (e.g., "#FFFFFF").
	 *
	 * @return the decoded {@link Color}.
	 *
	 * @throws NumberFormatException
	 * 	if the string is not a valid hexadecimal color code.
	 */
	private static Color decodeColor(String hex) {
		return Color.decode(hex);
	}

}
