/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * A utility class for reading and parsing values from a .config file.
 * <p>
 * Supports retrieving configuration values organized by sections and keys.
 * </p>
 */
public class ConfigReader {

	// Stores the parsed configuration data: Section -> (Key -> Value)
	private final Map<String, Map<String, String>> CONFIG_DATA = new HashMap<>();

	/**
	 * Initializes a ConfigReader by loading and parsing the .config file.
	 */
	public ConfigReader() {
		try {
			loadConfigFile("configuration.config");
		} catch (IOException e) {
			Logger.logError("Failed to read configuration file: " + e.getMessage());
		}
	}

	/**
	 * Retrieves the value associated with a specific section and key in the configuration.
	 *
	 * @param section
	 * 	the section name (e.g., "Database")
	 * @param key
	 * 	the key name (e.g., "DB_PATH")
	 *
	 * @return the value associated with the given section and key, or {@code null} if not found
	 */
	public String getValue(String section, String key) {
		Map<String, String> sectionData = CONFIG_DATA.get(section);
		return sectionData != null ? sectionData.get(key) : null;
	}

	/**
	 * Reads and parses the .config file, storing its contents in memory.
	 * <p>
	 * The file should be structured in sections (e.g., [Section]) with key-value pairs.
	 * </p>
	 *
	 * @param filePath
	 * 	the path to the .config file
	 *
	 * @throws IOException
	 * 	if an I/O error occurs
	 */
	private void loadConfigFile(String filePath) throws IOException {
		try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
			String line;
			String currentSection = null;

			while ((line = reader.readLine()) != null) {
				line = line.trim();

				// Ignore empty lines and comments (lines starting with '#')
				if (line.isEmpty() || line.startsWith("#")) {
					continue;
				}

				if (isSectionHeader(line)) {
					// Extract section name and create a new section if needed
					currentSection = extractSectionName(line);
					CONFIG_DATA.putIfAbsent(currentSection, new HashMap<>());
				} else if (currentSection != null && isKeyValuePair(line)) {
					// Parse and store key-value pairs within the current section
					addKeyValuePair(currentSection, line);
				}
			}
		}
	}

	/**
	 * Checks if a line represents a section header (e.g., [Section]).
	 *
	 * @param line
	 * 	the line to check
	 *
	 * @return {@code true} if the line is a valid section header; {@code false} otherwise
	 */
	private boolean isSectionHeader(String line) {
		return line.startsWith("[") && line.endsWith("]");
	}

	/**
	 * Extracts the section name from a section header line (e.g., "[Database]" -> "Database").
	 *
	 * @param line
	 * 	the section header line
	 *
	 * @return the extracted section name
	 */
	private String extractSectionName(String line) {
		return line.substring(1, line.length() - 1).trim();
	}

	/**
	 * Checks if a line contains a key-value pair (e.g., "key=value").
	 *
	 * @param line
	 * 	the line to check
	 *
	 * @return {@code true} if the line contains a key-value pair; {@code false} otherwise
	 */
	private boolean isKeyValuePair(String line) {
		return line.contains("=");
	}

	/**
	 * Parses a key-value pair and adds it to the specified section.
	 *
	 * @param section
	 * 	the section to which the key-value pair belongs
	 * @param line
	 * 	the line containing the key-value pair
	 */
	private void addKeyValuePair(String section, String line) {
		String[] keyValue = line.split("=", 2);
		if (keyValue.length == 2) {
			String key = keyValue[ 0 ].trim();
			String value = keyValue[ 1 ].trim();
			CONFIG_DATA.get(section).put(key, value);
		}
	}
}
