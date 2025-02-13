/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licensed under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

	// Initialize the board with two Human players.
	private final Board BOARD = new Board(new Human(), new Human(), true);

	// ===================== Rook (bottom) tests: White rook placed on the bottom (row 6) =====================
	// In this orientation, board.setWhiteAtBottom(true) places white pieces at the bottom.
	// The FEN string "K6k/8/8/8/8/8/3R4/8 w - - 0 1" places a white rook ('R') on square d2,
	// which corresponds to board coordinates (3, 6).

	@Test
	@DisplayName("Rook (bottom) horizontal move to the right")
	void testRookHorizontalRightBottom() {
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The rook at (3,6) should be able to move horizontally to the right.
		// For example, moving to (7,6) is valid.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(7, 6));
	}

	@Test
	@DisplayName("Rook (bottom) horizontal move to the left")
	void testRookHorizontalLeftBottom() {
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The rook should be able to move horizontally to the left.
		// For example, moving from (3,6) to (0,6) is valid.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(0, 6));
	}

	@Test
	@DisplayName("Rook (bottom) vertical move upward")
	void testRookVerticalUpBottom() {
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// Moving vertically upward (decreasing y) should be valid.
		// From (3,6) to (3,0) is a clear, unobstructed path.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(3, 0));
	}

	@Test
	@DisplayName("Rook (bottom) vertical move downward")
	void testRookVerticalDownBottom() {
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// Moving vertically downward (increasing y) should be valid.
		// From (3,6) to (3,7) is valid.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(3, 7));
	}

	@Test
	@DisplayName("Rook (bottom) cannot move diagonally")
	void testRookDiagonalInvalidBottom() {
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// Rooks are not allowed to move diagonally.
		// Therefore, moves from (3,6) to (4,5) or (2,7) must be invalid.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(4, 5));
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(2, 7));
	}

	@Test
	@DisplayName("Rook (bottom) horizontal move blocked by enemy piece")
	void testRookHorizontalBlockedByEnemyBottom() {
		// Place an enemy pawn ('p') on the rook's horizontal path.
		// The FEN below puts a white rook on d2 (3,6) and an enemy pawn on f2 (5,6).
		// Row 2 (index 6) is defined as "3R1p3": 3 empty squares, R at file d, 1 empty,
		// then enemy pawn at file f, then 3 empty squares.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R1p3/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);
		Piece rook = BOARD.getPieceAt(3, 6);

		// The rook should be able to move to (4,6) (before the enemy) and capture on (5,6).
		// However, it must not be allowed to move beyond the enemy piece.
		assertTrue(rook.isValidMovement(4, 6));
		assertTrue(rook.isValidMovement(5, 6)); // capturing move
		assertFalse(rook.isValidMovement(6, 6));
		assertFalse(rook.isValidMovement(7, 6));
	}

	@Test
	@DisplayName("Rook (bottom) vertical move blocked by enemy piece")
	void testRookVerticalBlockedByEnemyBottom() {
		// Place an enemy pawn ('p') vertically in the rook's path.
		// The FEN below places the enemy pawn on d4 (i.e. at (3,4)) along the rook's column.
		// The board (when white is at bottom) is arranged so that the rook is at (3,6).
		// Row 4 is represented as "3p4".
		BOARD.loadPositionFromFen("K6k/8/8/8/3p4/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);
		Piece rook = BOARD.getPieceAt(3, 6);

		// The rook can move vertically upward up to (3,5) and it can capture on (3,4),
		// but it must not be allowed to move to (3,3) or beyond.
		assertTrue(rook.isValidMovement(3, 5));
		assertTrue(rook.isValidMovement(3, 4)); // capturing move
		assertFalse(rook.isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Rook (bottom) legal moves count on an unobstructed board")
	void testRookLegalMoveCountBottom() {
		// On an empty board (except for the kings and the rook),
		// the white rook on d2 (at (3,6)) should have:
		// - Horizontal moves: to the left: (0,6), (1,6), (2,6) => 3 moves;
		//   to the right: (4,6), (5,6), (6,6), (7,6) => 4 moves;
		// - Vertical moves: upward: (3,5), (3,4), (3,3), (3,2), (3,1), (3,0) => 6 moves;
		//   downward: (3,7) => 1 move.
		// Total expected moves: 3 + 4 + 6 + 1 = 14.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);
		Piece rook = BOARD.getPieceAt(3, 6);

		assertEquals(14, rook.getLegalMoves(BOARD).size());
	}

	@Test
	@DisplayName("Rook (bottom) cannot move out of board")
	void testRookMoveOutOfBoardBottom() {
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3R4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);
		Piece rook = BOARD.getPieceAt(3, 6);

		// Moves that lead off the board should be invalid.
		// For example, moving to a negative file or a row beyond 7.
		assertFalse(rook.isValidMovement(-1, 6));
		assertFalse(rook.isValidMovement(3, 8));
	}

	// ===================== Rook (top) tests: White rook placed on the top (row 1) =====================
	// In this orientation, board.setWhiteAtBottom(false) places white pieces at the top.
	// The FEN string "K6k/3R4/8/8/8/8/8/8 w - - 0 1" puts a white rook on square d7,
	// corresponding to board coordinates (3, 1).

	@Test
	@DisplayName("Rook (top) horizontal move to the right")
	void testRookHorizontalRightTop() {
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The rook at (3,1) should be able to move horizontally to the right.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(7, 1));
	}

	@Test
	@DisplayName("Rook (top) horizontal move to the left")
	void testRookHorizontalLeftTop() {
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The rook should be able to move horizontally to the left.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(0, 1));
	}

	@Test
	@DisplayName("Rook (top) vertical move upward")
	void testRookVerticalUpTop() {
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// From (3,1), moving vertically upward (to row 0) is valid.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(3, 0));
	}

	@Test
	@DisplayName("Rook (top) vertical move downward")
	void testRookVerticalDownTop() {
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// From (3,1), moving vertically downward (to row 7) is valid.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(3, 7));
	}

	@Test
	@DisplayName("Rook (top) cannot move diagonally")
	void testRookDiagonalInvalidTop() {
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// Diagonal moves should be invalid for a rook.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(4, 2));
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(2, 0));
	}

	@Test
	@DisplayName("Rook (top) horizontal move blocked by enemy piece")
	void testRookHorizontalBlockedByEnemyTop() {
		// Place an enemy pawn on the same row (row 1) to block horizontal movement.
		// Here the FEN modifies rank 7 (row index 1) as "3R1p3" to place an enemy pawn at (5,1).
		BOARD.loadPositionFromFen("K6k/3R1p3/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);
		Piece rook = BOARD.getPieceAt(3, 1);

		// The rook can move to (4,1) and capture on (5,1) but cannot move beyond.
		assertTrue(rook.isValidMovement(4, 1));
		assertTrue(rook.isValidMovement(5, 1)); // capturing move
		assertFalse(rook.isValidMovement(6, 1));
		assertFalse(rook.isValidMovement(7, 1));
	}

	@Test
	@DisplayName("Rook (top) vertical move blocked by enemy piece")
	void testRookVerticalBlockedByEnemyTop() {
		// Place an enemy pawn in the vertical path.
		// For a rook at (3,1), put an enemy pawn at (3,3) (i.e. on rank 5).
		// The FEN below places the enemy pawn on rank 5 (row index 3) as "3p4".
		BOARD.loadPositionFromFen("K6k/3R4/8/3p4/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);
		Piece rook = BOARD.getPieceAt(3, 1);

		// The rook can move down to (3,2) and capture on (3,3),
		// but it must not be allowed to move to (3,4) or further down.
		assertTrue(rook.isValidMovement(3, 2));
		assertTrue(rook.isValidMovement(3, 3)); // capturing move
		assertFalse(rook.isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Rook (top) legal moves count on an unobstructed board")
	void testRookLegalMoveCountTop() {
		// On an empty board (except for the kings and the rook),
		// the white rook on d7 (at (3,1)) should have:
		// - Horizontal moves: left: (0,1), (1,1), (2,1) => 3 moves;
		//   right: (4,1), (5,1), (6,1), (7,1) => 4 moves;
		// - Vertical moves: upward: (3,0) => 1 move;
		//   downward: (3,2), (3,3), (3,4), (3,5), (3,6), (3,7) => 6 moves.
		// Total expected moves: 3 + 4 + 1 + 6 = 14.
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);
		Piece rook = BOARD.getPieceAt(3, 1);

		assertEquals(14, rook.getLegalMoves(BOARD).size());
	}

	@Test
	@DisplayName("Rook (top) cannot move out of board")
	void testRookMoveOutOfBoardTop() {
		BOARD.loadPositionFromFen("K6k/3R4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);
		Piece rook = BOARD.getPieceAt(3, 1);

		// Moves that fall outside the board limits should be invalid.
		assertFalse(rook.isValidMovement(-1, 1));
		assertFalse(rook.isValidMovement(3, -1));
		assertFalse(rook.isValidMovement(3, 8));
	}
}
