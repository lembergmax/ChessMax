/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.utils;

import com.mlprograms.chess.utils.Logger;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import java.io.File;

public class SoundPlayer {

	/**
	 * Plays a sound based on the provided sound key.
	 *
	 * @param soundKey
	 * 	The key for the sound (e.g., MOVE, CAPTURE).
	 */
	public void play(String soundKey) {
		String soundFilePath = Sounds.getFilePath(soundKey);
		if (soundFilePath == null || soundFilePath.isEmpty()) {
			Logger.logError("Sound key not found or file path is empty: " + soundKey);
			return;
		}

		try {
			File soundFile = new File(soundFilePath);
			if (!soundFile.exists()) {
				Logger.logError("Sound file not found: " + soundFilePath);
				return;
			}

			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(soundFile);
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
		} catch (Exception e) {
			Logger.logError("Error playing sound: " + soundFilePath);
			e.printStackTrace();
		}
	}
}
