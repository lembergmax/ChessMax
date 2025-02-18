/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorConfig;

public class MoveTableCellRenderer extends DefaultTableCellRenderer {
	private final Font cellFont = new Font("SansSerif", Font.PLAIN, 13);
	private final Color mainBgColor;

	/**
	 * Constructs a custom table cell renderer.
	 *
	 * @param mainBgColor
	 * 	The main background color for the table.
	 */
	public MoveTableCellRenderer(Color mainBgColor) {
		this.mainBgColor = mainBgColor;
	}

	@Override
	public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
		JLabel cell = (JLabel) super.getTableCellRendererComponent(
			table, value, isSelected, hasFocus, row, column);
		cell.setFont(cellFont);
		cell.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

		// Center the Text in the cell
		cell.setHorizontalAlignment(SwingConstants.CENTER);

		if (!isSelected) {
			Color evenRowColor = fetchColorConfig("Colors", "EVEN_ROW_COLOR");
			Color oddRowColor = fetchColorConfig("Colors", "ODD_ROW_COLOR");
			cell.setBackground(row % 2 == 0 ? evenRowColor : oddRowColor);
		} else {
			cell.setBackground(table.getSelectionBackground());
		}
		return cell;
	}
}
