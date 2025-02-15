/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.engine.ai;

import com.mlprograms.chess.game.Player;
import com.mlprograms.chess.game.engine.Move;
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

/**
 * Abstract class representing an AI player in the chess game.
 * <p>
 * This class extends {@link Player} and provides common functionality for AI players,
 * such as handling sprite images and executing moves.
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
@ToString(callSuper = true)
public abstract class Ai extends Player {

	// Name of the AI player
	private String name;
	// The AI's Elo rating representing its difficulty level
	private int elo;
	// Maximum search depth for the AI's move calculations
	private int depth;
	// Scale factor for the sprite sheet images
	private int sheetScale;
	// Sprite number used to identify the AI's piece image
	private int spriteNumber;
	// The sprite sheet containing the image for the AI
	private BufferedImage sheet;
	// The specific sprite image for this AI
	private Image sprite;

	/**
	 * Constructs an AI player with the specified name, sprite number, Elo rating, and search depth.
	 *
	 * @param name
	 * 	the name of the AI player
	 * @param sprite
	 * 	the sprite number for this AI's image
	 * @param elo
	 * 	the Elo rating (difficulty level) of the AI
	 * @param depth
	 * 	the maximum search depth for move calculations
	 */
	public Ai(String name, int sprite, int elo, int depth) {
		super(null);
		setName(name);
		setElo(elo);
		setDepth(depth);

		// Retrieve the sprite scale factor from the configuration
		setSheetScale(ConfigFetcher.fetchIntegerConfig("Images", "BOTS_SPRITE_SIZE"));
		setSpriteNumber(sprite);

		try {
			// Load the sprite sheet for the AI's images using the configured resource path
			setSheet(ImageIO.read(Objects.requireNonNull(
				getClass().getResourceAsStream(ConfigFetcher.fetchStringConfig("Images", "BOT_SPRITE"))
			)));
		} catch (IOException e) {
			throw new RuntimeException(e);
		}

		// Extract the specific sprite image using the sprite number and scale factor,
		// and scale it smoothly to the desired dimensions
		setSprite(getSheet().getSubimage((sprite - 1) * sheetScale, 0, sheetScale, sheetScale)
			          .getScaledInstance(sheetScale, sheetScale, BufferedImage.SCALE_SMOOTH));
	}

	/**
	 * Initiates the move-making process for the AI.
	 * <p>
	 * If a promotion is pending on the board, the move is skipped.
	 * Otherwise, the AI calculates its next strategic move and executes it.
	 */
	public void makeMove() {
		// Skip move if a pawn promotion is currently being processed
		if (getBoard().isPromotion()) {
			return;
		}

		// Execute the move returned by the AI's strategic move finder
		getBoard().makeMove(findStrategicMove());
	}

	/**
	 * Determines the next strategic move for the AI.
	 * <p>
	 * Subclasses must implement this method to define their specific move strategy.
	 *
	 * @return the move selected by the AI based on its strategy
	 */
	protected abstract Move findStrategicMove();

}
