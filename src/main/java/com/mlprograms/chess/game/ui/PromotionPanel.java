/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import com.mlprograms.chess.game.engine.Move;
import com.mlprograms.chess.game.pieces.*;
import com.mlprograms.chess.utils.ConfigFetcher;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;

public class PromotionPanel extends JDialog {

	private Piece selectedPiece;

	/**
	 * Constructs the promotion dialog.
	 *
	 * @param parent
	 * 	The parent JFrame.
	 */
	public PromotionPanel(JFrame parent, Board board, Move move) {
		super(parent, ConfigFetcher.fetchStringConfig("Promotion", "TITLE"), true);
		setSize(
			ConfigFetcher.fetchIntegerConfig("Promotion", "WIDTH"),
			ConfigFetcher.fetchIntegerConfig("Promotion", "HEIGHT")
		);
		setResizable(false);
		setLocationRelativeTo(parent);
		setLayout(new GridLayout(
			1,
			4,
			ConfigFetcher.fetchIntegerConfig("Promotion", "HGAP"),
			ConfigFetcher.fetchIntegerConfig("Promotion", "VGAP")
		));
		setDefaultCloseOperation(DO_NOTHING_ON_CLOSE); // Prevent closing via X button

		int row = move.getNewRow();
		int column = move.getNewColumn();
		boolean isWhite = move.getPiece().isWhite();

		// Possible pieces for promotion
		Piece[] pieces = {
			new Queen(board, column, row, isWhite),
			new Rook(board, column, row, isWhite),
			new Knight(board, column, row, isWhite),
			new Bishop(board, column, row, isWhite)
		};

		for (Piece piece : pieces) {
			// Load the image
			ImageIcon icon = new ImageIcon(piece.getSprite().getScaledInstance(
				ConfigFetcher.fetchIntegerConfig("Promotion", "IMAGE_ICON_WIDTH"),
				ConfigFetcher.fetchIntegerConfig("Promotion", "IMAGE_ICON_HEIGHT"),
				Image.SCALE_SMOOTH)
			);
			JButton button = new JButton(icon);

			button.addActionListener((ActionEvent _) -> {
				selectedPiece = piece;
				dispose(); // Close the dialog after selecting a piece
			});

			add(button);
		}
	}

	/**
	 * Shows the promotion dialog and waits for the user to select a piece.
	 *
	 * @return The selected piece (e.g., "Queen", "Rook").
	 */
	public Piece showDialog() {
		// Show the dialog and block interaction until the user makes a selection
		while (selectedPiece == null) {
			setVisible(true);
		}
		return selectedPiece;
	}
}
