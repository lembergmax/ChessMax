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
import java.util.Random;

import static com.mlprograms.chess.game.pieces.AsciiPieces.*;
import static com.mlprograms.chess.utils.ConfigFetcher.*;

public class ChessMax {

	private static final int BUTTON_CORNER_RADIUS = fetchIntegerConfig("Buttons", "BUTTON_CORNER_RADIUS");
	private static final int BUTTON_PADDING = fetchIntegerConfig("Buttons", "BUTTON_PADDING");
	private static DefaultTableModel moveHistoryTableModel;
	private static Board board;
	private static JScrollPane historyScrollPane;

	private final boolean isWhiteAtBottom;
	private final Player playerWhite;
	private final Player playerBlack;
	private JFrame frame;

	/**
	 * Constructs a new ChessMax game instance.
	 *
	 * @param playerWhite
	 * 	the white player
	 * @param playerBlack
	 * 	the black player
	 * @param isWhiteAtBottom
	 * 	if true, white pieces are displayed at the bottom
	 */
	public ChessMax(Player playerWhite, Player playerBlack, boolean isWhiteAtBottom) {
		this.playerWhite = playerWhite;
		this.playerBlack = playerBlack;
		this.isWhiteAtBottom = isWhiteAtBottom;

		// Generate bot sprite sheet for AI
		BotSpriteSheetCreator.createSpriteSheet();

		// Initialize the main game window
		initializeMainFrame();
	}

	/**
	 * Adds a move to the move history table.
	 *
	 * @param historyMove
	 * 	the move to add
	 */
	public static void addMove(HistoryMove historyMove) {
		if (moveHistoryTableModel == null || historyMove == null) {
			return;
		}
		// Update board move history and lookup index
		board.getMoveHistory().add(historyMove);
		board.setHistoryLookup(false);
		board.setHistoryLookupIndex(board.getMoveHistory().size());

		// Update move notation with proper piece symbols
		String updatedNotation = updateAlgebraicNotationSymbols(historyMove);
		int rowCount = moveHistoryTableModel.getRowCount();

		// Add a new row or update the last row based on current table state
		if (rowCount == 0 || (moveHistoryTableModel.getValueAt(rowCount - 1, 2) != null
			                      && !moveHistoryTableModel.getValueAt(rowCount - 1, 2).toString().isEmpty())) {
			Object[] row = new Object[] { rowCount + 1, updatedNotation, "" };
			moveHistoryTableModel.addRow(row);
		} else {
			moveHistoryTableModel.setValueAt(updatedNotation, rowCount - 1, 2);
		}

		// Auto-scroll the move history pane to the bottom
		if (historyScrollPane != null) {
			SwingUtilities.invokeLater(() -> {
				JScrollBar verticalBar = historyScrollPane.getVerticalScrollBar();
				verticalBar.setValue(verticalBar.getMaximum());
			});
		}
	}

	/**
	 * Updates the algebraic notation for a move by replacing piece letters with corresponding symbols.
	 *
	 * @param historyMove
	 * 	the move to update
	 *
	 * @return the updated notation string
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
	 * Highlights the move cell in the move history table.
	 *
	 * @param index
	 * 	the 0-based index of the move to mark
	 */
	public static void markHistoryMoveCell(int index) {
		if (moveHistoryTableModel == null || historyScrollPane == null) {
			return;
		}
		JTable moveTable = (JTable) historyScrollPane.getViewport().getView();
		if (moveTable == null || index < 0) {
			clearHistoryMoveSelection();
			return;
		}
		int row = index / 2;
		int col = (index % 2 == 0) ? 1 : 2;
		if (row >= moveHistoryTableModel.getRowCount()) {
			Logger.logError("Invalid move index: " + index);
			return;
		}
		moveTable.changeSelection(row, col, false, false);
	}

	/**
	 * Clears the current selection in the move history table.
	 */
	public static void clearHistoryMoveSelection() {
		if (historyScrollPane == null) {
			return;
		}
		JTable moveTable = (JTable) historyScrollPane.getViewport().getView();
		if (moveTable != null) {
			moveTable.clearSelection();
		}
	}

	/**
	 * Returns a border based on the developer mode setting.
	 * If DEV_MODE is enabled, the devBorder is combined with the normalBorder so that padding is preserved.
	 *
	 * @param devBorder
	 * 	the border to use in developer mode
	 * @param normalBorder
	 * 	the border to use in normal mode
	 *
	 * @return the appropriate border based on the DEV_MODE configuration
	 */
	private Border getDevBorder(Border devBorder, Border normalBorder) {
		if (fetchBooleanConfig("Developer", "DEV_MODE")) {
			// Combines the devBorder as an outer frame with the original normalBorder
			return (normalBorder != null) ? BorderFactory.createCompoundBorder(devBorder, normalBorder) : devBorder;
		}
		return normalBorder;
	}

	/**
	 * Initializes the main game window and lays out all panels.
	 */
	private void initializeMainFrame() {
		frame = new JFrame(fetchStringConfig("Text", "TITLE"));
		frame.setLayout(new GridBagLayout());
		frame.setMinimumSize(new Dimension(
			fetchIntegerConfig("ChessGame", "WIDTH"),
			fetchIntegerConfig("ChessGame", "HEIGHT")
		));
		frame.setLocationRelativeTo(null);
		frame.getContentPane().setBackground(fetchColorConfig("Colors", "BACKGROUND"));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);

		// Create the chessboard and remove any extra padding.
		board = new Board(playerWhite, playerBlack, isWhiteAtBottom);
		JPanel chessBoardPanel = board.getBoardContainer();
		chessBoardPanel.setBorder(getDevBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.RED, 1),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)
			),
			BorderFactory.createEmptyBorder()
		));

		// Column 0: Left Spacer
		JPanel leftSpacer = new JPanel();
		leftSpacer.setOpaque(false);
		int leftSpacerWidth = fetchIntegerConfig("MBPanel", "LEFT_SPACER_WIDTH");
		leftSpacer.setPreferredSize(new Dimension(fetchBooleanConfig("Developer", "DEV_MODE") ? leftSpacerWidth / 2 : leftSpacerWidth,
			chessBoardPanel.getPreferredSize().height));
		leftSpacer.setBorder(getDevBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.BLUE, 1),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)
			),
			BorderFactory.createEmptyBorder()
		));
		GridBagConstraints leftSpacerConstraints = new GridBagConstraints();
		leftSpacerConstraints.gridx = 0;
		leftSpacerConstraints.gridy = 0;
		leftSpacerConstraints.weightx = 0;
		leftSpacerConstraints.fill = GridBagConstraints.VERTICAL;
		frame.add(leftSpacer, leftSpacerConstraints);

		// Column 1: ChessBoard
		GridBagConstraints boardConstraints = new GridBagConstraints();
		boardConstraints.gridx = 1;
		boardConstraints.gridy = 0;
		boardConstraints.weightx = 1.0;
		boardConstraints.weighty = 1.0;
		boardConstraints.anchor = GridBagConstraints.CENTER;
		frame.add(chessBoardPanel, boardConstraints);

		// Column 2: Middle Panel with the image button (narrow and adjacent to the chessboard)
		JPanel middleButtonPanel = createMiddleButtonPanel();
		GridBagConstraints midPanelConstraints = new GridBagConstraints();
		midPanelConstraints.gridx = 2;
		midPanelConstraints.gridy = 0;
		midPanelConstraints.weightx = 0;
		midPanelConstraints.fill = GridBagConstraints.VERTICAL;
		midPanelConstraints.anchor = GridBagConstraints.WEST;
		frame.add(middleButtonPanel, midPanelConstraints);

		// Column 3: Spacer between Middle Panel and Right Panel
		int spacerWidth = fetchIntegerConfig("MBPanel", "RIGHT_SPACER_WIDTH");
		JPanel spacer = new JPanel();
		spacer.setOpaque(false);
		spacer.setPreferredSize(new Dimension(spacerWidth, chessBoardPanel.getPreferredSize().height));
		spacer.setBorder(getDevBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.MAGENTA, 1),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)
			),
			BorderFactory.createEmptyBorder()
		));
		GridBagConstraints spacerConstraints = new GridBagConstraints();
		spacerConstraints.gridx = 3;
		spacerConstraints.gridy = 0;
		spacerConstraints.weightx = 0;
		spacerConstraints.fill = GridBagConstraints.VERTICAL;
		frame.add(spacer, spacerConstraints);

		// Column 4: Right Panel (contains move history and navigation buttons)
		JPanel historyPanel = createHistoryPanel();
		JPanel buttonPanel = createButtonPanel();
		JPanel rightPanel = new JPanel(new BorderLayout());
		rightPanel.add(historyPanel, BorderLayout.CENTER);
		rightPanel.add(buttonPanel, BorderLayout.SOUTH);
		rightPanel.setBorder(getDevBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.GREEN, 1),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)
			),
			BorderFactory.createEmptyBorder()
		));
		GridBagConstraints rightPanelConstraints = new GridBagConstraints();
		rightPanelConstraints.gridx = 4;
		rightPanelConstraints.gridy = 0;
		rightPanelConstraints.weighty = 1.0;
		rightPanelConstraints.fill = GridBagConstraints.VERTICAL;
		rightPanelConstraints.anchor = GridBagConstraints.CENTER;
		frame.add(rightPanel, rightPanelConstraints);

		frame.setVisible(false);

		// Apply development borders if DEV_MODE is enabled.
		if (fetchBooleanConfig("Developer", "DEV_MODE")) {
			applyDevModeBorders(frame.getContentPane());
		}
	}

	/**
	 * Recursively applies a developer mode border to all JComponents in the given container.
	 * Each component receives a random colored border if DEV_MODE is enabled.
	 *
	 * @param container
	 * 	the container whose components will receive the border
	 */
	private void applyDevModeBorders(Container container) {
		for (Component component : container.getComponents()) {
			if (component instanceof JComponent jComponent) {
				// Preserve the original border in case it needs to be combined.
				Border originalBorder = jComponent.getBorder();
				// Create a random colored border.
				Border randomBorder = BorderFactory.createLineBorder(getRandomColor(), 1);
				// Set the border based on the DEV_MODE configuration.
				try {
					jComponent.setBorder(getDevBorder(randomBorder, originalBorder));
				} catch (Exception e) {
					// If setBorder is not supported, ignore and continue.
				}
			}
			if (component instanceof Container) {
				applyDevModeBorders((Container) component);
			}
		}
	}

	/**
	 * Generates and returns a random color.
	 *
	 * @return a randomly generated Color instance
	 */
	private Color getRandomColor() {
		Random random = new Random();
		return new Color(random.nextInt(256), random.nextInt(256), random.nextInt(256));
	}

	/**
	 * Creates the narrow middle panel that holds the image button.
	 * The panel's width is configured (e.g., 15px) and the button is positioned accordingly.
	 *
	 * @return the configured middle panel
	 */
	private JPanel createMiddleButtonPanel() {
		JPanel panel = new JPanel(null);
		panel.setOpaque(false);
		int middlePanelWidth = fetchIntegerConfig("MBPanel", "WIDTH"); // e.g., 15px
		int boardHeight = board.getBoardContainer().getPreferredSize().height;
		panel.setPreferredSize(new Dimension(middlePanelWidth, boardHeight));
		panel.setBorder(getDevBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.ORANGE, 1),
				BorderFactory.createEmptyBorder(0, 0, 0, 0)
			),
			BorderFactory.createEmptyBorder()
		));

		int buttonWidth = fetchIntegerConfig("MBPanel", "ROTATE_BUTTON_WIDTH");
		int buttonHeight = fetchIntegerConfig("MBPanel", "ROTATE_BUTTON_HEIGHT");
		int buttonX = fetchIntegerConfig("MBPanel", "ROTATE_BUTTON_X");
		int buttonY = fetchIntegerConfig("MBPanel", "ROTATE_BUTTON_Y");
		// Create a styled button that rotates the board on click
		JButton imageButton = createStyledButton(fetchStringConfig("Images", "ROTATE_BUTTON_ICON"), buttonWidth - buttonWidth / 4, buttonHeight - buttonHeight / 4, buttonWidth, buttonHeight, board::rotate);
		imageButton.setBounds(buttonX, buttonY, buttonWidth, buttonHeight);
		imageButton.setBorder(getDevBorder(
			BorderFactory.createLineBorder(Color.PINK, 1),
			BorderFactory.createEmptyBorder()
		));
		panel.add(imageButton);

		return panel;
	}

	/**
	 * Creates the history panel containing the move table.
	 *
	 * @return the configured move history panel
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
		panel.setBorder(getDevBorder(
			BorderFactory.createCompoundBorder(
				BorderFactory.createLineBorder(Color.CYAN, 1),
				BorderFactory.createEmptyBorder(10, 10, 10, 10)
			),
			BorderFactory.createEmptyBorder()
		));

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
		// Set border directly so that padding is always applied.
		panel.setBorder(BorderFactory.createCompoundBorder(extraMargin, titledBorder));

		moveHistoryTableModel = new DefaultTableModel(new Object[] { "Move", "White", "Black" }, 0) {
			@Override
			public boolean isCellEditable(int row, int column) {
				return false;
			}
		};

		JTable moveTable = new JTable(moveHistoryTableModel) {
			@Override
			protected void processMouseEvent(MouseEvent e) {
				// Prevent user-initiated selection
			}
		};

		moveTable.setShowGrid(false);
		moveTable.setIntercellSpacing(new Dimension(0, 0));
		moveTable.setRowHeight(fetchIntegerConfig("History", "ROW_HEIGHT"));
		moveTable.setBackground(mainBackground);
		moveTable.setOpaque(true);
		moveTable.setFocusable(false);
		moveTable.setFillsViewportHeight(true);
		moveTable.setCellSelectionEnabled(true);
		moveTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		moveTable.setSelectionBackground(fetchColorWithAlphaConfig("Colors", "HISTORY_CELL_HIGHLIGHT",
			fetchIntegerConfig("Colors", "HISTORY_CELL_HIGHLIGHT_ALPHA")));

		moveTable.getTableHeader().setReorderingAllowed(false);
		moveTable.getTableHeader().setBackground(mainBackground.darker());
		moveTable.getTableHeader().setForeground(fetchColorConfig("Colors", "TABLE_FOREGROUND"));
		moveTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, fetchIntegerConfig("History", "HEADER_FONT_SIZE")));
		moveTable.getTableHeader().setBorder(BorderFactory.createEmptyBorder());
		moveTable.getTableHeader().setOpaque(true);

		// Set custom cell renderer for move table
		MoveTableCellRenderer cellRenderer = new MoveTableCellRenderer(mainBackground);
		for (int i = 0; i < moveTable.getColumnCount(); i++) {
			moveTable.getColumnModel().getColumn(i).setCellRenderer(cellRenderer);
		}

		moveTable.getColumnModel().getColumn(0).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_MOVE_NUMBER_WIDTH"));
		moveTable.getColumnModel().getColumn(1).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_WHITE_WIDTH"));
		moveTable.getColumnModel().getColumn(2).setPreferredWidth(fetchIntegerConfig("History", "COLUMN_BLACK_WIDTH"));

		JScrollPane scrollPane = new JScrollPane(moveTable);
		scrollPane.setBorder(BorderFactory.createEmptyBorder());
		scrollPane.getViewport().setBackground(mainBackground);
		scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);

		JScrollBar verticalScrollBar = scrollPane.getVerticalScrollBar();
		verticalScrollBar.setUI(new CustomScrollBarUI());
		verticalScrollBar.setPreferredSize(new Dimension(10, 0));
		verticalScrollBar.setOpaque(false);

		panel.add(scrollPane, BorderLayout.CENTER);
		historyScrollPane = scrollPane;
		return panel;
	}

	/**
	 * Creates the panel containing navigation buttons for move history.
	 *
	 * @return the navigation button panel
	 */
	private JPanel createButtonPanel() {
		Color mainBackground = fetchColorConfig("Colors", "BACKGROUND");
		JPanel panel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
		panel.setOpaque(true);
		panel.setBackground(mainBackground);
		panel.setBorder(BorderFactory.createCompoundBorder(
			BorderFactory.createLineBorder(null, 0),
			BorderFactory.createEmptyBorder(
				fetchIntegerConfig("History", "BUTTON_PANEL_PADDING_TOP"),
				fetchIntegerConfig("History", "BUTTON_PANEL_PADDING_LEFT"),
				fetchIntegerConfig("History", "BUTTON_PANEL_PADDING_BOTTOM"),
				fetchIntegerConfig("History", "BUTTON_PANEL_PADDING_RIGHT")
			)
		));

		int iconWidth = fetchIntegerConfig("History", "ARROWS_ICON_WIDTH");
		int iconHeight = fetchIntegerConfig("History", "ARROWS_ICON_HEIGHT");

		JButton buttonToStart = createStyledButton(fetchStringConfig("Images", "HISTORY_TO_START"), iconWidth, iconHeight, board::toHistoryStart);
		JButton buttonPrevious = createStyledButton(fetchStringConfig("Images", "HISTORY_PREVIOUS"), iconWidth, iconHeight, board::historyPrevious);
		JButton buttonNext = createStyledButton(fetchStringConfig("Images", "HISTORY_NEXT"), iconWidth, iconHeight, board::historyNext);
		JButton buttonToEnd = createStyledButton(fetchStringConfig("Images", "HISTORY_TO_END"), iconWidth, iconHeight, board::toHistoryEnd);

		panel.add(buttonToStart);
		panel.add(buttonPrevious);
		panel.add(buttonNext);
		panel.add(buttonToEnd);
		return panel;
	}

	/**
	 * Creates a styled navigation button with the specified icon and click action.
	 *
	 * @param iconPath
	 * 	the resource path for the icon
	 * @param iconWidth
	 * 	the desired icon width
	 * @param iconHeight
	 * 	the desired icon height
	 * @param runnable
	 * 	the action to execute on button click
	 *
	 * @return the styled JButton
	 */
	private JButton createStyledButton(String iconPath, int iconWidth, int iconHeight, Runnable runnable) {
		Color normalColor = fetchColorConfig("Colors", "BUTTON_NORMAL");
		Color hoverColor = fetchColorConfig("Colors", "BUTTON_HOVER");
		Color pressedColor = fetchColorConfig("Colors", "BUTTON_PRESSED");

		RoundedButton button = new RoundedButton(BUTTON_CORNER_RADIUS);
		button.setIcon(getScaledIcon(iconPath, iconWidth, iconHeight));
		button.setBackground(normalColor);
		button.setPreferredSize(new Dimension(iconWidth + BUTTON_PADDING, iconHeight + BUTTON_PADDING));
		button.addActionListener(_ -> runnable.run());
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				button.setBackground(hoverColor);
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				button.setBackground(normalColor);
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				button.setBackground(pressedColor);
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
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
	 * Creates a styled navigation button with the specified icon, size, and click action.
	 *
	 * @param iconPath
	 * 	the resource path for the icon
	 * @param size
	 * 	the desired size for both width and height of the icon
	 * @param runnable
	 * 	the action to execute on button click
	 *
	 * @return the styled JButton
	 */
	private JButton createStyledButton(String iconPath, int size, Runnable runnable) {
		return createStyledButton(iconPath, size, size, runnable);
	}

	/**
	 * Creates a styled navigation button with the specified icon, icon size, button size, and click action.
	 *
	 * @param iconPath
	 * 	the resource path for the icon
	 * @param iconWidth
	 * 	the desired icon width
	 * @param iconHeight
	 * 	the desired icon height
	 * @param buttonWidth
	 * 	the desired button width
	 * @param buttonHeight
	 * 	the desired button height
	 * @param runnable
	 * 	the action to execute on button click
	 *
	 * @return the styled JButton
	 */
	private JButton createStyledButton(String iconPath, int iconWidth, int iconHeight, int buttonWidth, int buttonHeight, Runnable runnable) {
		Color normalColor = fetchColorConfig("Colors", "BUTTON_NORMAL");
		Color hoverColor = fetchColorConfig("Colors", "BUTTON_HOVER");
		Color pressedColor = fetchColorConfig("Colors", "BUTTON_PRESSED");

		RoundedButton button = new RoundedButton(BUTTON_CORNER_RADIUS);
		button.setIcon(getScaledIcon(iconPath, iconWidth, iconHeight));
		button.setBackground(normalColor);
		button.setPreferredSize(new Dimension(buttonWidth, buttonHeight));
		button.addActionListener(_ -> runnable.run());
		button.addMouseListener(new java.awt.event.MouseAdapter() {
			@Override
			public void mouseEntered(MouseEvent evt) {
				button.setBackground(hoverColor);
			}

			@Override
			public void mouseExited(MouseEvent evt) {
				button.setBackground(normalColor);
			}

			@Override
			public void mousePressed(MouseEvent evt) {
				button.setBackground(pressedColor);
			}

			@Override
			public void mouseReleased(MouseEvent evt) {
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
	 * Scales an SVG icon from the given resource path to the specified dimensions.
	 *
	 * @param resourcePath
	 * 	the path to the SVG resource
	 * @param width
	 * 	the target width
	 * @param height
	 * 	the target height
	 *
	 * @return the scaled ImageIcon, or null if an error occurs
	 */
	private ImageIcon getScaledIcon(String resourcePath, int width, int height) {
		URL url = getClass().getResource(resourcePath);
		if (url == null) {
			Logger.logError("Resource not found: " + resourcePath);
			return null;
		}
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
	 * Custom ScrollBar UI implementation that draws a rounded thumb without default arrow buttons.
	 */
	static class CustomScrollBarUI extends BasicScrollBarUI {
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
		 * Creates a button with zero dimensions.
		 *
		 * @return a zero-size JButton
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
