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

class PawnTest {

	// Initialize the board with two Human players.
	private final Board BOARD = new Board(new Human(), new Human(), true);

	// ===================== Pawn (bottom) tests: White pawn placed on the bottom =====================
	// In this orientation, board.setWhiteAtBottom(true) places white pieces at the bottom.
	// According to the FEN, the pawn is placed on square d2 (board coordinates (3,6)).
	// For a pawn at the bottom, moving forward means moving upward (i.e. decreasing the y coordinate).

	@Test
	@DisplayName("Pawn (bottom) one step forward")
	void testPawnOneStepForwardBottom() {
		// Load a board position with a white pawn on d2 (3,6).
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		// Place white pieces at the bottom.
		BOARD.setWhiteAtBottom(true);

		// The pawn should be able to move one step forward from (3,6) to (3,5).
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(3, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward")
	void testPawnTwoStepForwardBottom() {
		// Load a board position with a white pawn on d2.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// On its first move, the pawn should be allowed to move two squares forward
		// from (3,6) to (3,4) if the path is unobstructed.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward (first move)")
	void testPawnTwoStepForwardFirstMoveBottom() {
		// Confirm that on its first move the pawn can move two squares forward.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn on (3,6) must be able to move to (3,4) on its first move.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward (not first move)")
	void testPawnTwoStepForwardNotFirstMoveBottom() {
		// Test that a pawn which is not on its first move cannot move two squares forward.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		Piece pawn = BOARD.getPieceAt(3, 6);
		// Mark the pawn as having moved before.
		pawn.setFirstMove(false);

		// Now, the pawn should not be allowed to move two squares forward.
		assertFalse(pawn.isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) not more than 2 valid moves")
	void testPawnValidMoveCountBottom() {
		// Verify that an unobstructed pawn has exactly two forward moves: one or two squares.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn at (3,6) should have exactly 2 legal moves.
		assertEquals(2, BOARD.getPieceAt(3, 6).getLegalMoves(BOARD).size());
	}

	@Test
	@DisplayName("Pawn (bottom) blocked by black piece directly in front")
	void testPawnOneStepForwardBottomBlockedByPieceZeroSpacing() {
		// Place an enemy pawn directly in front of the white pawn.
		// The enemy piece ('p') is on the square immediately ahead of the pawn (3,5).
		BOARD.loadPositionFromFen("K6k/8/8/8/8/3p4/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn should not be able to move forward if the square is occupied.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(3, 5));
		// Without a free path, even the two-square move is invalid.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward blocked by black piece with one space in between")
	void testPawnTwoStepForwardBottomBlockedByPieceOneSpacing() {
		// Place an enemy pawn on the square two steps ahead of the white pawn (at (3,4)),
		// while leaving the immediate square (3,5) empty.
		BOARD.loadPositionFromFen("K6k/8/8/8/3p4/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn should be able to move one square forward but not two squares,
		// since the second square is blocked by an enemy piece.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(3, 5));
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move backward")
	void testPawnCannotMoveBackwardBottom() {
		// Ensure that a pawn cannot move backward.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// A move from (3,6) to (3,7) (backward move) must be invalid.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(3, 7));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move sideways")
	void testPawnCannotMoveSidewaysBottom() {
		// Verify that a pawn cannot move horizontally.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// Horizontal moves (to (2,6) or (4,6)) are not permitted.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(2, 6));
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(4, 6));
	}

	@Test
	@DisplayName("Pawn (bottom) capturing right diagonally")
	void testPawnCapturingDiagonallyRightBottom() {
		// Place an enemy pawn diagonally to the right of the white pawn.
		// The enemy pawn is positioned at (4,5) relative to the white pawn at (3,6).
		BOARD.loadPositionFromFen("K6k/8/8/8/8/4p3/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn should be able to capture the enemy piece diagonally.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(4, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) capturing left diagonally")
	void testPawnCapturingDiagonallyLeftBottom() {
		// Place an enemy pawn diagonally to the left of the white pawn.
		// The enemy pawn is at (2,5) relative to the pawn at (3,6).
		BOARD.loadPositionFromFen("K6k/8/8/8/8/2p5/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn should be able to capture diagonally to the left.
		assertTrue(BOARD.getPieceAt(3, 6).isValidMovement(2, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move diagonally if no enemy piece present")
	void testPawnDiagonalInvalidWithoutCaptureBottom() {
		// With no enemy piece present on the diagonal squares,
		// the pawn should not be allowed to move diagonally.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// Both diagonal moves from (3,6) must be invalid if there is no capture.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(2, 5));
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(4, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move out of board")
	void testPawnMoveOutOfBoardBottom() {
		// Test that a pawn cannot move to a square outside the board boundaries.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// A move to an invalid row (e.g., y = -1) must be rejected.
		assertFalse(BOARD.getPieceAt(3, 6).isValidMovement(3, -1));
	}

	@Test
	@DisplayName("Pawn (bottom) has no legal moves when completely blocked")
	void testPawnNoLegalMovesWhenBlockedBottom() {
		// Place an enemy pawn directly in front of the white pawn so that it is completely blocked.
		BOARD.loadPositionFromFen("K6k/8/8/8/8/3p4/3P4/8 w - - 0 1");
		BOARD.setWhiteAtBottom(true);

		// The pawn should have no legal moves when its forward path is obstructed.
		assertTrue(BOARD.getPieceAt(3, 6).getLegalMoves(BOARD).isEmpty());
	}

	// ===================== Pawn (top) tests: White pawn placed on the top =====================
	// In this orientation, board.setWhiteAtBottom(false) places white pieces at the top.
	// The FEN string "K6k/3P4/8/8/8/8/8/8 w - - 0 1" positions the pawn on square d7 (board coordinates (3,1)).
	// For a pawn at the top, moving forward means moving downward (i.e. increasing the y coordinate).

	@Test
	@DisplayName("Pawn (top) one step forward")
	void testPawnOneStepForwardTop() {
		// Load a board position with a white pawn on d7.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		// Place white pieces at the top.
		BOARD.setWhiteAtBottom(false);

		// The pawn should move one step forward (downward) from (3,1) to (3,2).
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(3, 2));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward")
	void testPawnTwoStepForwardTop() {
		// Load a board position with a white pawn on d7.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should be able to move two squares forward from (3,1) to (3,3)
		// on its first move if both squares are unobstructed.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward (first move)")
	void testPawnTwoStepForwardFirstMoveTop() {
		// Confirm that the pawn on d7 can move two squares forward on its first move.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should be allowed to move from (3,1) to (3,3).
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward (not first move)")
	void testPawnTwoStepForwardNotFirstMoveTop() {
		// Test that a pawn which has already moved cannot move two squares forward.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		Piece pawn = BOARD.getPieceAt(3, 1);
		// Mark the pawn as not on its first move.
		pawn.setFirstMove(false);

		// The pawn should not be able to move two squares forward from (3,1) to (3,3).
		assertFalse(pawn.isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) has not more than 2 valid moves")
	void testPawnValidMoveCountTop() {
		// Verify that an unobstructed pawn at the top has exactly two legal moves.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn on (3,1) should have exactly 2 legal moves.
		assertEquals(2, BOARD.getPieceAt(3, 1).getLegalMoves(BOARD).size());
	}

	@Test
	@DisplayName("Pawn (top) blocked by black piece directly in front")
	void testPawnOneStepForwardTopBlockedByPieceZeroSpacing() {
		// Place an enemy pawn directly in front of the pawn at d7 (3,1).
		// The enemy piece is positioned at (3,2).
		BOARD.loadPositionFromFen("K6k/3p4/3P4/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should not be allowed to move forward if its path is blocked.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(3, 2));
		// Consequently, the two-square move is also invalid.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward blocked by black piece with one space in between")
	void testPawnTwoStepForwardTopBlockedByPieceOneSpacing() {
		// Place an enemy pawn on the square two steps ahead of the pawn at d7,
		// leaving the immediate forward square (3,2) free.
		BOARD.loadPositionFromFen("K6k/3P4/8/3p4/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should be allowed to move one square forward but not two squares forward.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(3, 2));
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) cannot move backward")
	void testPawnCannotMoveBackwardTop() {
		// Test that a pawn cannot move backward from its current position.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// Moving from (3,1) to (3,0) is a backward move and must be rejected.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(3, 0));
	}

	@Test
	@DisplayName("Pawn (top) cannot move sideways")
	void testPawnCannotMoveSidewaysTop() {
		// Verify that a pawn at the top is not allowed to move horizontally.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// Moves to (2,1) or (4,1) are invalid for a pawn.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(2, 1));
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(4, 1));
	}

	@Test
	@DisplayName("Pawn (top) capturing right diagonally")
	void testPawnCapturingDiagonallyRightTop() {
		// Place an enemy pawn diagonally in front to the right of the white pawn at d7.
		// The enemy pawn is placed at (4,2) relative to the pawn on (3,1).
		BOARD.loadPositionFromFen("K6k/3P4/4p3/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should be able to capture the enemy piece diagonally to the right.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(4, 2));
	}

	@Test
	@DisplayName("Pawn (top) capturing left diagonally")
	void testPawnCapturingDiagonallyLeftTop() {
		// Place an enemy pawn diagonally in front to the left of the white pawn at d7.
		// The enemy pawn is positioned at (2,2) relative to (3,1).
		BOARD.loadPositionFromFen("K6k/3P4/2p5/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should be able to capture diagonally to the left.
		assertTrue(BOARD.getPieceAt(3, 1).isValidMovement(2, 2));
	}

	@Test
	@DisplayName("Pawn (top) cannot move diagonally without enemy piece")
	void testPawnDiagonalInvalidWithoutCaptureTop() {
		// When there is no enemy piece on the diagonal, the pawn should not be allowed to move diagonally.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// Both diagonal moves from (3,1) must be invalid.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(2, 2));
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(4, 2));
	}

	@Test
	@DisplayName("Pawn (top) cannot move out of board")
	void testPawnMoveOutOfBoardTop() {
		// Verify that the pawn cannot move to a square outside the board boundaries.
		BOARD.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// Moving to an invalid row (e.g., row 8) should be rejected.
		assertFalse(BOARD.getPieceAt(3, 1).isValidMovement(3, 8));
	}

	@Test
	@DisplayName("Pawn (top) has no legal moves when completely blocked")
	void testPawnNoLegalMovesWhenBlockedTop() {
		// Place an enemy pawn directly in front of the white pawn at d7 so that its path is blocked.
		BOARD.loadPositionFromFen("K6k/3P4/3p4/8/8/8/8/8 w - - 0 1");
		BOARD.setWhiteAtBottom(false);

		// The pawn should have no legal moves when its forward path is obstructed.
		assertTrue(BOARD.getPieceAt(3, 1).getLegalMoves(BOARD).isEmpty());
	}
}
