/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Bishop;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

	private Board board;
	private Bishop whiteBishop;
	private Bishop blackBishop;

	@BeforeEach
	void setUp() {
		// Initialize the board with two Human players.
		board = new Board(new Human(), new Human(), true);
		// Clear the board's piece list for a controlled test environment.
		board.getPieceList().clear();

		// Place a white bishop at (2,0) and a black bishop at (2,7)
		whiteBishop = new Bishop(board, 2, 0, true);
		blackBishop = new Bishop(board, 2, 7, false);
		board.getPieceList().add(whiteBishop);
		board.getPieceList().add(blackBishop);
	}

	// ===================== White Bishop Tests =====================

	@Test
	@DisplayName("White Bishop: Valid diagonal move up-right from initial position")
	void testWhiteBishopDiagonalUpRight() {
		// From (2,0), a move to (3,1) or (5,3) is a valid diagonal up-right move.
		assertTrue(whiteBishop.isValidMovement(3, 1));
		assertTrue(whiteBishop.isValidMovement(5, 3));
	}

	@Test
	@DisplayName("White Bishop: Valid diagonal move up-left from initial position")
	void testWhiteBishopDiagonalUpLeft() {
		// From (2,0), moving up-left leads to (1,1) and (0,2).
		assertTrue(whiteBishop.isValidMovement(1, 1));
		assertTrue(whiteBishop.isValidMovement(0, 2));
	}

	@Test
	@DisplayName("White Bishop: Valid diagonal move down-right from a central position")
	void testWhiteBishopDiagonalDownRight() {
		// Move the bishop to a central position.
		whiteBishop.setPosition(3, 3);
		// From (3,3), moves to (4,4) and (6,6) are valid down-right diagonal moves.
		assertTrue(whiteBishop.isValidMovement(4, 4));
		assertTrue(whiteBishop.isValidMovement(6, 6));
	}

	@Test
	@DisplayName("White Bishop: Valid diagonal move down-left from a central position")
	void testWhiteBishopDiagonalDownLeft() {
		// Place the bishop in the center.
		whiteBishop.setPosition(3, 3);
		// From (3,3), moves to (2,4) and (0,6) are valid down-left diagonal moves.
		assertTrue(whiteBishop.isValidMovement(2, 4));
		assertTrue(whiteBishop.isValidMovement(0, 6));
	}

	@Test
	@DisplayName("White Bishop: Invalid horizontal move")
	void testWhiteBishopInvalidHorizontal() {
		// A horizontal move (same row, different column) is not allowed.
		assertFalse(whiteBishop.isValidMovement(5, 0));
	}

	@Test
	@DisplayName("White Bishop: Invalid vertical move")
	void testWhiteBishopInvalidVertical() {
		// A vertical move (same column, different row) is not allowed.
		assertFalse(whiteBishop.isValidMovement(2, 5));
	}

	@Test
	@DisplayName("White Bishop: Moving to the same position is invalid")
	void testWhiteBishopSamePosition() {
		// Moving to the square it currently occupies is invalid.
		assertFalse(whiteBishop.isValidMovement(whiteBishop.getColumn(), whiteBishop.getRow()));
	}

	@Test
	@DisplayName("White Bishop: Moving out of board bounds is invalid")
	void testWhiteBishopOutOfBounds() {
		// Attempt to move to a square outside the board (e.g., (8,8)).
		assertFalse(whiteBishop.isValidMovement(8, 8));
	}

	@Test
	@DisplayName("White Bishop: Legal moves count in an unobstructed scenario")
	void testWhiteBishopLegalMovesUnobstructed() {
		// Place the white bishop in the center of an otherwise empty board.
		whiteBishop.setPosition(3, 3);
		// Remove all other pieces except the white bishop.
		board.getPieceList().removeIf(piece -> piece != whiteBishop);
		// For a bishop at (3,3) on an 8x8 board, the number of diagonal moves is:
		// up-right: (4,2), (5,1), (6,0)  -> 3 moves
		// up-left:  (2,2), (1,1), (0,0)  -> 3 moves
		// down-right: (4,4), (5,5), (6,6), (7,7)  -> 4 moves
		// down-left: (2,4), (1,5), (0,6)  -> 3 moves
		// Total = 3 + 3 + 4 + 3 = 13 moves.
		assertEquals(13, whiteBishop.getLegalMoves(board).size());
	}

	@Test
	@DisplayName("White Bishop: Capture opponent piece")
	void testWhiteBishopCaptureOpponent() {
		// Place an opponent (black) bishop on a diagonal path.
		Bishop blackBishopToCapture = new Bishop(board, 4, 2, false);
		board.getPieceList().add(blackBishopToCapture);
		// White bishop from (2,0) should be able to move to (4,2) to capture the opponent.
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	@DisplayName("White Bishop: Cannot capture own piece")
	void testWhiteBishopCannotCaptureOwnPiece() {
		// Place another white piece on the diagonal path.
		Bishop whiteBishopToBlock = new Bishop(board, 4, 1, true);
		board.getPieceList().add(whiteBishopToBlock);
		// The white bishop should not be allowed to move to (4,1) because it is occupied by a friendly piece.
		assertFalse(whiteBishop.isValidMovement(4, 1));
	}

	@Test
	@DisplayName("White Bishop: Move blocked by a piece along the diagonal")
	void testWhiteBishopBlockedByPiece() {
		// To test blocking, place a friendly piece between the white bishop and its target.
		// For a move from (2,0) to (5,3), the path includes (3,1) and (4,2).
		// Place a blocking piece at (4,2).
		Bishop blockingBishop = new Bishop(board, 4, 2, true);
		board.getPieceList().add(blockingBishop);
		// The move to (5,3) should be invalid because the path is obstructed.
		assertFalse(whiteBishop.isValidMovement(5, 3));
		// Also, moving directly onto the blocking piece should be invalid if it is a friendly piece.
		assertFalse(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	@DisplayName("White Bishop: Move allowed when path is clear")
	void testWhiteBishopNotBlocked() {
		// Ensure the path is clear by removing all other pieces except the white bishop.
		board.getPieceList().removeIf(piece -> piece != whiteBishop);
		// The white bishop at (2,0) should be able to move diagonally to (5,3).
		assertTrue(whiteBishop.isValidMovement(5, 3));
	}

	// ===================== Black Bishop Tests =====================

	@Test
	@DisplayName("Black Bishop: Valid diagonal move down-right from central position")
	void testBlackBishopDiagonalDownRight() {
		// Reposition the black bishop to the center.
		blackBishop.setPosition(4, 4);
		// From (4,4), moves to (5,5) and (7,7) are valid down-right diagonal moves.
		assertTrue(blackBishop.isValidMovement(5, 5));
		assertTrue(blackBishop.isValidMovement(7, 7));
	}

	@Test
	@DisplayName("Black Bishop: Valid diagonal move down-left from central position")
	void testBlackBishopDiagonalDownLeft() {
		blackBishop.setPosition(4, 4);
		// From (4,4), moves to (3,5) and (2,6) are valid down-left diagonal moves.
		assertTrue(blackBishop.isValidMovement(3, 5));
		assertTrue(blackBishop.isValidMovement(2, 6));
	}

	@Test
	@DisplayName("Black Bishop: Valid diagonal move up-right from central position")
	void testBlackBishopDiagonalUpRight() {
		blackBishop.setPosition(4, 4);
		// From (4,4), moves to (5,3) and (7,1) are valid up-right diagonal moves.
		assertTrue(blackBishop.isValidMovement(5, 3));
		assertTrue(blackBishop.isValidMovement(7, 1));
	}

	@Test
	@DisplayName("Black Bishop: Valid diagonal move up-left from central position")
	void testBlackBishopDiagonalUpLeft() {
		blackBishop.setPosition(4, 4);
		// From (4,4), moves to (3,3) and (0,0) are valid up-left diagonal moves.
		assertTrue(blackBishop.isValidMovement(3, 3));
		assertTrue(blackBishop.isValidMovement(0, 0));
	}

	@Test
	@DisplayName("Black Bishop: Invalid horizontal move")
	void testBlackBishopInvalidHorizontal() {
		blackBishop.setPosition(4, 4);
		// A horizontal move, such as to (6,4), is invalid.
		assertFalse(blackBishop.isValidMovement(6, 4));
	}

	@Test
	@DisplayName("Black Bishop: Invalid vertical move")
	void testBlackBishopInvalidVertical() {
		blackBishop.setPosition(4, 4);
		// A vertical move, such as to (4,2), is invalid.
		assertFalse(blackBishop.isValidMovement(4, 2));
	}

	@Test
	@DisplayName("Black Bishop: Moving to the same position is invalid")
	void testBlackBishopSamePosition() {
		blackBishop.setPosition(4, 4);
		// Moving to its current position should return false.
		assertFalse(blackBishop.isValidMovement(4, 4));
	}

	@Test
	@DisplayName("Black Bishop: Moving out of board bounds is invalid")
	void testBlackBishopOutOfBounds() {
		blackBishop.setPosition(4, 4);
		// Attempt moves to squares outside the board.
		assertFalse(blackBishop.isValidMovement(8, -1));
		assertFalse(blackBishop.isValidMovement(9, 9));
	}

	@Test
	@DisplayName("Black Bishop: Capture opponent piece")
	void testBlackBishopCaptureOpponent() {
		// Place a white bishop on a diagonal path from the black bishop.
		Bishop whiteBishopToCapture = new Bishop(board, 6, 2, true);
		board.getPieceList().add(whiteBishopToCapture);
		blackBishop.setPosition(4, 4);
		// The black bishop should be allowed to move to (6,2) to capture the opponent.
		assertTrue(blackBishop.isValidMovement(6, 2));
	}

	@Test
	@DisplayName("Black Bishop: Cannot capture own piece")
	void testBlackBishopCannotCaptureOwnPiece() {
		// Place a black piece on the diagonal path.
		Bishop blackBishopToBlock = new Bishop(board, 6, 2, false);
		board.getPieceList().add(blackBishopToBlock);
		blackBishop.setPosition(4, 4);
		// Moving to (6,2) should be invalid because the square is occupied by a friendly piece.
		assertFalse(blackBishop.isValidMovement(6, 2));
	}

	@Test
	@DisplayName("Black Bishop: Move blocked by a piece along the diagonal")
	void testBlackBishopBlockedByPiece() {
		// For the black bishop at (4,4), a move to (7,7) requires an unobstructed diagonal.
		// Place a blocking piece at (6,6).
		Bishop blockingBishop = new Bishop(board, 6, 6, false);
		board.getPieceList().add(blockingBishop);
		blackBishop.setPosition(4, 4);
		// The move to (7,7) should be invalid because the path is blocked.
		assertFalse(blackBishop.isValidMovement(7, 7));
	}

	@Test
	@DisplayName("Black Bishop: Move allowed when path is clear")
	void testBlackBishopNotBlocked() {
		// Remove any obstructing pieces so the path is clear.
		board.getPieceList().removeIf(piece -> piece != blackBishop);
		blackBishop.setPosition(4, 4);
		// Now, moving to (7,7) should be allowed.
		assertTrue(blackBishop.isValidMovement(7, 7));
	}
}
