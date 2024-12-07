/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import javax.swing.*;
import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.*;

public class Frame extends JPanel {

	public Frame() {
		initializeJFrame();
	}

	/**
	 * Initializes the JFrame with essential properties such as title, size, and layout.
	 */
	private void initializeJFrame() {
		JFrame frame = new JFrame(fetchStringConfig("ChessGame", "TITLE"));

		frame.setLayout(new BorderLayout());
		frame.setMinimumSize(new Dimension(
			fetchIntegerConfig("ChessGame", "WIDTH"),
			fetchIntegerConfig("ChessGame", "HEIGHT")
		));
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(fetchColorConfig("BACKGROUND"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Board is now a member of Frame, not a subclass
		Board board = new Board();
		frame.add(board, BorderLayout.CENTER);
		frame.setVisible(true);
	}

}
