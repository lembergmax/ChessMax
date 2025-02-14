package com.mlprograms.chess.game.engine.state;

import com.mlprograms.chess.game.pieces.King;
import com.mlprograms.chess.game.pieces.Queen;
import com.mlprograms.chess.game.ui.Board;
import lombok.Getter;

@Getter
public class PositionEvaluation {

	private final Board BOARD;

	/**
	 * Constructor for PositionEvaluation.
	 *
	 * @param board the chess board instance.
	 */
	public PositionEvaluation(Board board) {
		this.BOARD = board;
	}

	/**
	 * Evaluates the current game state based on material, queen count, and king positions.
	 * New heuristic:
	 * <ul>
	 *   <li>If very few pieces (excluding kings) are present (piecesCount &lt; 6), then END_GAME.</li>
	 *   <li>If exactly one queen is present, then END_GAME.</li>
	 *   <li>If no queens are present:
	 *     <ul>
	 *       <li>If material is reduced (piecesCount &lt; 28) → END_GAME,</li>
	 *       <li>If almost full material is present (piecesCount ≥ 28) → MIDDLE_GAME.</li>
	 *     </ul>
	 *   </li>
	 *   <li>If both kings are central → END_GAME.</li>
	 *   <li>If both kings are in a typical castled position → MIDDLE_GAME.</li>
	 *   <li>If both kings are on their starting positions:
	 *     <ul>
	 *       <li>If material is nearly complete (piecesCount ≥ 29) → OPENING,</li>
	 *       <li>otherwise → MIDDLE_GAME.</li>
	 *     </ul>
	 *   </li>
	 *   <li>Otherwise, default to MIDDLE_GAME.</li>
	 * </ul>
	 *
	 * @return the evaluated game state: OPENING, MIDDLE_GAME, or END_GAME.
	 */
	public GameState evaluateGameState() {
		// Calculate the number of pieces (excluding both kings)
		int piecesCount = BOARD.getPieceList().size() - 2;
		// Count the number of queens
		int queensCount = (int) BOARD.getPieceList().stream()
			                        .filter(piece -> piece instanceof Queen)
			                        .count();

		// If very few pieces are present → End game
		if (piecesCount < 6) {
			return GameState.END_GAME;
		}
		// If exactly one queen is present → End game
		if (queensCount == 1) {
			return GameState.END_GAME;
		}
		// If no queens are present, differentiate based on material
		if (queensCount == 0) {
			if (piecesCount < 28) {
				return GameState.END_GAME;
			} else {
				return GameState.MIDDLE_GAME;
			}
		}
		// If both kings are positioned centrally → End game
		if (isKingCentral(true) && isKingCentral(false)) {
			return GameState.END_GAME;
		}
		// If both kings are in a typical castled position → Middle game
		if (areKingsCastled()) {
			return GameState.MIDDLE_GAME;
		}
		// If both kings are on their starting squares:
		if (areKingsOnStartingPositions()) {
			// With nearly complete material (e.g., opening phase) → OPENING,
			// otherwise (e.g., in a reduced but still early position) → MIDDLE_GAME.
			if (piecesCount >= 29) {
				return GameState.OPENING;
			} else {
				return GameState.MIDDLE_GAME;
			}
		}
		// Fallback: if material is reduced → Middle game, otherwise → Opening
		if (piecesCount < 30) {
			return GameState.MIDDLE_GAME;
		}
		return GameState.OPENING;
	}

	/**
	 * Checks if the king (based on color) is positioned centrally.
	 * A king is considered central if its Euclidean distance from the board's center (4.5, 4.5)
	 * is less than or equal to 2.
	 *
	 * @param whiteKing true for the white king; false for the black king.
	 * @return true if the king is central, otherwise false.
	 */
	private boolean isKingCentral(boolean whiteKing) {
		King king = BOARD.getMoveValidator().findKing(whiteKing);
		double dx = king.getColumn() - 4.5;
		double dy = king.getRow() - 4.5;
		double distance = Math.sqrt(dx * dx + dy * dy);
		return distance <= 2;
	}

	/**
	 * Checks if both kings are on their starting squares.
	 * Assumptions:
	 *   - The white king starts at e1 (0-indexed: column 4, row 7).
	 *   - The black king starts at e8 (0-indexed: column 4, row 0).
	 *
	 * @return true if both kings are on their starting positions, otherwise false.
	 */
	private boolean areKingsOnStartingPositions() {
		King whiteKing = BOARD.getMoveValidator().findKing(true);
		King blackKing = BOARD.getMoveValidator().findKing(false);
		return whiteKing.getColumn() == 4 && whiteKing.getRow() == 7
			       && blackKing.getColumn() == 4 && blackKing.getRow() == 0;
	}

	/**
	 * Checks if both kings are in a typical castled position.
	 * For white: g1 (column 6, row 7) or c1 (column 2, row 7).<br>
	 * For black: g8 (column 6, row 0) or c8 (column 2, row 0).
	 *
	 * @return true if both kings are in a castled position, otherwise false.
	 */
	private boolean areKingsCastled() {
		King whiteKing = BOARD.getMoveValidator().findKing(true);
		King blackKing = BOARD.getMoveValidator().findKing(false);
		boolean whiteCastled = (whiteKing.getColumn() == 6 && whiteKing.getRow() == 7)
			                       || (whiteKing.getColumn() == 2 && whiteKing.getRow() == 7);
		boolean blackCastled = (blackKing.getColumn() == 6 && blackKing.getRow() == 0)
			                       || (blackKing.getColumn() == 2 && blackKing.getRow() == 0);
		return whiteCastled && blackCastled;
	}
}
