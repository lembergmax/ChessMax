/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import com.mlprograms.chess.game.action.MouseInput;
import com.mlprograms.chess.game.action.Move;
import com.mlprograms.chess.game.pieces.*;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import static com.mlprograms.chess.utils.ConfigFetcher.*;

/**
 * Represents the game board for ChessMax.
 * Responsible for initializing board-specific configurations
 * and preparing the board for gameplay. It also handles the inputs from the user and updates the game state.
 */
@Getter
@Setter
public class Board extends JPanel {

	private final String FEN_STARTING_POSITION = fetchStringConfig("ChessGame", "STARTING_POSITION");
	private String title;
	private String startingPosition;

	private BoardPainter boardPainter;
	private JPanel boardContainer;

	private List<Piece> pieceList = new ArrayList<>();
	private Piece selectedPiece;

	private List<Move> possibleMoves = new ArrayList<>();

	private int width;
	private int height;
	private int padding;
	private int blinkCount;
	private int delay;
	private int tileSize;
	private int columns;
	private int rows;
	private int halfMoveClock;
	private int fullMoveNumber;
	private int tempFullMoveNumber;
	private int targetColumn;
	private int targetRow;
	private int oldColumn;
	private int oldRow;
	private int newColumn;
	private int newRow;
	private int enPassantTile = -1;

	private boolean isWhiteTurn = true;
	private boolean mouseIsDragged = false;
	private boolean hasCastled = false;

	/**
	 * Constructs the Board and initializes its components and configurations.
	 * Sets up the JFrame and prepares the chessboard layout.
	 */
	public Board() {
		this.boardPainter = new BoardPainter(this);
		this.boardContainer = new JPanel(new GridBagLayout());

		MouseInput mouseInput = new MouseInput(this);
		this.addMouseListener(mouseInput);
		this.addMouseMotionListener(mouseInput);

		boardContainer.setBackground(fetchColorConfig("BACKGROUND"));
		boardContainer.add(this);

		initializeBoardConfigurations();
		setPreferredSize(new Dimension(columns * tileSize, rows * tileSize));

		loadPositionFromFen(FEN_STARTING_POSITION);
	}

	/**
	 * Overrides the paintComponent method to customize the drawing of the chessboard.
	 *
	 * @param graphics
	 * 	the Graphics object used for painting the component.
	 */
	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		removeAll();

		// Delegate drawing tasks to the BoardPainter
		boardPainter.drawChessBoard((Graphics2D) graphics);
		boardPainter.drawCoordinates((Graphics2D) graphics);

		boardPainter.highlightPossibleMoves((Graphics2D) graphics);

		boardPainter.paintPieces((Graphics2D) graphics);
	}

	/**
	 * Loads board-specific configurations from the configuration file.
	 * Assigns values to various board-related attributes.
	 */
	private void initializeBoardConfigurations() {
		final String CHESS_SECTION = "ChessGame";

		this.title = fetchStringConfig(CHESS_SECTION, "TITLE");
		this.startingPosition = fetchStringConfig(CHESS_SECTION, "STARTING_POSITION");
		this.width = fetchIntegerConfig(CHESS_SECTION, "WIDTH");
		this.height = fetchIntegerConfig(CHESS_SECTION, "HEIGHT");
		this.padding = fetchIntegerConfig(CHESS_SECTION, "PADDING");
		this.blinkCount = fetchIntegerConfig(CHESS_SECTION, "BLINK_COUNT");
		this.delay = fetchIntegerConfig(CHESS_SECTION, "DELAY");
		this.tileSize = fetchIntegerConfig(CHESS_SECTION, "TILE_SIZE");
		this.columns = fetchIntegerConfig(CHESS_SECTION, "COLUMNS");
		this.rows = fetchIntegerConfig(CHESS_SECTION, "ROWS");
		this.halfMoveClock = fetchIntegerConfig(CHESS_SECTION, "HALF_MOVE_CLOCK");
		this.fullMoveNumber = fetchIntegerConfig(CHESS_SECTION, "FULL_MOVE_NUMBER");
		this.tempFullMoveNumber = fetchIntegerConfig(CHESS_SECTION, "TEMP_FULL_MOVE_NUMBER");
		this.targetColumn = fetchIntegerConfig(CHESS_SECTION, "TARGET_COLUMN");
		this.targetRow = fetchIntegerConfig(CHESS_SECTION, "TARGET_ROW");
		this.oldColumn = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_FROM_COLUMN");
		this.oldRow = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_FROM_ROW");
		this.newColumn = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_TO_COLUMN");
		this.newRow = fetchIntegerConfig(CHESS_SECTION, "LAST_MOVE_TO_ROW");
	}

	/**
	 * Loads a chess position from a given FEN string and updates the board state accordingly.
	 * The method parses the FEN string to set up the pieces, determine the turn,
	 * castling rights, and en passant square. It also highlights the last move if possible.
	 *
	 * @param fenString
	 * 	the FEN string representing the chess position
	 */
	public void loadPositionFromFen(String fenString) {
		// Clear the current list of pieces to prepare for loading a new position
		pieceList.clear();

		// Split the FEN string into its components
		StringTokenizer tokenizer = new StringTokenizer(fenString, " ");
		String position = tokenizer.nextToken(); // Piece placement section
		isWhiteTurn = tokenizer.nextToken().equals("w"); // Whose turn it is
		String castlingRights = tokenizer.nextToken(); // Castling availability
		String enPassant = tokenizer.nextToken(); // En passant target square

		// Initialize row and column for placing pieces
		int row = 0;
		int column = 0;

		// Parse the piece placement section of the FEN string
		for (int i = 0; i < position.length(); i++) {
			char character = position.charAt(i);

			if (character == '/') {
				// Move to the next row when encountering a '/' separator
				row++;
				column = 0;
			} else if (Character.isDigit(character)) {
				// Skip a number of empty squares based on the digit
				column += Character.getNumericValue(character);
			} else {
				// Add a piece to the list
				boolean isWhite = Character.isUpperCase(character); // Determine if the piece is white
				pieceList.add(createPiece(character, column++, row, isWhite)); // Create and add the piece
			}
		}

		// Update castling rights based on the FEN string
		updateCastling(castlingRights);

		// Update the en passant target square based on the FEN string
		updateEnPassant(enPassant);

		// Redraw the board to reflect the new position
		repaint();
	}

	/**
	 * Executes a chess move, updating the game state accordingly.
	 *
	 * @param move
	 * 	the Move object containing details of the move to be executed.
	 */
	public void makeMove(Move move) {
		// Get the piece being moved
		Piece piece = move.getPiece();

		// Update the last move's start and end positions
		oldColumn = move.getOldColumn();
		oldRow = move.getOldRow();
		newColumn = move.getNewColumn();
		newRow = move.getNewRow();

		// Increment move counts and other related updates
		incrementMoveCounts(move, piece);

		// Handle pawn-specific logic
		if (piece instanceof Pawn) {
			movePawn(move);
		} else {
			enPassantTile = -1; // Reset en passant tile for non-pawn moves
		}

		// Handle king-specific logic, such as castling
		if (piece instanceof King) {
			moveKing(move);
		}

		// Animate the move or update the piece position directly if dragging
		if (!mouseIsDragged) {
			animateMove(piece, move.getNewColumn(), move.getNewRow());
		} else {
			piece.setColumn(move.getNewColumn());
			piece.setRow(move.getNewRow());
			piece.setXPos(move.getNewColumn() * getTileSize());
			piece.setYPos(move.getNewRow() * getTileSize());
		}

		// Mark the piece as no longer being in its initial state
		piece.setFirstMove(false);

		// Handle capturing of opponent pieces, if any
		capturePiece(move);

		// Toggle the turn to the other player
		isWhiteTurn = !isWhiteTurn;

		// Clear possible moves for the next turn
		getPossibleMoves().clear();

		// Reset target column and row indicators
		targetColumn = -1;
		targetRow = -1;
	}

	/**
	 * Increments the full move number and half-move clock based on the current move.
	 * The full move number is incremented if it is not white's turn. The half-move clock
	 * is reset to 0 when a pawn is moved or a piece is captured, otherwise it is incremented.
	 *
	 * @param move
	 * 	The current move being made, including the captured piece (if any).
	 * @param piece
	 * 	The piece being moved in the current move.
	 */
	private void incrementMoveCounts(Move move, Piece piece) {
		// Increment the full move number if it's not white's turn (indicating black's move)
		if (!isWhiteTurn) {
			fullMoveNumber++; // Increment the full move number after black's move
			tempFullMoveNumber = fullMoveNumber; // Store the current full move number temporarily
		}

		// Reset the half-move clock if a pawn is moved or a piece is captured
		if (piece instanceof Pawn || move.getCapturedPiece() != null) {
			halfMoveClock = 0; // Reset the half-move clock
		} else {
			// Increment the half-move clock if neither a pawn is moved nor a piece is captured
			halfMoveClock++;
		}
	}

	/**
	 * Handles the movement of a pawn, including en passant, en passant preparation,
	 * and pawn promotion.
	 *
	 * @param move
	 * 	The move being made by the pawn, containing the old and new position.
	 */
	private void movePawn(Move move) {
		// Determine the color index based on the pawn's color (1 for white, -1 for black)
		int colorIndex = move.getPiece().isWhite() ? 1 : -1;

		// Check for en passant: If the pawn lands on the en passant target square, capture the piece
		if (getTileNumber(move.getNewColumn(), move.getNewRow()) == enPassantTile) {
			move.setCapturedPiece(getPieceAt(move.getNewColumn(), move.getNewRow() + colorIndex));
		}

		// If the pawn moves two squares forward, set the en passant target square
		if (Math.abs(move.getPiece().getRow() - move.getNewRow()) == 2) {
			enPassantTile = getTileNumber(move.getNewColumn(), move.getNewRow() + colorIndex);
		} else {
			// If the pawn moves only one square, clear the en passant target square
			enPassantTile = -1;
		}

		// Check for pawn promotion: If the pawn reaches the promotion rank, trigger promotion logic
		// TODO: checkPawnPromotion(move);
	}

	/**
	 * Handles the movement of the king, including castling.
	 * If the king moves two squares horizontally (castling), the appropriate rook
	 * is moved with the king.
	 *
	 * @param move
	 * 	The move being made by the king, containing the old and new position.
	 */
	private void moveKing(Move move) {
		// Check if the king is attempting to castle by moving two squares horizontally
		if (Math.abs(move.getPiece().getColumn() - move.getNewColumn()) == 2) {
			Piece rook;
			int targetColumn;

			// Determine which rook to move based on the direction of castling (left or right)
			if (move.getPiece().getColumn() < move.getNewColumn()) {
				// Castling to the right: get the rook from the rightmost column
				rook = getPieceAt(7, move.getPiece().getRow());
				targetColumn = 5;  // The king moves to column 5 during castling
			} else {
				// Castling to the left: get the rook from the leftmost column
				rook = getPieceAt(0, move.getPiece().getRow());
				targetColumn = 3;  // The king moves to column 3 during castling
			}

			// If the mouse is not being dragged, animate the rook's move as well
			if (!mouseIsDragged) {
				hasCastled = true;
				animateMove(rook, targetColumn, move.getPiece().getRow());
			} else {
				// Set the rook's position immediately if the mouse is dragged
				rook.setPosition(targetColumn, move.getPiece().getRow());
			}

			// Update the rook's column and position
			rook.setColumn(targetColumn);
			rook.setXPos(rook.getColumn() * getTileSize());
		}
	}

	/**
	 * Animates the movement of a chess piece from its current position to a target position on the board.
	 * The movement is divided into several steps to create a smooth animation effect.
	 *
	 * @param piece
	 * 	The piece being moved.
	 * @param targetColumn
	 * 	The target column for the piece to move to.
	 * @param targetRow
	 * 	The target row for the piece to move to.
	 */
	public void animateMove(Piece piece, int targetColumn, int targetRow) {
		// Get the starting position of the piece
		int startX = piece.getXPos();
		int startY = piece.getYPos();

		// Calculate the target position based on the column and row
		int endX = targetColumn * getTileSize();
		int endY = targetRow * getTileSize();

		// Create a timer to update the piece's position gradually
		Timer timer = new Timer(10, new ActionListener() {
			final int steps = 7; // Number of steps for the animation (controls the speed and smoothness)
			int step = 0; // Keeps track of the current step in the animation

			@Override
			public void actionPerformed(ActionEvent e) {
				step++; // Increment the step counter

				// Calculate the progress of the animation as a value between 0 and 1
				double progress = (double) step / steps;

				// Update the piece's position based on the progress of the animation
				piece.setXPos((int) (startX + (endX - startX) * progress));
				piece.setYPos((int) (startY + (endY - startY) * progress));

				// Repaint the board to reflect the updated position of the piece
				repaint();

				// Stop the animation once all steps are completed and set the final position
				if (step >= steps) {
					((Timer) e.getSource()).stop(); // Stop the timer once the animation is complete
					piece.setPosition(targetColumn, targetRow); // Set the final position of the piece
				}
			}
		});

		// Start the timer to begin the animation
		timer.start();
	}

	/**
	 * Captures a piece on the board during a move, updating the list of pieces,
	 * and optionally adding the captured piece to the scoreboard.
	 *
	 * @param move
	 * 	The move that is being made, which may involve capturing a piece.
	 */
	public void capturePiece(Move move) {
		// Remove the captured piece from the list of pieces
		pieceList.remove(move.getCapturedPiece());

		// If the captured piece is not null and the scoreboard is available, add it to the scoreboard
		if (move.getCapturedPiece() != null) {
			// TODO: Uncomment and implement scoreboard functionality to track captured pieces
			// scoreboard.addCapturedPiece(move.capturedPiece);
		}

		// If there is a piece at the destination square, update the captured piece in the move
		if (getPieceAt(move.getNewColumn(), move.getNewRow()) != null) {
			move.setCapturedPiece(getPieceAt(move.getNewColumn(), move.getNewRow())); // Update the captured piece
		}
	}


	/**
	 * Creates a chess piece based on the given character, position, and color.
	 * The character is matched to its corresponding piece type (e.g., 'r' for Rook).
	 *
	 * @param character
	 * 	the character representing the type of the chess piece (e.g., 'r' for rook)
	 * @param column
	 * 	the column where the piece is located
	 * @param row
	 * 	the row where the piece is located
	 * @param isWhite
	 * 	true if the piece belongs to the white player, false if it belongs to the black player
	 *
	 * @return a new instance of the corresponding chess piece
	 */
	private Piece createPiece(char character, int column, int row, boolean isWhite) {
		// Use a switch expression to map the character to the correct piece type.
		return switch (Character.toLowerCase(character)) {
			case 'r' -> new Rook(this, column, row, isWhite); // Create a Rook piece
			case 'n' -> new Knight(this, column, row, isWhite); // Create a Knight piece
			case 'b' -> new Bishop(this, column, row, isWhite); // Create a Bishop piece
			case 'q' -> new Queen(this, column, row, isWhite); // Create a Queen piece
			case 'k' -> new King(this, column, row, isWhite); // Create a King piece
			default -> new Pawn(this, column, row, isWhite); // Create a Pawn piece for any other character
		};
	}

	/**
	 * Updates the castling rights for the rooks based on the provided castling rights string.
	 * The string contains indicators (e.g., "K", "Q", "k", "q") for available castling moves.
	 *
	 * @param castlingRights
	 * 	the FEN castling rights string, e.g., "KQkq" or "-" if no castling is allowed
	 */
	private void updateCastling(String castlingRights) {
		// Check if the white king-side rook is at its initial position and update its first move status
		if (getPieceAt(7, 7) instanceof Rook wkr) {
			wkr.setFirstMove(castlingRights.contains("K")); // "K" indicates white king-side castling rights
		}
		// Check if the white queen-side rook is at its initial position and update its first move status
		if (getPieceAt(0, 7) instanceof Rook wqr) {
			wqr.setFirstMove(castlingRights.contains("Q")); // "Q" indicates white queen-side castling rights
		}
		// Check if the black king-side rook is at its initial position and update its first move status
		if (getPieceAt(7, 0) instanceof Rook bkr) {
			bkr.setFirstMove(castlingRights.contains("k")); // "k" indicates black king-side castling rights
		}
		// Check if the black queen-side rook is at its initial position and update its first move status
		if (getPieceAt(0, 0) instanceof Rook bqr) {
			bqr.setFirstMove(castlingRights.contains("q")); // "q" indicates black queen-side castling rights
		}
	}

	/**
	 * Updates the en passant tile based on the provided en passant string from the FEN notation.
	 * The en passant tile is calculated based on the column and row of the target square.
	 *
	 * @param enPassant
	 * 	the FEN en passant string, e.g., "e3" or "-" if no en passant is available
	 */
	private void updateEnPassant(String enPassant) {
		// If en passant is "-", there is no valid en passant tile
		enPassantTile = enPassant.equals("-")
			                ? -1 // No en passant
			                : (7 - (enPassant.charAt(1) - '1')) * 8 + (enPassant.charAt(0) - 'a');
		// Calculate tile index: (row index * 8 + column index)
	}

	/**
	 * Retrieves the chess piece located at the specified board position.
	 *
	 * @param column
	 * 	the column to check
	 * @param row
	 * 	the row to check
	 *
	 * @return the piece at the specified position, or null if no piece is found
	 */
	public Piece getPieceAt(int column, int row) {
		// Iterate through the list of all pieces on the board
		for (Piece piece : pieceList) {
			// Check if the piece is at the specified column and row
			if (piece.getColumn() == column && piece.getRow() == row) {
				return piece; // Return the piece if found
			}
		}

		// Return null if no piece exists at the given position
		return null;
	}

	/**
	 * Calculates the tile number for the specified column and row.
	 * The tile number is a unique identifier for a board position, calculated linearly.
	 *
	 * @param column
	 * 	the column of the tile
	 * @param row
	 * 	the row of the tile
	 *
	 * @return the tile number as an integer
	 */
	public int getTileNumber(int column, int row) {
		// Multiply the row index by the total number of rows and add the column index
		return row * getRows() + column;
	}

	/**
	 * Sets the target position on the grid.
	 *
	 * @param column
	 * 	The column of the target position.
	 * @param row
	 * 	The row of the target position.
	 */
	public void setTargetPosition(int column, int row) {
		// Assign the given column and row values to the target position
		this.targetColumn = column;
		this.targetRow = row;

		// Trigger a repaint to update the display with the new target position
		repaint();
	}

	/**
	 * Displays possible moves for the given piece by calculating and highlighting them on the board.
	 *
	 * @param piece
	 * 	the piece for which to show possible moves
	 */
	public void showPossibleMoves(Piece piece) {
		// Clear any previously displayed possible moves
		possibleMoves.clear();

		// Calculate all valid moves for the given piece
		List<Move> calculatedMoves = calculatePossibleMoves(piece);

		// Add the calculated moves to the board's possible moves list
		possibleMoves.addAll(calculatedMoves);

		// Repaint the board to visually display the possible moves
		repaint();
	}

	/**
	 * Calculates all valid possible moves for the given piece.
	 *
	 * @param piece
	 * 	The piece for which the possible moves are to be calculated.
	 *
	 * @return A list of valid moves for the given piece.
	 */
	public List<Move> calculatePossibleMoves(Piece piece) {
		// Initialize a list to store valid moves
		List<Move> moves = new ArrayList<>();

		// Iterate through all the rows and columns of the board
		for (int r = 0; r < getRows(); r++) {
			for (int c = 0; c < getColumns(); c++) {
				// Create a move object for the current position
				Move move = new Move(this, piece, c, r);

				// Check if the move is valid
				if (isValidMove(move)) {
					// Add valid moves to the list
					moves.add(move);
				}
			}
		}

		// Return the list of valid moves
		return moves;
	}

	/**
	 * Checks if the given move is valid based on several conditions.
	 *
	 * @param move
	 * 	The move to be validated.
	 *
	 * @return true if the move is valid, false otherwise.
	 */
	public boolean isValidMove(Move move) {
		// Check if the piece's color matches the current player's turn
		if (move.getPiece().isWhite() != isWhiteTurn) {
			return false; // The piece cannot move if it's not the correct player's turn
		}

		// Ensure the move is within the bounds of the board
		if (move.getNewColumn() < 0 || move.getNewColumn() >= getColumns() || move.getNewRow() < 0 || move.getNewRow() >= getRows()) {
			return false; // The move is out of bounds
		}

		// TODO: Check if the game is over (no more moves allowed)
		// if (isGameOver) {
		// 	return false; // No moves are allowed if the game is over
		// }

		// Ensure the move doesn't involve capturing a piece from the same team
		if (sameTeam(move.getPiece(), move.getCapturedPiece())) {
			return false; // Can't capture pieces of the same color
		}

		// Validate the movement according to the piece's rules
		if (!move.getPiece().isValidMovement(move.getNewColumn(), move.getNewRow())) {
			return false; // The move doesn't comply with the piece's movement rules
		}

		// Ensure the move doesn't collide with another piece
		if (move.getPiece().moveCollidesWithPiece(move.getNewColumn(), move.getNewRow())) {
			return false; // The move is blocked by another piece
		}

		return true;

		// Ensure the move doesn't place the king in check
		// TODO: return !checkScanner.wouldMovePutKingInCheck(move); // Check if the move puts the king in check
	}

	/**
	 * Checks if two pieces belong to the same team (same color).
	 *
	 * @param piece
	 * 	The first piece to compare.
	 * @param otherPiece
	 * 	The second piece to compare.
	 *
	 * @return true if both pieces are of the same color, false otherwise.
	 */
	public boolean sameTeam(Piece piece, Piece otherPiece) {
		// Return false if either piece is null (invalid comparison)
		if (piece == null || otherPiece == null) {
			return false; // Cannot compare if either piece is null
		}

		// Return true if both pieces have the same color (white or black)
		return piece.isWhite() == otherPiece.isWhite();
	}

}
