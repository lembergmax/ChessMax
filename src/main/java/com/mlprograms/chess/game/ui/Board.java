/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.fetchIntegerConfig;
import static com.mlprograms.chess.utils.ConfigFetcher.fetchStringConfig;

/**
 * Represents the game board for ChessMax.
 * Responsible for initializing board-specific configurations
 * and preparing the board for gameplay.
 */
@Getter
@Setter
public class Board extends JPanel {

	private BoardPainter boardPainter;
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

	/**
	 * Constructs the Board and initializes its components and configurations.
	 * Sets up the JFrame and prepares the chessboard layout.
	 */
	public Board() {
		this.boardPainter = new BoardPainter(this);
		initializeBoardConfigurations();
	}

	/**
	 * Overrides the paintComponent method to customize the drawing of the chessboard.
	 *
	 * @param graphics
	 * 	the Graphics object used for painting the component.
	 */
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		removeAll();

		// Delegate drawing tasks to the BoardPainter
		boardPainter.drawChessBoard((Graphics2D) graphics);
		boardPainter.drawCoordinates((Graphics2D) graphics);
	}

	/**
	 * Loads board-specific configurations from the configuration file.
	 * Assigns values to various board-related attributes.
	 */
	private void initializeBoardConfigurations() {
		final String CHESS_SECTION = "ChessGame";

		this.title = fetchStringConfig(CHESS_SECTION, "TITLE");
		this.startingPosition = fetchStringConfig(CHESS_SECTION, "STARTING_POSITION");
		this.width = fetchIntegerConfig(CHESS_SECTION, "WIDTH");
		this.height = fetchIntegerConfig(CHESS_SECTION, "HEIGHT");
		this.padding = fetchIntegerConfig(CHESS_SECTION, "PADDING");
		this.blinkCount = fetchIntegerConfig(CHESS_SECTION, "BLINK_COUNT");
		this.delay = fetchIntegerConfig(CHESS_SECTION, "DELAY");
		this.tileSize = fetchIntegerConfig(CHESS_SECTION, "TILE_SIZE");
		this.columns = fetchIntegerConfig(CHESS_SECTION, "COLUMNS");
		this.rows = fetchIntegerConfig(CHESS_SECTION, "ROWS");
		this.halfMoveClock = fetchIntegerConfig(CHESS_SECTION, "HALF_MOVE_CLOCK");
		this.fullMoveNumber = fetchIntegerConfig(CHESS_SECTION, "FULL_MOVE_NUMBER");
		this.tempFullMoveNumber = fetchIntegerConfig(CHESS_SECTION, "TEMP_FULL_MOVE_NUMBER");
		this.targetColumn = fetchIntegerConfig(CHESS_SECTION, "TARGET_COLUMN");
		this.targetRow = fetchIntegerConfig(CHESS_SECTION, "TARGET_ROW");
		this.lastMoveFromColumn = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_FROM_COLUMN");
		this.lastMoveFromRow = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_FROM_ROW");
		this.lastMoveToColumn = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_TO_COLUMN");
		this.lastMoveToRow = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_TO_ROW");
	}

}
