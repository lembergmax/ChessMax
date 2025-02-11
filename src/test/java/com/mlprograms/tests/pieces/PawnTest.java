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

	private Board board = new Board(new Human(), new Human(), true);;

	@Test
	@DisplayName("Pawn (bottom) one step forward")
	void testPawnOneStepForwardBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).isValidMovement(3, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward")
	void testPawnTwoStepForwardBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward (first move)")
	void testPawnTwoStepForwardFirstMoveBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward (not first move)")
	void testPawnTwoStepForwardNotFirstMoveBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		Piece pawn = board.getPieceAt(3, 6);
		pawn.setFirstMove(false);

		assertFalse(pawn.isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) not more than 2 valid moves")
	void testPawnValidMoveCountBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertEquals(2, board.getPieceAt(3, 6).getLegalMoves(board).size());
	}

	@Test
	@DisplayName("Pawn (bottom) blocked by black piece directly in front")
	void testPawnOneStepForwardBottomBlockedByPieceZeroSpacing() {
		board.loadPositionFromFen("K6k/8/8/8/8/3p4/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertFalse(board.getPieceAt(3, 6).isValidMovement(3, 5));
		assertFalse(board.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) two steps forward blocked by black piece with one space in between")
	void testPawnTwoStepForwardBottomBlockedByPieceOneSpacing() {
		board.loadPositionFromFen("K6k/8/8/8/3p4/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).isValidMovement(3, 5));
		assertFalse(board.getPieceAt(3, 6).isValidMovement(3, 4));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move backward")
	void testPawnCannotMoveBackwardBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertFalse(board.getPieceAt(3, 6).isValidMovement(3, 7));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move sideways")
	void testPawnCannotMoveSidewaysBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertFalse(board.getPieceAt(3, 6).isValidMovement(2, 6));
		assertFalse(board.getPieceAt(3, 6).isValidMovement(4, 6));
	}

	@Test
	@DisplayName("Pawn (bottom) capturing right diagonally")
	void testPawnCapturingDiagonallyRightBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/4p3/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).isValidMovement(4, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) capturing left diagonally")
	void testPawnCapturingDiagonallyLeftBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/2p5/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).isValidMovement(2, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move diagonally if no enemy piece present")
	void testPawnDiagonalInvalidWithoutCaptureBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertFalse(board.getPieceAt(3, 6).isValidMovement(2, 5));
		assertFalse(board.getPieceAt(3, 6).isValidMovement(4, 5));
	}

	@Test
	@DisplayName("Pawn (bottom) cannot move out of board")
	void testPawnMoveOutOfBoardBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/8/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertFalse(board.getPieceAt(3, 6).isValidMovement(3, -1));
	}

	@Test
	@DisplayName("Pawn (bottom) has no legal moves when completely blocked")
	void testPawnNoLegalMovesWhenBlockedBottom() {
		board.loadPositionFromFen("K6k/8/8/8/8/3p4/3P4/8 w - - 0 1");
		board.setWhiteAtBottom(true);

		assertTrue(board.getPieceAt(3, 6).getLegalMoves(board).isEmpty());
	}






	@Test
	@DisplayName("Pawn (top) one step forward")
	void testPawnOneStepForwardTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).isValidMovement(3, 2));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward")
	void testPawnTwoStepForwardTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward (first move)")
	void testPawnTwoStepForwardFirstMoveTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward (not first move)")
	void testPawnTwoStepForwardNotFirstMoveTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		Piece pawn = board.getPieceAt(3, 1);
		pawn.setFirstMove(false);

		assertFalse(pawn.isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) has not more than 2 valid moves")
	void testPawnValidMoveCountTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertEquals(2, board.getPieceAt(3, 1).getLegalMoves(board).size());
	}

	@Test
	@DisplayName("Pawn (top) blocked by black piece directly in front")
	void testPawnOneStepForwardTopBlockedByPieceZeroSpacing() {
		board.loadPositionFromFen("K6k/3p4/3P4/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertFalse(board.getPieceAt(3, 1).isValidMovement(3, 2));
		assertFalse(board.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) two steps forward blocked by black piece with one space in between")
	void testPawnTwoStepForwardTopBlockedByPieceOneSpacing() {
		board.loadPositionFromFen("K6k/3P4/8/3p4/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).isValidMovement(3, 2));
		assertFalse(board.getPieceAt(3, 1).isValidMovement(3, 3));
	}

	@Test
	@DisplayName("Pawn (top) cannot move backward")
	void testPawnCannotMoveBackwardTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertFalse(board.getPieceAt(3, 1).isValidMovement(3, 0));
	}

	@Test
	@DisplayName("Pawn (top) cannot move sideways")
	void testPawnCannotMoveSidewaysTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertFalse(board.getPieceAt(3, 1).isValidMovement(2, 1));
		assertFalse(board.getPieceAt(3, 1).isValidMovement(4, 1));
	}

	@Test
	@DisplayName("Pawn (top) capturing right diagonally")
	void testPawnCapturingDiagonallyRightTop() {
		board.loadPositionFromFen("K6k/3P4/4p3/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).isValidMovement(4, 2));
	}

	@Test
	@DisplayName("Pawn (top) capturing left diagonally")
	void testPawnCapturingDiagonallyLeftTop() {
		board.loadPositionFromFen("K6k/3P4/2p5/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).isValidMovement(2, 2));
	}

	@Test
	@DisplayName("Pawn (top) cannot move diagonally without enemy piece")
	void testPawnDiagonalInvalidWithoutCaptureTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertFalse(board.getPieceAt(3, 1).isValidMovement(2, 2));
		assertFalse(board.getPieceAt(3, 1).isValidMovement(4, 2));
	}

	@Test
	@DisplayName("Pawn (top) cannot move out of board")
	void testPawnMoveOutOfBoardTop() {
		board.loadPositionFromFen("K6k/3P4/8/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertFalse(board.getPieceAt(3, 1).isValidMovement(3, 8));
	}

	@Test
	@DisplayName("Pawn (top) has no legal moves when completely blocked")
	void testPawnNoLegalMovesWhenBlockedTop() {
		board.loadPositionFromFen("K6k/3P4/3p4/8/8/8/8/8 w - - 0 1");
		board.setWhiteAtBottom(false);

		assertTrue(board.getPieceAt(3, 1).getLegalMoves(board).isEmpty());
	}
}
