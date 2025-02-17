/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game;

import com.mlprograms.chess.game.engine.ai.BotSpriteSheetCreator;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.game.ui.MoveTableCellRenderer;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

import static com.mlprograms.chess.utils.ConfigFetcher.*;

public class ChessMax {

	private JFrame frame;
	private Player playerWhite;
	private Player playerBlack;
	private boolean isWhiteAtBottom;

	// Table model for dynamically updating move history
	private DefaultTableModel moveTableModel;

	// Scroll pane for the move history panel (used for scrolling to the bottom when a move is added)
	private JScrollPane historyScrollPane;

	/**
	 * Creates a new ChessMax game instance.
	 *
	 * @param playerWhite    The white player.
	 * @param playerBlack    The black player.
	 * @param isWhiteAtBottom Determines if the white pieces are displayed at the bottom of the board.
	 */
	public ChessMax(Player playerWhite, Player playerBlack, boolean isWhiteAtBottom) {
		this.playerWhite = playerWhite;
		this.playerBlack = playerBlack;
		this.isWhiteAtBottom = isWhiteAtBottom;

		// Generate the bot sprite sheet for AI usage.
		BotSpriteSheetCreator.createSpriteSheet();

		// Initialize the graphical user interface.
		initializeJFrame();
	}

	/**
	 * Initializes the main window (JFrame) with the chessboard and move history panel.
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

		// Add the chessboard component to the main window.
		Board board = new Board(playerWhite, playerBlack, isWhiteAtBottom);
		GridBagConstraints gbcBoard = new GridBagConstraints();
		gbcBoard.gridx = 0;
		gbcBoard.gridy = 0;
		gbcBoard.weightx = 1.0;
		gbcBoard.weighty = 1.0;
		gbcBoard.anchor = GridBagConstraints.CENTER;
		frame.add(board.getBoardContainer(), gbcBoard);

		// Add the move history panel next to the chessboard.
		JPanel historyPanel = createHistoryPanel();
		GridBagConstraints gbcHistory = new GridBagConstraints();
		gbcHistory.gridx = 1;
		gbcHistory.gridy = 0;
		gbcHistory.weighty = 1.0;
		gbcHistory.fill = GridBagConstraints.VERTICAL;
		gbcHistory.anchor = GridBagConstraints.CENTER;
		frame.add(historyPanel, gbcHistory);

		frame.setVisible(false);
	}

	/**
	 * Creates the move history panel containing a table of moves.
	 *
	 * @return A JPanel representing the history panel.
	 */
	private JPanel createHistoryPanel() {
		Color mainBgColor = fetchColorConfig("Colors", "BACKGROUND");

		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(fetchIntegerConfig("History", "WIDTH"), fetchIntegerConfig("History", "HEIGHT")));
		panel.setBackground(mainBgColor);
		panel.setOpaque(true);

		// Retrieve padding configurations for the history panel.
		int paddingTop = fetchIntegerConfig("History", "PADDING_TOP");
		int paddingLeft = fetchIntegerConfig("History", "PADDING_LEFT");
		int paddingBottom = fetchIntegerConfig("History", "PADDING_BOTTOM");
		int paddingRight = fetchIntegerConfig("History", "PADDING_RIGHT");

		// Create a titled border with the specified padding and font settings.
		TitledBorder titledBorder = BorderFactory.createTitledBorder(
			BorderFactory.createEmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight),
			"",
			TitledBorder.CENTER,
			TitledBorder.TOP,
			new Font("Arial", Font.BOLD, 18)
		);
		Border extraMargin = BorderFactory.createEmptyBorder(20, 0, 20, 20);
		panel.setBorder(BorderFactory.createCompoundBorder(extraMargin, titledBorder));

		// Initialize the move history table model with columns: Move, White, Black.
		moveTableModel = new DefaultTableModel(new Object[] { "Zug", "Weiß", "Schwarz" }, 0);
		JTable moveTable = new JTable(moveTableModel);
		moveTable.setShowGrid(false);
		moveTable.setIntercellSpacing(new Dimension(0, 0));
		moveTable.setRowHeight(30);

		moveTable.setBackground(mainBgColor);
		moveTable.setOpaque(true);
		moveTable.setFillsViewportHeight(true);

		moveTable.getTableHeader().setReorderingAllowed(false);
		moveTable.getTableHeader().setBackground(mainBgColor.darker());
		moveTable.getTableHeader().setForeground(fetchColorConfig("Colors", "TABLE_FOREGROUND"));
		moveTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, fetchIntegerConfig("History", "HEADER_FONT_SIZE")));
		moveTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
		moveTable.getTableHeader().setOpaque(true);

		// Custom renderer for centering text in the cells and setting alternating row colors.
		MoveTableCellRenderer cellRenderer = new MoveTableCellRenderer(mainBgColor);
		for (int i = 0; i < moveTable.getColumnCount(); i++) {
			moveTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
		}

		// Adjust column widths based on configuration.
		moveTable.getColumnModel().getColumn(0).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_MOVE_NUMBER_WIDTH"));  // Move number
		moveTable.getColumnModel().getColumn(1).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_WHITE_WIDTH"));       // White move
		moveTable.getColumnModel().getColumn(2).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_BLACK_WIDTH"));       // Black move

		JScrollPane scrollPane = new JScrollPane(moveTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(mainBgColor);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Set a custom scrollbar UI for the vertical scrollbar.
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setUI(new CustomScrollBarUI());
		verticalScrollBar.setPreferredSize(new Dimension(10, 0));
		verticalScrollBar.setOpaque(false);

		panel.add(scrollPane, BorderLayout.CENTER);

		// Save the scroll pane reference for scrolling to the bottom when a move is added.
		historyScrollPane = scrollPane;

		return panel;
	}

	/**
	 * Displays the game window.
	 */
	public void play() {
		frame.setVisible(true);
	}

	/**
	 * Adds a move to the move history table and scrolls the history panel to the bottom.
	 *
	 * @param moveNumber The move number.
	 * @param whiteMove  The move made by the white player.
	 * @param blackMove  The move made by the black player.
	 */
	public void addMove(int moveNumber, String whiteMove, String blackMove) {
		if (moveTableModel == null) {
			return;
		}

		boolean moveUpdated = false;
		// Update an existing row if the move number already exists.
		for (int i = 0; i < moveTableModel.getRowCount(); i++) {
			if (moveTableModel.getValueAt(i, 0).equals(String.valueOf(moveNumber))) {
				if (!whiteMove.isEmpty()) {
					moveTableModel.setValueAt(whiteMove, i, 1);
				}
				if (!blackMove.isEmpty()) {
					moveTableModel.setValueAt(blackMove, i, 2);
				}
				moveUpdated = true;
				break;
			}
		}
		// If the move number does not exist, add a new row.
		if (!moveUpdated) {
			moveTableModel.addRow(new Object[] { String.valueOf(moveNumber), whiteMove, blackMove });
		}

		// Scroll the history panel to the bottom after adding/updating a move.
		if (historyScrollPane != null) {
			SwingUtilities.invokeLater(() -> {
				JScrollBar verticalBar = historyScrollPane.getVerticalScrollBar();
				verticalBar.setValue(verticalBar.getMaximum());
			});
		}
	}

	/**
	 * Custom ScrollBar UI that draws a round thumb without the default arrow buttons.
	 */
	static class CustomScrollBarUI extends BasicScrollBarUI {
		// The arc size for the scrollbar thumb, fetched from configuration.
		private final int THUMB_ARC = fetchIntegerConfig("Colors", "HISTORY_SCROLLBAR_THUMB_ARC");

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return createZeroButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return createZeroButton();
		}

		/**
		 * Creates a button with zero size to remove the default arrow buttons.
		 *
		 * @return A JButton with no size.
		 */
		private JButton createZeroButton() {
			JButton button = new JButton();
			Dimension zeroDim = new Dimension(0, 0);
			button.setPreferredSize(zeroDim);
			button.setMinimumSize(zeroDim);
			button.setMaximumSize(zeroDim);
			return button;
		}

		@Override
		protected void paintThumb(Graphics g, JComponent c, Rectangle thumbBounds) {
			if (!c.isEnabled() || thumbBounds.width > thumbBounds.height) {
				return;
			}
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

			// Fetch the thumb color with the specified transparency.
			int alpha = fetchIntegerConfig("Colors", "HISTORY_SCROLLBAR_THUMB_ALPHA");
			Color thumbColor = fetchColorWithAlphaConfig("Colors", "HISTORY_SCROLLBAR_THUMB", alpha);

			g2.setPaint(thumbColor);
			g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, THUMB_ARC, THUMB_ARC);
			g2.dispose();
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			// Optionally adjust the color of the scrollbar track to match the background.
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(c.getBackground());
			g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			g2.dispose();
		}

		@Override
		protected Dimension getMinimumThumbSize() {
			// Minimum size for the thumb to remain clickable.
			return new Dimension(10, 30);
		}
	}
}
