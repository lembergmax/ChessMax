/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game;

import com.mlprograms.chess.game.engine.ai.BotSpriteSheetCreator;
import com.mlprograms.chess.game.ui.Board;

import javax.swing.*;
import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.*;

public class ChessMax {

	private JFrame frame;

	public ChessMax() {
		setUp();
		initializeJFrame();
	}

	private void setUp() {
		BotSpriteSheetCreator.createSpriteSheet();
	}

	/**
	 * Initializes the JFrame with essential properties such as title, size, and layout.
	 */
	private void initializeJFrame() {
		frame = new JFrame(fetchStringConfig("ChessGame", "TITLE"));

		frame.setLayout(new GridBagLayout());
		frame.setMinimumSize(new Dimension(
			fetchIntegerConfig("ChessGame", "WIDTH"),
			fetchIntegerConfig("ChessGame", "HEIGHT")
		));
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(fetchColorConfig("Colors", "BACKGROUND"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Board ist jetzt ein Mitglied von Frame, nicht eine Unterklasse
		Board board = new Board(frame);
		GridBagConstraints gbc = new GridBagConstraints();
		gbc.gridx = 0;
		gbc.gridy = 0;
		gbc.weightx = 1.0;
		gbc.weighty = 1.0;
		gbc.anchor = GridBagConstraints.CENTER;

		frame.add(board.getBoardContainer(), gbc);
		frame.setVisible(false);
	}

	public void play() {
		frame.setVisible(true);
	}

}
