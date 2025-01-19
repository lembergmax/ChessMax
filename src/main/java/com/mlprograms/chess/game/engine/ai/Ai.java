/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai;

import com.mlprograms.chess.game.Player;
import com.mlprograms.chess.utils.ConfigFetcher;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Objects;

@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString
public class Ai {

	// Name of the AI
	private String name;

	// The difficulty level of the AI
	private int elo;

	// The maximum depth the AI will search to
	private int depth;

	// Scale factor for the sprite sheet
	private int sheetScale;

	// The sprite number for this piece
	private int spriteNumber;

	// The sprite sheet containing the piece image
	private BufferedImage sheet;

	// The specific sprite for this piece
	private Image sprite;

	public Ai(String name, int sprite, int elo, int depth) {
		super();
		setName(name);
		setElo(elo);
		setDepth(depth);

		// Calculate the scale factor based on the sprite sheet dimensions
		setSheetScale(ConfigFetcher.fetchIntegerConfig("Images", "BOTS_SPRITE_SIZE"));
		setSpriteNumber(sprite);

		try {
			setSheet(ImageIO.read(Objects.requireNonNull(getClass().getResourceAsStream(ConfigFetcher.fetchStringConfig("Images", "BOT_SPRITE")))));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		setSprite(getSheet().getSubimage((sprite - 1) * sheetScale, 0, sheetScale, sheetScale).getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));

	}

}
