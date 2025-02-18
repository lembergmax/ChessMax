/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import javax.swing.*;
import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.fetchColorConfig;

/**
 * A JButton extension that renders a button with rounded corners.
 */
public class RoundedButton extends JButton {
	private final int cornerRadius;

	/**
	 * Constructs a RoundedButton with the specified corner radius.
	 *
	 * @param cornerRadius
	 * 	the radius for the button's rounded corners
	 */
	public RoundedButton(int cornerRadius) {
		super();
		this.cornerRadius = cornerRadius;
		// Disable default opaque background and content area to preserve the custom rounded design.
		setOpaque(false);
		setContentAreaFilled(false);
		setFocusPainted(false);
	}

	/**
	 * Paints the button's background as a rounded rectangle.
	 *
	 * @param g
	 * 	the Graphics context used for painting
	 */
	@Override
	protected void paintComponent(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g.create();
		// Enable antialiasing for smooth rendering of curves
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Set the fill color to the button's background color and draw the rounded rectangle
		graphics2D.setColor(getBackground());
		graphics2D.fillRoundRect(0, 0, getWidth(), getHeight(), cornerRadius, cornerRadius);
		graphics2D.dispose();
		super.paintComponent(g);
	}

	/**
	 * Paints the button's border as a rounded rectangle.
	 *
	 * @param g
	 * 	the Graphics context used for painting the border
	 */
	@Override
	protected void paintBorder(Graphics g) {
		Graphics2D graphics2D = (Graphics2D) g.create();
		// Enable anti-aliasing for smooth border rendering
		graphics2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		// Retrieve the border color from configuration and draw the rounded border
		graphics2D.setColor(fetchColorConfig("Colors", "BUTTON_BORDER"));
		graphics2D.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, cornerRadius, cornerRadius);
		graphics2D.dispose();
	}

	/**
	 * Computes the preferred size of the button, ensuring it is large enough to display the rounded corners.
	 *
	 * @return the preferred Dimension of the button
	 */
	@Override
	public Dimension getPreferredSize() {
		Dimension prefSize = super.getPreferredSize();
		int diameter = cornerRadius * 2;
		// Ensure the button's width and height are at least as large as the diameter defined by the corner radius
		prefSize.width = Math.max(prefSize.width, diameter);
		prefSize.height = Math.max(prefSize.height, diameter);
		return prefSize;
	}
}