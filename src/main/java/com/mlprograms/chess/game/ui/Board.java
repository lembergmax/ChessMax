/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import com.mlprograms.chess.game.utils.SoundPlayer;
import com.mlprograms.chess.game.utils.Sounds;
import com.mlprograms.chess.player.Player;
import com.mlprograms.chess.utils.ConfigReader;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents the game board for ChessMax.
 * Responsible for initializing board-specific configurations
 * and preparing the board for gameplay.
 */
public class Board extends JPanel {

	private final ConfigReader configReader;

	private String title;
	private String startingPosition;

	private int width;
	private int height;
	private int padding;
	private int blinkCount;
	private int delay;
	private int tileSize;
	private int columns;
	private int rows;
	private int halfMoveClock;
	private int fullMoveNumber;
	private int tempFullMoveNumber;
	private int targetColumn;
	private int targetRow;
	private int lastMoveFromColumn;
	private int lastMoveFromRow;
	private int lastMoveToColumn;
	private int lastMoveToRow;

	private Player player1;
	private Player player2;

	/**
	 * Constructs the Board and initializes configuration values.
	 */
	public Board() {
		this.configReader = new ConfigReader();
		initializeBoardConfigurations();

		this.player1 = player1;
		this.player2 = player2;

		player1.setWhite(true);
		player2.setWhite(false);

		this.setPreferredSize(new Dimension(columns * tileSize, rows * tileSize));
		this.setLayout(null);
	}

	/**
	 * Initializes board configurations by loading values from the configuration file.
	 * Configurations are fetched from the "ChessGame" section of the configuration file.
	 */
	private void initializeBoardConfigurations() {
		this.title = fetchStringConfig("TITLE");
		this.startingPosition = fetchStringConfig("STARTING_POSITION");

		this.width = fetchIntegerConfig("WIDTH");
		this.height = fetchIntegerConfig("HEIGHT");
		this.padding = fetchIntegerConfig("PADDING");
		this.blinkCount = fetchIntegerConfig("BLINK_COUNT");
		this.delay = fetchIntegerConfig("DELAY");
		this.tileSize = fetchIntegerConfig("TILE_SIZE");
		this.columns = fetchIntegerConfig("COLUMNS");
		this.rows = fetchIntegerConfig("ROWS");
		this.halfMoveClock = fetchIntegerConfig("HALF_MOVE_CLOCK");
		this.fullMoveNumber = fetchIntegerConfig("FULL_MOVE_NUMBER");
		this.tempFullMoveNumber = fetchIntegerConfig("TEMP_FULL_MOVE_NUMBER");
		this.targetColumn = fetchIntegerConfig("TARGET_COLUMN");
		this.targetRow = fetchIntegerConfig("TARGET_ROW");
		this.lastMoveFromColumn = fetchIntegerConfig("LAST_MOVE_FROM_COLUMN");
		this.lastMoveFromRow = fetchIntegerConfig("LAST_MOVE_FROM_ROW");
		this.lastMoveToColumn = fetchIntegerConfig("LAST_MOVE_TO_COLUMN");
		this.lastMoveToRow = fetchIntegerConfig("LAST_MOVE_TO_ROW");
	}

	/**
	 * Fetches a string configuration value from the ChessGame section.
	 *
	 * @param key
	 * 	The key of the configuration value.
	 *
	 * @return The string value associated with the key.
	 */
	private String fetchStringConfig(String key) {
		return configReader.getValue("ChessGame", key);
	}

	/**
	 * Fetches an integer configuration value from the ChessGame section.
	 *
	 * @param key
	 * 	The key of the configuration value.
	 *
	 * @return The integer value associated with the key.
	 *
	 * @throws NumberFormatException
	 * 	if the configuration value is not a valid integer.
	 */
	private int fetchIntegerConfig(String key) {
		return Integer.parseInt(fetchStringConfig(key));
	}
}
