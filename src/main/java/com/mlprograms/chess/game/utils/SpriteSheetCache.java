/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.utils;

import com.mlprograms.chess.utils.ConfigFetcher;
import com.mlprograms.chess.utils.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

/**
 * A utility class for caching the sprite sheet used for chess pieces.
 */
public class SpriteSheetCache {

	private static BufferedImage piecesSpriteSheet;

	/**
	 * Retrieves the sprite sheet for chess pieces. If the sprite sheet is not already loaded,
	 * it loads it from the resource path specified in the configuration.
	 *
	 * @return the sprite sheet for chess pieces
	 */
	public static BufferedImage getPiecesSpriteSheet() {
		if (piecesSpriteSheet == null) {
			try {
				String resourcePath = ConfigFetcher.fetchStringConfig("Images", "PIECES_SPRITE");

				piecesSpriteSheet = ImageIO.read(Objects.requireNonNull(
					SpriteSheetCache.class.getResourceAsStream(resourcePath)
				));
			} catch (IOException e) {
				Logger.logError(e.getMessage());
			}
		}
		return piecesSpriteSheet;
	}
}