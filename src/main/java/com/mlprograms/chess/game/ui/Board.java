/*
 * Copyright (c) 2024-2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.game.ui;

import com.mlprograms.chess.game.Player;
import com.mlprograms.chess.game.engine.*;
import com.mlprograms.chess.game.engine.ai.Ai;
import com.mlprograms.chess.game.pieces.*;
import com.mlprograms.chess.game.utils.SoundPlayer;
import com.mlprograms.chess.game.utils.Sounds;
import com.mlprograms.chess.human.Human;
import com.mlprograms.chess.utils.Logger;
import com.mlprograms.chess.utils.ui.InformationMessage;
import lombok.Getter;
import lombok.Setter;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import static com.mlprograms.chess.game.ChessMax.*;
import static com.mlprograms.chess.utils.ConfigFetcher.*;

/**
 * Represents the game board for ChessMax.
 * Responsible for initializing board-specific configurations
 * and preparing the board for gameplay. It also handles the inputs from the user and updates the game state.
 */
@Getter
@Setter
public class Board extends JPanel {

	private SoundPlayer soundPlayer;
	private BoardPainter boardPainter;
	private MoveValidator moveValidator;

	private String title;
	private String startingPosition;
	private String promotedTo = "";

	private JPanel boardContainer;
	private JPanel promotionPanel;

	private GameEnding gameEnding = GameEnding.IN_PROGRESS;
	private List<HistoryMove> moveHistory = new ArrayList<>();
	private List<String> moveSoundHistory = new ArrayList<>();
	private List<Move> possibleMoves = new ArrayList<>();
	private List<Piece> pieceList = new ArrayList<>();
	private Piece selectedPiece;

	private int width;
	private int height;
	private int padding;
	private int blinkCount;
	private int tileSize;
	private int columns;
	private int rows;
	private int halfMoveClock;
	private int fullMoveNumber;
	private int tempFullMoveNumber;
	private int enPassantTile = -1;
	private int historyLookupIndex = 0;

	private boolean historyLookup = false;
	private boolean isWhiteTurn = true;
	private boolean mouseDragged = false;
	private boolean hasCastled = false;
	private boolean isPromotion = false;
	private boolean moveHistoryKingInCheck = false;
	private boolean isWhiteAtBottom;

	private Player playerWhite;
	private Player playerBlack;

	private List<Arrow> arrows = new ArrayList<>();
	private List<Point> redHighlights = new ArrayList<>();
	private Arrow tempArrow;

	private Point hoveredTile;

	/**
	 * Constructs the Board and initializes its components and configurations.
	 * Sets up the JFrame and prepares the chessboard layout.
	 */
	public Board(Player playerWhite, Player playerBlack, boolean isWhiteAtBottom) {
		playerWhite.setBoard(this);
		playerBlack.setBoard(this);

		this.playerWhite = playerWhite;
		this.playerBlack = playerBlack;
		this.moveValidator = new MoveValidator(this);
		this.soundPlayer = new SoundPlayer();
		this.boardPainter = new BoardPainter(this);
		this.boardContainer = new JPanel(new GridBagLayout());
		this.isWhiteAtBottom = isWhiteAtBottom;

		MouseInput mouseInput = new MouseInput(this);
		addMouseListener(mouseInput);
		addMouseMotionListener(mouseInput);

		getBoardContainer().setBackground(fetchColorConfig("Colors", "BACKGROUND"));
		getBoardContainer().add(this);

		initializeBoardConfigurations();
		setPreferredSize(new Dimension(getColumns() * getTileSize(), getRows() * getTileSize()));

		SwingUtilities.invokeLater(this::playStartGameSound);
		SwingUtilities.invokeLater(this::checkForAiMove);
	}

	public Board(Player playerWhite, Player playerBlack) {
		this(playerWhite, playerBlack, true);
	}

	/**
	 * Only for testing purposes.
	 */
	public Board() {
		this(new Human(), new Human());
	}

	/**
	 * Plays the sound effect for the start of the game.
	 */
	private void playStartGameSound() {
		getSoundPlayer().play(Sounds.GAME_START);
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
		getBoardPainter().drawChessBoard((Graphics2D) graphics);

		if (isHistoryLookup()) {
			if (getHistoryLookupIndex() != -1) {
				getBoardPainter().highlightMadeMove((Graphics2D) graphics, getMoveHistory().get(getHistoryLookupIndex()).getMove());
			}
		} else {
			getBoardPainter().highlightMadeMove((Graphics2D) graphics);
		}

		getBoardPainter().highlightPossibleMoves((Graphics2D) graphics);
		getBoardPainter().paintRedHighlights((Graphics2D) graphics);
		getBoardPainter().drawCoordinates((Graphics2D) graphics);
		getBoardPainter().drawTileHoverBorder((Graphics2D) graphics);
		getBoardPainter().paintPieces((Graphics2D) graphics);
		getBoardPainter().drawArrows((Graphics2D) graphics);
	}

	/**
	 * Loads board-specific configurations from the configuration file.
	 * Assigns values to various board-related attributes.
	 */
	private void initializeBoardConfigurations() {
		final String CHESS_SECTION = "ChessGame";

		this.title = fetchStringConfig(CHESS_SECTION, "TITLE");
		this.startingPosition = fetchStringConfig(CHESS_SECTION, "STARTING_POSITION_" + (isWhiteAtBottom() ? "WHITE" : "BLACK"));
		this.width = fetchIntegerConfig(CHESS_SECTION, "WIDTH");
		this.height = fetchIntegerConfig(CHESS_SECTION, "HEIGHT");
		this.padding = fetchIntegerConfig(CHESS_SECTION, "PADDING");
		this.blinkCount = fetchIntegerConfig(CHESS_SECTION, "BLINK_COUNT");
		this.tileSize = fetchIntegerConfig(CHESS_SECTION, "TILE_SIZE");
		this.columns = fetchIntegerConfig(CHESS_SECTION, "COLUMNS");
		this.rows = fetchIntegerConfig(CHESS_SECTION, "ROWS");
		this.halfMoveClock = fetchIntegerConfig(CHESS_SECTION, "HALF_MOVE_CLOCK");
		this.fullMoveNumber = fetchIntegerConfig(CHESS_SECTION, "FULL_MOVE_NUMBER");
		this.tempFullMoveNumber = fetchIntegerConfig(CHESS_SECTION, "TEMP_FULL_MOVE_NUMBER");
		this.enPassantTile = fetchIntegerConfig(CHESS_SECTION, "EN_PASSANT_TILE");

		loadPositionFromFen(getStartingPosition());
	}

	/**
	 * Checks if it is the AI's turn to move and makes the move if it is.
	 * If it is the white player's turn and the white player is an AI, the AI makes a move.
	 * If it is the black player's turn and the black player is an AI, the AI makes a move.
	 */
	public void checkForAiMove() {
		if (isWhiteTurn() && playerWhite instanceof Ai) {
			((Ai) playerWhite).makeMove();
		} else if (!isWhiteTurn() && playerBlack instanceof Ai) {
			((Ai) playerBlack).makeMove();
		}
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

		// Retrieve local reference to the piece list to avoid repeated getter calls.
		List<Piece> pieceList = getPieceList();
		pieceList.clear();

		// Manually parse the FEN string instead of using StringTokenizer.
		// FEN Format: [piece placement] [active color] [castling availability] [en passant square] ...
		int firstSpace = fenString.indexOf(' ');
		if (firstSpace == -1) return; // Invalid FEN, abort

		String placement = fenString.substring(0, firstSpace);

		int secondSpace = fenString.indexOf(' ', firstSpace + 1);
		if (secondSpace == -1) return; // Invalid FEN, abort
		// Active color: "w" or "b"
		boolean whiteTurn = fenString.charAt(firstSpace + 1) == 'w';
		setWhiteTurn(whiteTurn);

		int thirdSpace = fenString.indexOf(' ', secondSpace + 1);
		if (thirdSpace == -1) return; // Invalid FEN, abort
		String castlingRights = fenString.substring(secondSpace + 1, thirdSpace);

		int fourthSpace = fenString.indexOf(' ', thirdSpace + 1);
		// If no fourth space exists, en passant goes bis zum Ende.
		String enPassant = (fourthSpace == -1)
			                   ? fenString.substring(thirdSpace + 1)
			                   : fenString.substring(thirdSpace + 1, fourthSpace);

		// Parse the piece placement section using a char array for performance.
		char[] placementChars = placement.toCharArray();
		int row = 0;
		int column = 0;
		for (char ch : placementChars) {
			if (ch == '/') {
				// New row detected; move to the next row and reset column.
				row++;
				column = 0;
			} else if (ch >= '1' && ch <= '8') {
				// Advance the column by the numeric value (number of empty squares).
				column += ch - '0';
			} else {
				// Create and add the piece.
				// Determine the color by checking if the character is uppercase.
				boolean isWhite = Character.isUpperCase(ch);
				pieceList.add(createPiece(ch, column, row, isWhite));
				column++;
			}
		}

		// Update castling rights and the en passant target square.
		updateCastling(castlingRights);
		updateEnPassant(enPassant);

		// Redraw the board to reflect the new position.
		repaint();
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
		return switch (Character.toLowerCase(character)) {
			case 'r' -> new Rook(this, column, row, isWhite);
			case 'n' -> new Knight(this, column, row, isWhite);
			case 'b' -> new Bishop(this, column, row, isWhite);
			case 'q' -> new Queen(this, column, row, isWhite);
			case 'k' -> new King(this, column, row, isWhite);
			default -> new Pawn(this, column, row, isWhite);
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
		// Update white king-side rook.
		if (getPieceAt(7, 7) instanceof Rook wkr) {
			wkr.setFirstMove(castlingRights.contains("K"));
		}
		// Update white queen-side rook.
		if (getPieceAt(0, 7) instanceof Rook wqr) {
			wqr.setFirstMove(castlingRights.contains("Q"));
		}
		// Update black king-side rook.
		if (getPieceAt(7, 0) instanceof Rook bkr) {
			bkr.setFirstMove(castlingRights.contains("k"));
		}
		// Update black queen-side rook.
		if (getPieceAt(0, 0) instanceof Rook bqr) {
			bqr.setFirstMove(castlingRights.contains("q"));
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
		setEnPassantTile(
			enPassant.equals("-")
				? -1
				: (7 - (enPassant.charAt(1) - '1')) * 8 + (enPassant.charAt(0) - 'a')
		);
	}

	/**
	 * Generates the current FEN notation as an object of FenNotation.
	 * The FEN notation includes piece positions, turn, castling rights,
	 * en passant target square, halfmove clock, and fullmove number.
	 *
	 * @return a FenNotation object representing the current board position
	 */
	public FenNotation getCurrentPositionsFenNotation() {
		StringBuilder fen = new StringBuilder();

		// Piece positions
		for (int row = 0; row < getRows(); row++) {
			int emptyCount = 0;
			for (int col = 0; col < getColumns(); col++) {
				Piece piece = getPieceAt(col, row);
				if (piece == null) {
					emptyCount++;
				} else {
					if (emptyCount > 0) {
						fen.append(emptyCount);
						emptyCount = 0;
					}
					String pieceCharacter = switch (piece.getName()) {
						case "Knight" -> "N";
						case "Bishop" -> "B";
						case "Rook" -> "R";
						case "Queen" -> "Q";
						case "King" -> "K";
						default -> "P"; // Pawn
					};

					fen.append(piece.isWhite() ? pieceCharacter.toUpperCase() : pieceCharacter.toLowerCase());
				}
			}
			if (emptyCount > 0) {
				fen.append(emptyCount);
			}
			if (row < getRows() - 1) {
				fen.append('/');
			}
		}

		// Turn
		fen.append(isWhiteTurn() ? " w " : " b ");

		// Castling rights
		StringBuilder castlingRights = new StringBuilder();
		if (getMoveValidator().findKing(true).isFirstMove()) {
			if (getPieceAt(7, 7) instanceof Rook && getPieceAt(7, 7).isFirstMove()) castlingRights.append('K');
			if (getPieceAt(0, 7) instanceof Rook && getPieceAt(0, 7).isFirstMove()) castlingRights.append('Q');
		}

		if (getMoveValidator().findKing(false).isFirstMove()) {
			if (getPieceAt(7, 0) instanceof Rook && getPieceAt(7, 0).isFirstMove()) castlingRights.append('k');
			if (getPieceAt(0, 0) instanceof Rook && getPieceAt(0, 0).isFirstMove()) castlingRights.append('q');
		}

		if (castlingRights.isEmpty()) {
			castlingRights.append('-');
		}

		return getFenNotation(fen, castlingRights);
	}

	/**
	 * Creates a FenNotation object representing the current board state.
	 * The FEN notation includes piece positions, turn, castling rights,
	 * en passant target square, halfmove clock, and fullmove number.
	 *
	 * @param fen
	 * 	the StringBuilder containing the piece positions in FEN format.
	 * @param castlingRights
	 * 	the StringBuilder containing the castling rights in FEN format.
	 *
	 * @return a FenNotation object representing the current board state.
	 */
	private FenNotation getFenNotation(StringBuilder fen, StringBuilder castlingRights) {
		// En passant target square
		String enPassantTarget = getEnPassantTile() == -1 ? "-" :
			                         String.valueOf((char) ('a' + (getEnPassantTile() % 8))) + (8 - (getEnPassantTile() / 8));

		// Create FenNotation object
		FenNotation fenNotation = new FenNotation();
		fenNotation.setFenString(fen.toString());
		fenNotation.setCastlingRights(castlingRights.toString());
		fenNotation.setEnPassantTile(enPassantTarget);
		fenNotation.setHalfMoveClock(getHalfMoveClock());
		fenNotation.setFullMoveNumber(getFullMoveNumber());
		fenNotation.setWhiteToMove(isWhiteTurn());

		return fenNotation;
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

		// Increment move counts and other related updates
		incrementMoveCounts(move, piece);

		// Handle pawn-specific logic
		if (piece instanceof Pawn) {
			movePawn(move);
		} else {
			setEnPassantTile(-1); // Reset en passant tile for non-pawn moves
		}

		// Handle king-specific logic, such as castling
		if (piece instanceof King) {
			moveKing(move);
		}

		// Animate the move or update the piece position directly if dragging
		// if (!isMouseDragged()) {
		// TODO: animateMove(piece, move.getNewColumn(), move.getNewRow());
		// }

		// Set the new position of the piece
		piece.setColumn(move.getNewColumn());
		piece.setRow(move.getNewRow());
		piece.setXPos(move.getNewColumn() * getTileSize());
		piece.setYPos(move.getNewRow() * getTileSize());

		// Mark the piece as no longer being in its initial state
		piece.setFirstMove(false);

		// Handle capturing of opponent pieces, if any
		capturePiece(move);

		// Toggle the turn to the other player
		setWhiteTurn(!isWhiteTurn());


		// Clear possible moves for the next turn
		getPossibleMoves().clear();

		setGameEnding(checkForGameEnding());

		if (!isPromotion()) {
			// Play appropriate sound effects based on the move
			playGameSound(move);

			// Create a new HistoryMove object to store the move details
			HistoryMove historyMove = new HistoryMove(
				getMoveHistory().size() + 1,
				move.toAlgebraicNotation(),
				move,
				getCurrentPositionsFenNotation()
			);

			// Add the move to the move history
			addMove(historyMove);
			checkGameEnd();
		}

		markHistoryMoveCell(getHistoryLookupIndex() - 1);
	}

	/**
	 * Checks if the game has ended and handles the end-of-game logic.
	 * If the game is still in progress, it checks if it is the AI's turn to move.
	 * If the game has ended, it plays the game end sound and displays an information message.
	 */
	private void checkGameEnd() {
		setGameEnding(checkForGameEnding());
		if (getGameEnding() == GameEnding.IN_PROGRESS) {
			if (isWhiteTurn() && playerWhite instanceof Ai || !isWhiteTurn() && playerBlack instanceof Ai) {
				checkForAiMove();
			}
			return;
		}

		getSoundPlayer().play(Sounds.GAME_END);

		// TODO: make better ui for game ending
		boolean isDraw = getGameEnding() != GameEnding.CHECKMATE;
		new InformationMessage("Spielende", "Das Spiel ist beendet! " + (isDraw ? "Unentschieden" : (isWhiteTurn() ? "Schwarz" : "Weiß") + " hat gewonnen!") + "\nGrund: " + gameEnding);
	}

	public void rotateBoard() {
		// TODO: board does not update
		// TODO: there are many more issues after rotation

		System.out.println("rotate board");

		for (Piece piece : getPieceList()) {
			int[] newCoordinates = rotateCoordinates(piece.getColumn(), piece.getRow());
			piece.setColumn(newCoordinates[ 0 ]);
			piece.setRow(newCoordinates[ 1 ]);
		}

		setWhiteAtBottom(!isWhiteAtBottom());
	}

	private int[] rotateCoordinates(int column, int row) {
		return new int[] { getColumns() - 1 - column, getRows() - 1 - row };
	}

	/**
	 * Navigates to the start of the game (i.e. the initial board position).
	 */
	public void toHistoryStart() {
		// If there is no move history, do nothing
		if (getMoveHistory() == null || getMoveHistory().isEmpty()) {
			return;
		}

		// Enable history lookup mode
		setHistoryLookup(true);
		// Set index to -1, representing the starting position
		setHistoryLookupIndex(-1);
		// Clear any cached possible moves
		getPossibleMoves().clear();
		// Load the starting board position using FEN notation
		loadPositionFromFen(getStartingPosition());

		clearHistoryMoveSelection();
		clearHighlightsAndArrows();
	}

	/**
	 * Navigates to the end of the move history (i.e. the final board position).
	 */
	public void toHistoryEnd() {
		// If there is no move history, do nothing
		if (getMoveHistory() == null || getMoveHistory().isEmpty()) {
			return;
		}

		// Since we are at the final move, disable history lookup mode
		setHistoryLookup(false);
		// Set index to the last move (0-based index)
		setHistoryLookupIndex(getMoveHistory().size() - 1);
		// Clear any cached possible moves
		getPossibleMoves().clear();
		// Load the board position from the FEN of the last move in history
		loadPositionFromFen(getMoveHistory().get(getHistoryLookupIndex()).getFenNotation().toString());

		markHistoryMoveCell(getHistoryLookupIndex());
		clearHighlightsAndArrows();

		playHistoryGameSound(getMoveHistory().size());
	}

	/**
	 * Moves one step backward in the move history.
	 * If at the first move, it will revert to the starting position.
	 */
	public void historyBackward() {
		// If there is no move history, do nothing
		if (getMoveHistory() == null || getMoveHistory().isEmpty()) {
			return;
		}

		// If already at the starting position (index -1), no further backward navigation is possible
		if (getHistoryLookupIndex() <= -1) {
			return;
		}

		// Ensure history lookup mode is enabled
		setHistoryLookup(true);

		// If currently at the first move (index 0), moving backward goes to the starting position
		if (getHistoryLookupIndex() == 0) {
			setHistoryLookupIndex(-1);
			getPossibleMoves().clear();
			loadPositionFromFen(getStartingPosition());
		} else {
			// Otherwise, decrement the index and load the corresponding board position
			setHistoryLookupIndex(getHistoryLookupIndex() - 1);
			getPossibleMoves().clear();
			// If the new index is -1, load the starting position; else load the move's FEN
			if (getHistoryLookupIndex() == -1) {
				loadPositionFromFen(getStartingPosition());
			} else {
				if (getHistoryLookupIndex() == getMoveHistory().size() - 1) {
					setHistoryLookupIndex(getHistoryLookupIndex() - 1);
				}

				if (getHistoryLookupIndex() == -1) {
					toHistoryStart();
				} else {
					loadPositionFromFen(getMoveHistory().get(getHistoryLookupIndex()).getFenNotation().toString());
				}

			}
		}

		markHistoryMoveCell(getHistoryLookupIndex());
		clearHighlightsAndArrows();
		playHistoryGameSound(getHistoryLookupIndex() + 1);
	}

	/**
	 * Moves one step forward in the move history.
	 * When reaching the final move, the history lookup mode is disabled.
	 */
	public void historyForward() {
		// If there is no move history, do nothing
		if (getMoveHistory() == null || getMoveHistory().isEmpty() || getHistoryLookupIndex() >= getMoveHistory().size() - 1) {
			return;
		}

		// Increment the history index to move forward
		setHistoryLookupIndex(getHistoryLookupIndex() + 1);
		// Ensure history lookup mode remains enabled
		setHistoryLookup(true);
		// Clear any cached possible moves
		getPossibleMoves().clear();
		// Load the board position corresponding to the new history index
		loadPositionFromFen(getMoveHistory().get(getHistoryLookupIndex()).getFenNotation().toString());

		markHistoryMoveCell(getHistoryLookupIndex());

		// If already at the final move, disable history lookup mode and exit
		if (getHistoryLookupIndex() >= getMoveHistory().size() - 1) {
			setHistoryLookup(false);
		}

		clearHighlightsAndArrows();

		if (getMoveHistory().size() > 1) {
			playHistoryGameSound(getHistoryLookupIndex());
		} else {
			playHistoryGameSound(getHistoryLookupIndex() + 1);
		}
	}

	/**
	 * Clears all red highlights and arrows from the board.
	 * This method removes any visual indicators such as red highlights and arrows,
	 * and resets the temporary arrow to null.
	 */
	private void clearHighlightsAndArrows() {
		getRedHighlights().clear();
		getArrows().clear();
		setTempArrow(null);
	}

	/**
	 * Plays a sound effect based on the type of move being made.
	 * Note that the following sounds are only for basic sound effects like game end, king in check, piece capture, and
	 * piece movement.
	 * Other sounds like castling, game start, and illegal moves are handled elsewhere.
	 *
	 * @param move
	 * 	The move that is being made, including the captured piece (if any).
	 */
	private void playGameSound(Move move) {
		// If the game has ended
		if (getMoveValidator().isCheckmate() || getMoveValidator().isStalemate()) {
			playAndAddSoundToHistory(Sounds.GAME_END);
			return;
		}

		if (getMoveValidator().isKingInCheck()) {
			setMoveHistoryKingInCheck(true);
			playAndAddSoundToHistory(Sounds.CHECK);
			return;
		}

		// King has castled
		if (isHasCastled()) {
			playAndAddSoundToHistory(Sounds.CASTLE);
			setHasCastled(false);
			return;
		}

		// If a Piece was captured
		if (move.getCapturedPiece() != null) {
			playAndAddSoundToHistory(Sounds.CAPTURE);
			return;
		}

		playAndAddSoundToHistory(Sounds.MOVE);
	}

	/**
	 * Plays the specified sound and adds it to the move sound history.
	 *
	 * @param sound
	 * 	The key of the sound to be played.
	 */
	private void playAndAddSoundToHistory(String sound) {
		getMoveSoundHistory().add(sound);
		getSoundPlayer().play(sound);
	}

	/**
	 * Plays the sound associated with the current move in the history.
	 * This method retrieves the sound key from the move sound history
	 * based on the current history lookup index and plays the corresponding sound.
	 */
	private void playHistoryGameSound(int index) {
		if (index == getMoveHistory().size()) {
			return;
		}

		getSoundPlayer().play(getMoveSoundHistory().get(index));
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
		if (!isWhiteTurn()) {
			setFullMoveNumber(getTempFullMoveNumber() + 1); // Increment the full move number after black's move
			setTempFullMoveNumber(getFullMoveNumber()); // Store the current full move number temporarily
		}

		// Reset the half-move clock if a pawn is moved or a piece is captured
		if (piece instanceof Pawn || move.getCapturedPiece() != null) {
			setHalfMoveClock(0); // Reset the half-move clock
		} else {
			// Increment the half-move clock if neither a pawn is moved nor a piece is captured
			setHalfMoveClock(getHalfMoveClock() + 1);
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
		int colorIndex = (isWhiteAtBottom ? (move.getPiece().isWhite() ? 1 : -1) : (move.getPiece().isWhite() ? -1 : 1));

		// Check for en passant: If the pawn lands on the en passant target square, capture the piece
		if (getTileNumber(move.getNewColumn(), move.getNewRow()) == getEnPassantTile()) {
			move.setCapturedPiece(getPieceAt(move.getNewColumn(), move.getNewRow() + colorIndex));
		}

		// If the pawn moves two squares forward, set the en passant target square
		if (Math.abs(move.getPiece().getRow() - move.getNewRow()) == 2) {
			setEnPassantTile(getTileNumber(move.getNewColumn(), move.getNewRow() + colorIndex));
		} else {
			// If the pawn moves only one square, clear the en passant target square
			setEnPassantTile(-1);
		}

		// Check for pawn promotion: If the pawn reaches the promotion rank, trigger promotion logic
		checkPawnPromotion(move);
	}

	/**
	 * Checks if the pawn has reached the promotion rank and initiates the promotion process.
	 *
	 * @param move
	 * 	the move being made by the pawn
	 */
	private void checkPawnPromotion(Move move) {
		// Check if the pawn has reached the promotion rank
		boolean isPieceWhite = move.getPiece().isWhite();
		int promotionRank = isWhiteAtBottom() ? isPieceWhite ? 0 : 7 : isPieceWhite ? 7 : 0;

		if (move.getNewRow() == promotionRank) {
			if (isWhiteTurn() && getPlayerWhite() instanceof Ai || !isWhiteTurn() && getPlayerBlack() instanceof Ai) {
				promotePawn(move, new Queen(this, move.getNewColumn(), move.getNewRow(), move.getPiece().isWhite()));
				return;
			}

			setPromotion(true);

			// Show the promotion dialog to allow the player to choose a piece to promote to
			SwingUtilities.invokeLater(() -> {
				// Get the parent window
				Window window = SwingUtilities.getWindowAncestor(this);

				// Create the Promotion Dialog
				PromotionPanel dialog = new PromotionPanel(window instanceof JFrame ? (JFrame) window : null, this, move);

				// Wait for the dialog to be closed and retrieve the selected piece
				Piece chosenPiece = dialog.showDialog();

				// Proceed with the pawn promotion
				promotePawn(move, chosenPiece);

				// Refresh the board after promotion
				repaint();
			});
		}
	}

	/**
	 * Promotes a pawn to the chosen piece.
	 *
	 * @param move
	 * 	the move being made by the pawn
	 * @param chosenPiece
	 * 	the piece chosen for promotion
	 */
	public void promotePawn(Move move, Piece chosenPiece) {
		// Create the promotion piece based on the type of chosen piece
		if (chosenPiece == null) {
			// Handle the case where no piece was selected
			Logger.logError("No piece selected for promotion");
			return;
		}

		// Remove the pawn from the board and replace it with the new piece
		getPieceList().remove(move.getPiece());
		getPieceList().add(chosenPiece);

		chosenPiece.setColumn(move.getNewColumn());
		chosenPiece.setRow(move.getNewRow());
		chosenPiece.setXPos(move.getNewColumn() * getTileSize());
		chosenPiece.setYPos(move.getNewRow() * getTileSize());

		playGameSound(move);
		checkGameEnd();

		// Create a new HistoryMove object to store the move details
		HistoryMove historyMove = new HistoryMove(
			getMoveHistory().size() + 1,
			move.toAlgebraicNotation(String.valueOf(chosenPiece.getFenChar()).toUpperCase()),
			move,
			getCurrentPositionsFenNotation()
		);

		// Add the move to the move history
		addMove(historyMove);
		markHistoryMoveCell(getHistoryLookupIndex() - 1);
		setPromotion(false);

		if (isWhiteTurn() && playerBlack instanceof Ai || !isWhiteTurn() && playerWhite instanceof Ai) {
			checkForAiMove();
		}

		checkForGameEnding();
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
			if (!isMouseDragged()) {
				// TODO: animateMove(rook, targetColumn, move.getPiece().getRow());
			} else {
				// Set the rook's position immediately if the mouse is dragged
				rook.setPosition(targetColumn, move.getPiece().getRow());
			}

			// Set the castling flag to true
			setHasCastled(true);

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

					// Set the new position of the piece
					piece.setColumn(targetColumn);
					piece.setRow(targetRow);
					piece.setXPos(targetColumn * getTileSize());
					piece.setYPos(targetRow * getTileSize());
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
		getPieceList().remove(move.getCapturedPiece());

		// If the captured piece is not null and the scoreboard is available, add it to the scoreboard
		// if (move.getCapturedPiece() != null) {
		// TODO: implement scoreboard functionality to track captured pieces
		// scoreboard.addCapturedPiece(move.capturedPiece);
		// }
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
		return getPieceList().stream().filter(piece -> piece.getColumn() == column && piece.getRow() == row).findFirst().orElse(null);
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
	 * Displays possible moves for the given piece by calculating and highlighting them on the board.
	 *
	 * @param piece
	 * 	the piece for which to show possible moves
	 */
	public void showPossibleMoves(Piece piece) {
		// Clear any previously displayed possible moves
		getPossibleMoves().clear();

		// Add the calculated moves to the board's possible moves list
		getPossibleMoves().addAll(piece.getLegalMoves(this));

		// Repaint the board to visually display the possible moves
		repaint();
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
		if (move.getPiece().isWhite() != isWhiteTurn()) {
			return false; // The piece cannot move if it's not the correct player's turn
		}

		// Ensure the move is within the bounds of the board
		if (move.getNewColumn() < 0 || move.getNewColumn() >= getColumns() || move.getNewRow() < 0 || move.getNewRow() >= getRows()) {
			return false; // The move is out of bounds
		}

		// Ensure the move doesn't involve capturing a piece from the same team
		if (sameTeam(move.getPiece(), move.getCapturedPiece())) {
			return false; // Can't capture pieces of the same color
		}

		// Validate the movement according to the piece's rules
		if (!move.getPiece().isValidMovement(move.getNewColumn(), move.getNewRow())) {
			return false; // The move doesn't comply with the piece's movement rules
		}

		// Ensure the move doesn't collide with another piece
		return !move.getPiece().moveCollidesWithPiece(move.getNewColumn(), move.getNewRow()); // The move is blocked by another piece
	}

	/**
	 * Checks the current game state to determine if the game has ended.
	 * <p>
	 * This method evaluates various conditions to check if the game has reached an ending state,
	 * such as checkmate, stalemate, insufficient material, fifty-move rule, threefold repetition,
	 * time forfeit, resignation, or agreed draw. If none of these conditions are met, the game
	 * is considered to be still in progress.
	 *
	 * @return the current game ending state as a GameEnding enum value
	 */
	public GameEnding checkForGameEnding() {
		MoveValidator moveValidator = getMoveValidator();

		if (moveValidator.isCheckmate()) {
			return GameEnding.CHECKMATE;
		}

		if (moveValidator.isStalemate()) {
			return GameEnding.STALEMATE;
		}

		if (moveValidator.isInsufficientMaterial()) {
			return GameEnding.INSUFFICIENT_MATERIAL;
		}

		if (moveValidator.isFiftyMoveRule()) {
			return GameEnding.FIFTY_MOVE_RULE;
		}

		if (moveValidator.isThreefoldRepetition()) {
			return GameEnding.THREEFOLD_REPETITION;
		}

		if (moveValidator.isTimeForfeit()) {
			return GameEnding.TIME_FORFEIT;
		}

		if (moveValidator.isResignation()) {
			return GameEnding.RESIGNATION;
		}

		if (moveValidator.isAgreedDraw()) {
			return GameEnding.AGREED_DRAW;
		}

		return GameEnding.IN_PROGRESS;
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

	/**
	 * Sets a piece on the board at the specified column and row.
	 * Updates the piece's position and triggers a repaint.
	 *
	 * @param column
	 * 	the column to place the piece
	 * @param row
	 * 	the row to place the piece
	 * @param piece
	 * 	the piece to be placed, must not be null
	 */
	public void setPieceAt(int column, int row, Piece piece) {
		if (piece == null) {
			return;
		}

		piece.setColumn(column);
		piece.setRow(row);
		piece.setXPos(column * getTileSize());
		piece.setYPos(row * getTileSize());
		repaint();
	}

}
