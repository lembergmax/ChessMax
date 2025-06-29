/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess;

import com.mlprograms.chess.account.ui.Startmenu;
import com.mlprograms.chess.game.ChessMax;
import com.mlprograms.chess.human.Human;

/**
 * The main entry point for the ChessMax application.
 * Initializes and displays the user interface.
 */
public class Main {

	/**
	 * Main method to start the application.
	 *
	 * @param args
	 * 	command-line arguments (not used)
	 */
	public static void main(String[] args) {
		//initializeUI();
		new ChessMax(new Human(), new Human(), true).play();
	}

	/**
	 * Loads and displays the start menu of the application.
	 */
	private static void initializeUI() {
		Startmenu startmenu = new Startmenu();
		startmenu.setVisible(true);
	}

}
