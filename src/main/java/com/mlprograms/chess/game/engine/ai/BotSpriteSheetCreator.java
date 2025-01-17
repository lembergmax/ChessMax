/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai;

import com.mlprograms.chess.utils.ConfigFetcher;
import com.mlprograms.chess.utils.Logger;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class BotSpriteSheetCreator {

	private static final int SPRITE_SIZE = ConfigFetcher.fetchIntegerConfig("Images", "BOTS_SPRITE_SIZE");

	public static void createSpriteSheet() {
		String botFolder = ConfigFetcher.fetchStringConfig("Images", "BOT_FOLDER");
		String outputSpritePath = ConfigFetcher.fetchStringConfig("Images", "BOT_SPRITE_ABS");

		try {
			File botFolderFile = new File(botFolder);
			File[] imageFiles = botFolderFile.listFiles((dir, name) -> name.toLowerCase().endsWith(".png"));

			if (imageFiles == null || imageFiles.length == 0) {
				Logger.logError("No PNG images found in the specified folder.");
				return;
			}

			int numberOfImages = imageFiles.length;
			int sheetWidth = numberOfImages * SPRITE_SIZE;

			// Create a blank sprite sheet large enough to hold all images horizontally
			BufferedImage spriteSheet = new BufferedImage(sheetWidth, SPRITE_SIZE, BufferedImage.TYPE_INT_ARGB);
			Graphics2D g2d = spriteSheet.createGraphics();

			int xPosition = 0;

			// Read all images from the bot folder and append them horizontally
			for (File imageFile : imageFiles) {
				BufferedImage image = ImageIO.read(imageFile);

				// Draw the current image onto the sprite sheet
				g2d.drawImage(image, xPosition, 0, null);

				// Update the x position for the next image
				xPosition += image.getWidth();
			}

			// Dispose of graphics context
			g2d.dispose();

			// Save the combined sprite sheet to a file
			File outputSpriteFile = new File(outputSpritePath);
			ImageIO.write(spriteSheet, "png", outputSpriteFile);

			Logger.logSuccess("Sprite sheet created successfully!");

		} catch (IOException e) {
			Logger.logError("Failed to create sprite sheet: " + e.getMessage());
		}
	}
}
