/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils.ui;

import javax.swing.*;

public class InformationMessage {

	public InformationMessage(String windowName, String message) {
		JOptionPane.showMessageDialog(new JFrame(), message, windowName,
			JOptionPane.INFORMATION_MESSAGE);
	}

}
