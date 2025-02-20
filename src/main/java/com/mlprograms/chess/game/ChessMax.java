/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game;

import com.mlprograms.chess.game.engine.HistoryMove;
import com.mlprograms.chess.game.engine.ai.BotSpriteSheetCreator;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.game.ui.MoveTableCellRenderer;
import com.mlprograms.chess.game.ui.RoundedButton;
import com.mlprograms.chess.utils.Logger;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.plaf.basic.BasicScrollBarUI;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;

import static com.mlprograms.chess.game.pieces.AsciiPieces.*;
import static com.mlprograms.chess.utils.ConfigFetcher.*;

public class ChessMax {

	private static final int BUTTON_CORNER_RADIUS = 15;
	private static final int BUTTON_PADDING = 20;
	private static DefaultTableModel moveHistoryTableModel;
	private static Board board;
	private static JScrollPane historyScrollPane;
	private final boolean IS_WHITE_AT_BOTTOM;
	private final Player PLAYER_WHITE;
	private final Player PLAYER_BLACK;
	private JFrame frame;

	/**
	 * Creates a new ChessMax game instance.
	 *
	 * @param playerWhite
	 * 	the white player.
	 * @param playerBlack
	 * 	the black player.
	 * @param isWhiteAtBottom
	 * 	determines if the white pieces are displayed at the bottom.
	 */
	public ChessMax(Player playerWhite, Player playerBlack, boolean isWhiteAtBottom) {
		this.PLAYER_WHITE = playerWhite;
		this.PLAYER_BLACK = playerBlack;
		this.IS_WHITE_AT_BOTTOM = isWhiteAtBottom;

		// Generate the sprite sheet for the bot (AI).
		BotSpriteSheetCreator.createSpriteSheet();

		// Initialize the main game window.
		initializeMainFrame();
	}

	/**
	 * Adds a move to the move history table.
	 *
	 * @param historyMove
	 * 	the move to be added.
	 */
	public static void addMove(HistoryMove historyMove) {
		if (moveHistoryTableModel == null || historyMove == null) {
			return;
		}

		board.getMoveHistory().add(historyMove);
		board.setHistoryLookup(false);
		board.setHistoryLookupIndex(board.getMoveHistory().size());

		String updatedNotation = updateAlgebraicNotationSymbols(historyMove);
		int rowCount = moveHistoryTableModel.getRowCount();

		// If the last row already contains a black move, start a new row for the white move.
		if (rowCount == 0 || (moveHistoryTableModel.getValueAt(rowCount - 1, 2) != null
			                      && !moveHistoryTableModel.getValueAt(rowCount - 1, 2).toString().isEmpty())) {
			Object[] row = new Object[] { rowCount + 1, updatedNotation, "" };
			moveHistoryTableModel.addRow(row);
		} else {
			// Otherwise, update the last row with the black move.
			moveHistoryTableModel.setValueAt(updatedNotation, rowCount - 1, 2);
		}

		// Scroll to the bottom of the move history.
		if (historyScrollPane != null) {
			SwingUtilities.invokeLater(() -> {
				JScrollBar verticalBar = historyScrollPane.getVerticalScrollBar();
				verticalBar.setValue(verticalBar.getMaximum());
			});
		}
	}

	/**
	 * Updates the algebraic notation for a move by replacing piece letters with corresponding ASCII symbols.
	 *
	 * @param historyMove
	 * 	the move whose notation will be updated.
	 *
	 * @return the updated algebraic notation.
	 */
	private static String updateAlgebraicNotationSymbols(HistoryMove historyMove) {
		String notation = historyMove.getMoveAlgebraic();

		if (historyMove.getMove().getPiece().isWhite()) {
			notation = notation.replace("K", WHITE_KING.getSYMBOL())
				           .replace("Q", WHITE_QUEEN.getSYMBOL())
				           .replace("R", WHITE_ROOK.getSYMBOL())
				           .replace("B", WHITE_BISHOP.getSYMBOL())
				           .replace("N", WHITE_KNIGHT.getSYMBOL())
				           .replace("P", WHITE_PAWN.getSYMBOL());
		} else {
			notation = notation.replace("K", BLACK_KING.getSYMBOL())
				           .replace("Q", BLACK_QUEEN.getSYMBOL())
				           .replace("R", BLACK_ROOK.getSYMBOL())
				           .replace("B", BLACK_BISHOP.getSYMBOL())
				           .replace("N", BLACK_KNIGHT.getSYMBOL())
				           .replace("P", BLACK_PAWN.getSYMBOL());
		}
		return notation;
	}

	/**
	 * Marks a single cell in the move history corresponding to the given move index.
	 *
	 * @param index
	 * 	the move index in the move list (0-based).
	 * 	It is assumed that even indices represent white moves (column 1)
	 * 	and odd indices represent black moves (column 2).
	 */
	public static void markHistoryMoveCell(int index) {
		if (moveHistoryTableModel == null || historyScrollPane == null) {
			return;
		}

		// Retrieve the JTable instance from the JScrollPane
		JTable moveTable = (JTable) historyScrollPane.getViewport().getView();
		if (moveTable == null || index <= -1) {
			clearHistoryMoveSelection();
			return;
		}

		// Calculate row and column:
		// Row = index / 2, Column = 1 (white) if index is even, otherwise Column = 2 (black)
		int row = index / 2;
		int col = (index % 2 == 0) ? 1 : 2;

		// Check if the calculated row exists
		if (row >= moveHistoryTableModel.getRowCount()) {
			Logger.logError("Invalid Move-Index: " + index);
			return;
		}

		// Select the cell in the table
		moveTable.changeSelection(row, col, false, false);
	}

	/**
	 * Clears any current selection in the move history.
	 */
	public static void clearHistoryMoveSelection() {
		if (historyScrollPane == null) {
			return;
		}

		// Retrieve the JTable instance from the JScrollPane
		JTable moveTable = (JTable) historyScrollPane.getViewport().getView();
		if (moveTable == null) {
			return;
		}

		// Clear all selections
		moveTable.clearSelection();
	}


	/**
	 * Initializes the main JFrame with the chessboard and side panels.
	 */
	private void initializeMainFrame() {
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

		// Create and add the chessboard.
		board = new Board(PLAYER_WHITE, PLAYER_BLACK, IS_WHITE_AT_BOTTOM);
		GridBagConstraints boardConstraints = new GridBagConstraints();
		boardConstraints.gridx = 0;
		boardConstraints.gridy = 0;
		boardConstraints.weightx = 1.0;
		boardConstraints.weighty = 1.0;
		boardConstraints.anchor = GridBagConstraints.CENTER;
		frame.add(board.getBoardContainer(), boardConstraints);

		// Create the move history panel.
		JPanel historyPanel = createHistoryPanel();
		// Create the button panel.
		JPanel buttonPanel = createButtonPanel();

		// Create a right-side container combining the history panel and buttons.
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(historyPanel, BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);

		GridBagConstraints rightPanelConstraints = new GridBagConstraints();
		rightPanelConstraints.gridx = 1;
		rightPanelConstraints.gridy = 0;
		rightPanelConstraints.weighty = 1.0;
		rightPanelConstraints.fill = GridBagConstraints.VERTICAL;
		rightPanelConstraints.anchor = GridBagConstraints.CENTER;
		frame.add(rightPanel, rightPanelConstraints);

		frame.setVisible(false);
	}

	/**
	 * Creates the move history panel containing a table of moves.
	 *
	 * @return a JPanel representing the move history.
	 */
	private JPanel createHistoryPanel() {
		Color mainBackground = fetchColorConfig("Colors", "BACKGROUND");

		JPanel panel = new JPanel(new BorderLayout());
		panel.setPreferredSize(new Dimension(
			fetchIntegerConfig("History", "WIDTH"),
			fetchIntegerConfig("History", "HEIGHT")
		));
		panel.setBackground(mainBackground);
		panel.setOpaque(true);

		// Retrieve padding configurations.
		int paddingTop = fetchIntegerConfig("History", "PADDING_TOP");
		int paddingLeft = fetchIntegerConfig("History", "PADDING_LEFT");
		int paddingBottom = fetchIntegerConfig("History", "PADDING_BOTTOM");
		int paddingRight = fetchIntegerConfig("History", "PADDING_RIGHT");

		// Create a titled border with specified padding and font.
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
		moveHistoryTableModel = new DefaultTableModel(new Object[] { "Move", "White", "Black" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false; // All cells are non-editable.
			}
		};

		// Create the JTable with the move history table model.
		JTable moveTable = new JTable(moveHistoryTableModel) {
			@Override
			protected void processMouseEvent(MouseEvent e) {
				// Prevent user-initiated selection by overriding mouse events.
			}
		};

		moveTable.setShowGrid(false);
		moveTable.setIntercellSpacing(new Dimension(0, 0));
		moveTable.setRowHeight(30);
		moveTable.setBackground(mainBackground);
		moveTable.setOpaque(true);
		moveTable.setFocusable(false);
		moveTable.setFillsViewportHeight(true);
		moveTable.setCellSelectionEnabled(true);
		moveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		moveTable.setSelectionBackground(fetchColorWithAlphaConfig("Colors", "HISTORY_CELL_HIGHLIGHT", fetchIntegerConfig("Colors", "HISTORY_CELL_HIGHLIGHT_ALPHA")));

		moveTable.getTableHeader().setReorderingAllowed(false);
		moveTable.getTableHeader().setBackground(mainBackground.darker());
		moveTable.getTableHeader().setForeground(fetchColorConfig("Colors", "TABLE_FOREGROUND"));
		moveTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, fetchIntegerConfig("History", "HEADER_FONT_SIZE")));
		moveTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
		moveTable.getTableHeader().setOpaque(true);

		// Set custom renderer for centering text and alternating row colors.
		MoveTableCellRenderer cellRenderer = new MoveTableCellRenderer(mainBackground);
		for (int i = 0; i < moveTable.getColumnCount(); i++) {
			moveTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
		}

		// Adjust column widths based on configuration.
		moveTable.getColumnModel().getColumn(0).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_MOVE_NUMBER_WIDTH"));
		moveTable.getColumnModel().getColumn(1).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_WHITE_WIDTH"));
		moveTable.getColumnModel().getColumn(2).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_BLACK_WIDTH"));

		JScrollPane scrollPane = new JScrollPane(moveTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(mainBackground);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		// Customize the vertical scrollbar.
		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setUI(new CustomScrollBarUI());
		verticalScrollBar.setPreferredSize(new Dimension(10, 0));
		verticalScrollBar.setOpaque(false);

		panel.add(scrollPane, BorderLayout.CENTER);

		// Save reference to scroll pane for auto-scrolling.
		historyScrollPane = scrollPane;
		return panel;
	}

	/**
	 * Creates a panel with navigation buttons.
	 *
	 * @return a JPanel containing the navigation buttons.
	 */
	private JPanel createButtonPanel() {
		Color mainBackground = fetchColorConfig("Colors", "BACKGROUND");

		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		panel.setOpaque(true);
		panel.setBackground(mainBackground);

		// Retrieve padding configurations.
		int paddingTop = fetchIntegerConfig("History", "PADDING_TOP");
		int paddingLeft = fetchIntegerConfig("History", "PADDING_LEFT");
		int paddingBottom = fetchIntegerConfig("History", "PADDING_BOTTOM");
		int paddingRight = fetchIntegerConfig("History", "PADDING_RIGHT");

		TitledBorder titledBorder = BorderFactory.createTitledBorder(
			BorderFactory.createEmptyBorder(paddingTop, paddingLeft, paddingBottom, paddingRight),
			"",
			TitledBorder.CENTER,
			TitledBorder.TOP,
			new Font("Arial", Font.BOLD, 18)
		);
		Border extraMargin = BorderFactory.createEmptyBorder(20, 0, 20, 20);
		panel.setBorder(BorderFactory.createCompoundBorder(extraMargin, titledBorder));

		// Icon dimensions from configuration.
		int iconWidth = fetchIntegerConfig("History", "ARROWS_ICON_WIDTH");
		int iconHeight = fetchIntegerConfig("History", "ARROWS_ICON_HEIGHT");

		// Create navigation buttons.
		JButton buttonToStart = createStyledButton(
			"/images/buttons/To_Start.svg", iconWidth, iconHeight, board::toHistoryStart);
		JButton buttonBackward = createStyledButton(
			"/images/buttons/Backward.svg", iconWidth, iconHeight, board::historyBackward);
		JButton buttonForward = createStyledButton(
			"/images/buttons/Forward.svg", iconWidth, iconHeight, board::historyForward);
		JButton buttonToEnd = createStyledButton(
			"/images/buttons/To_End.svg", iconWidth, iconHeight, board::toHistoryEnd);

		panel.add(buttonToStart);
		panel.add(buttonBackward);
		panel.add(buttonForward);
		panel.add(buttonToEnd);

		return panel;
	}

	/**
	 * Creates a styled button with custom icon and hover/pressed effects.
	 *
	 * @param iconPath
	 * 	the resource path to the button icon.
	 * @param iconWidth
	 * 	the desired icon width.
	 * @param iconHeight
	 * 	the desired icon height.
	 * @param runnable
	 * 	the action to be executed on button click.
	 *
	 * @return the styled JButton.
	 */
	private JButton createStyledButton(String iconPath, int iconWidth, int iconHeight, Runnable runnable) {
		Color normalColor = fetchColorConfig("Colors", "BUTTON_NORMAL");
		Color hoverColor = fetchColorConfig("Colors", "BUTTON_HOVER");
		Color pressedColor = fetchColorConfig("Colors", "BUTTON_PRESSED");

		// Create a rounded button.
		RoundedButton button = new RoundedButton(BUTTON_CORNER_RADIUS);
		button.setIcon(getScaledIcon(iconPath, iconWidth, iconHeight));
		button.setBackground(normalColor);
		button.setPreferredSize(new Dimension(iconWidth + BUTTON_PADDING, iconHeight + BUTTON_PADDING));

		// Log debug text on button click.
		button.addActionListener(_ -> {
			runnable.run();
		});

		// Mouse listener for hover and pressed effects.
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(java.awt.event.MouseEvent evt) {
				button.setBackground(hoverColor);
			}

			@Override
			public void mouseExited(java.awt.event.MouseEvent evt) {
				button.setBackground(normalColor);
			}

			@Override
			public void mousePressed(java.awt.event.MouseEvent evt) {
				button.setBackground(pressedColor);
			}

			@Override
			public void mouseReleased(java.awt.event.MouseEvent evt) {
				if (button.contains(evt.getPoint())) {
					button.setBackground(hoverColor);
				} else {
					button.setBackground(normalColor);
				}
			}
		});

		return button;
	}

	/**
	 * Scales an SVG icon from the resource path to the specified width and height.
	 * Requires Apache Batik to be available in the classpath.
	 *
	 * @param resourcePath
	 * 	the path to the image resource.
	 * @param width
	 * 	the target width.
	 * @param height
	 * 	the target height.
	 *
	 * @return the scaled ImageIcon, or null if an error occurs.
	 */
	private ImageIcon getScaledIcon(String resourcePath, int width, int height) {
		URL url = getClass().getResource(resourcePath);
		if (url == null) {
			Logger.logError("Resource not found: " + resourcePath);
			return null;
		}

		// Only process SVG files.
		if (!resourcePath.toLowerCase().endsWith(".svg")) {
			return null;
		}

		try {
			TranscoderInput transcoderInput = new TranscoderInput(url.toString());
			ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
			TranscoderOutput transcoderOutput = new TranscoderOutput(byteArrayOutputStream);

			PNGTranscoder pngTranscoder = new PNGTranscoder();
			pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) width);
			pngTranscoder.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) height);

			pngTranscoder.transcode(transcoderInput, transcoderOutput);
			byteArrayOutputStream.flush();

			ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
			BufferedImage bufferedImage = ImageIO.read(byteArrayInputStream);

			return new ImageIcon(bufferedImage);
		} catch (Exception e) {
			Logger.logError(e.getMessage());
			return null;
		}
	}

	/**
	 * Displays the game window.
	 */
	public void play() {
		frame.setVisible(true);
	}

	/**
	 * Custom ScrollBar UI that draws a rounded thumb without default arrow buttons.
	 */
	static class CustomScrollBarUI extends BasicScrollBarUI {
		// Arc size for the scrollbar thumb from configuration.
		private final int THUMB_ARC = fetchIntegerConfig("Colors", "HISTORY_SCROLLBAR_THUMB_ARC");

		@Override
		protected JButton createDecreaseButton(int orientation) {
			return createZeroSizeButton();
		}

		@Override
		protected JButton createIncreaseButton(int orientation) {
			return createZeroSizeButton();
		}

		/**
		 * Creates an invisible button to remove the default arrow buttons.
		 *
		 * @return a zero-sized JButton.
		 */
		private JButton createZeroSizeButton() {
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

			// Get thumb color with configured transparency.
			int alpha = fetchIntegerConfig("Colors", "HISTORY_SCROLLBAR_THUMB_ALPHA");
			Color thumbColor = fetchColorWithAlphaConfig("Colors", "HISTORY_SCROLLBAR_THUMB", alpha);

			g2.setPaint(thumbColor);
			g2.fillRoundRect(thumbBounds.x, thumbBounds.y, thumbBounds.width, thumbBounds.height, THUMB_ARC, THUMB_ARC);
			g2.dispose();
		}

		@Override
		protected void paintTrack(Graphics g, JComponent c, Rectangle trackBounds) {
			Graphics2D g2 = (Graphics2D) g.create();
			g2.setPaint(c.getBackground());
			g2.fillRect(trackBounds.x, trackBounds.y, trackBounds.width, trackBounds.height);
			g2.dispose();
		}

		@Override
		protected Dimension getMinimumThumbSize() {
			return new Dimension(10, 30);
		}
	}
}
