/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils.ui;

import com.mlprograms.chess.account.ui.Startmenu;

import javax.swing.*;

public class OptionMessage {

	public OptionMessage(String windowName, String message, String[] options, Runnable[] actions) {
		int result = JOptionPane.showOptionDialog(
			null,
			message,
			windowName,
			JOptionPane.YES_NO_OPTION,
			JOptionPane.PLAIN_MESSAGE,
			null,
			options,
			options[0]);

		if (result >= 0 && result < actions.length) {
			actions[result].run();
		} else {
			new Startmenu().setVisible(true);
		}
	}

}
