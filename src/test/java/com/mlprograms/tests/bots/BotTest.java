/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.bots;

import com.mlprograms.chess.game.engine.ai.Ai;
import com.mlprograms.chess.game.engine.ai.BotSpriteSheetCreator;
import com.mlprograms.chess.game.engine.ai.presets.Martin;
import com.mlprograms.chess.utils.ConfigFetcher;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import com.mlprograms.chess.utils.Logger;
import org.junit.jupiter.api.Test;

class BotTest {

	@Test
	void testCorrectSprite() {
		// Create an instance of the Martin bot
		Ai bot = new Martin();

		// Extract the sprite of the bot
		BotSpriteSheetCreator.createSpriteSheet();
		BufferedImage spriteImage = extractSpriteImage(bot);

		if (spriteImage != null) {
			try {
				// Define the path to save the image on the Desktop
				String desktopPath = System.getProperty("user.home") + "/Desktop/";
				File outputFile = new File(desktopPath + "Bot_Image.png");

				// Write the image to the Desktop as a PNG
				ImageIO.write(spriteImage, "png", outputFile);
				Logger.logSuccess("Image successfully saved to: " + outputFile.getAbsolutePath());
			} catch (IOException e) {
				Logger.logError("Error while saving the image: " + e.getMessage());
			}
		} else {
			Logger.logError("Failed to extract sprite image for the bot.");
		}
	}

	/**
	 * Extracts the sprite image from the bot's sprite sheet.
	 *
	 * @param bot The AI bot instance.
	 * @return The extracted sprite as a BufferedImage.
	 */
	private BufferedImage extractSpriteImage(Ai bot) {
		try {
			int spriteIndex = ConfigFetcher.fetchIntegerConfig("Bots", "BOT_" + bot.getSpriteNumber() + "_SPRITE") - 1;
			int sheetScale = bot.getSheetScale();
			BufferedImage sheet = bot.getSheet();

			// Validate dimensions to avoid Raster errors
			if ((spriteIndex + 1) * sheetScale > sheet.getWidth()) {
				throw new IllegalArgumentException("Sprite index is out of bounds for the sprite sheet.");
			}

			// Extract the subimage (sprite) from the sprite sheet
			return sheet.getSubimage(spriteIndex * sheetScale, 0, sheetScale, sheetScale);
		} catch (Exception e) {
			Logger.logError("Error extracting sprite image: " + e.getMessage());
			return null;
		}
	}
}
