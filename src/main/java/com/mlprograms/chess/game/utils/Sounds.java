/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.utils;

import com.mlprograms.chess.utils.ConfigReader;

import java.util.HashMap;
import java.util.Map;

public class Sounds {

	public static final String MOVE = "MOVE";
	public static final String CAPTURE = "CAPTURE";
	public static final String CHECK = "CHECK";
	public static final String GAME_END = "GAME_END";
	public static final String GAME_START = "GAME_START";
	public static final String CASTLE = "CASTLE";
	public static final String ILLEGAL_MOVE = "ILLEGAL_MOVE";

	private static final ConfigReader CONFIG_READER = new ConfigReader();
	private static final Map<String, String> SOUND_PATHS = new HashMap<>();

	static {
		loadSoundPaths();
	}

	private Sounds() {
		// Utility class, no instances allowed.
	}

	/**
	 * Fetches the file path for a given sound identifier.
	 *
	 * @param soundKey
	 * 	The key for the sound (e.g., MOVE, CAPTURE).
	 *
	 * @return The file path to the sound file.
	 */
	public static String getFilePath(String soundKey) {
		return SOUND_PATHS.getOrDefault(soundKey, "");
	}

	/**
	 * Loads sound paths from the configuration file.
	 */
	private static void loadSoundPaths() {
		String soundSection = "Sounds";
		SOUND_PATHS.put(MOVE, CONFIG_READER.getValue(soundSection, MOVE));
		SOUND_PATHS.put(CAPTURE, CONFIG_READER.getValue(soundSection, CAPTURE));
		SOUND_PATHS.put(CHECK, CONFIG_READER.getValue(soundSection, CHECK));
		SOUND_PATHS.put(GAME_END, CONFIG_READER.getValue(soundSection, GAME_END));
		SOUND_PATHS.put(GAME_START, CONFIG_READER.getValue(soundSection, GAME_START));
		SOUND_PATHS.put(CASTLE, CONFIG_READER.getValue(soundSection, CASTLE));
		SOUND_PATHS.put(ILLEGAL_MOVE, CONFIG_READER.getValue(soundSection, ILLEGAL_MOVE));
	}
}