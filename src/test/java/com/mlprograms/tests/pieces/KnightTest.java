/*
 * Copyright (c) 2025 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Knight;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

	private Board board;
	private Knight whiteKnight;
	private Knight blackKnight;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
		// Clear any existing pieces from the board.
		board.getPieceList().clear();
		// Place a white knight at (1, 0) and a black knight at (1, 7).
		whiteKnight = new Knight(board, 1, 0, true);
		blackKnight = new Knight(board, 1, 7, false);
		board.getPieceList().add(whiteKnight);
		board.getPieceList().add(blackKnight);
	}

	// ========================= White Knight Tests =========================

	@Test
	@DisplayName("White Knight valid move: L-shaped move to (3,1)")
	void testWhiteKnightValidMove_3_1() {
		// From (1,0) -> (3,1) is a valid knight move.
		assertTrue(whiteKnight.isValidMovement(3, 1));
	}

	@Test
	@DisplayName("White Knight valid move: L-shaped move to (2,2)")
	void testWhiteKnightValidMove_2_2() {
		// From (1,0) -> (2,2) is valid.
		assertTrue(whiteKnight.isValidMovement(2, 2));
	}

	@Test
	@DisplayName("White Knight valid move: L-shaped move to (0,2)")
	void testWhiteKnightValidMove_0_2() {
		// From (1,0) -> (0,2) is valid.
		assertTrue(whiteKnight.isValidMovement(0, 2));
	}

	@Test
	@DisplayName("White Knight invalid move: non L-shaped move")
	void testWhiteKnightInvalidNonLShapeMove() {
		// Moving from (1,0) to (1,2) is not an L-shaped move.
		assertFalse(whiteKnight.isValidMovement(1, 2));
	}

	@Test
	@DisplayName("White Knight move out of board is invalid")
	void testWhiteKnightMoveOutOfBoard() {
		// Attempt to move to a square outside the board boundaries.
		assertFalse(whiteKnight.isValidMovement(-1, -1));
	}

	@Test
	@DisplayName("White Knight cannot move to the same position")
	void testWhiteKnightMoveToSamePosition() {
		// Moving to its current position should be invalid.
		assertFalse(whiteKnight.isValidMovement(whiteKnight.getColumn(), whiteKnight.getRow()));
	}

	@Test
	@DisplayName("White Knight can jump over pieces")
	void testWhiteKnightJumpOverPieces() {
		// Place a friendly piece (dummy knight) in an intermediate square.
		Knight blockingPiece = new Knight(board, 1, 1, true);
		board.getPieceList().add(blockingPiece);
		// Knight's move from (1,0) to (2,2) should be valid even if the path is obstructed.
		assertTrue(whiteKnight.isValidMovement(2, 2));
	}

	@Test
	@DisplayName("White Knight capturing opponent piece is allowed")
	void testWhiteKnightCaptureOpponent() {
		// Place an enemy knight at a valid destination (2,2).
		Knight enemyKnight = new Knight(board, 2, 2, false);
		board.getPieceList().add(enemyKnight);
		// Capturing the enemy piece by moving to (2,2) is allowed.
		assertTrue(whiteKnight.isValidMovement(2, 2));
	}

	@Test
	@DisplayName("White Knight cannot capture its own piece")
	void testWhiteKnightCannotCaptureOwnPiece() {
		// Place a friendly knight at a valid destination (2,2).
		Knight friendlyKnight = new Knight(board, 2, 2, true);
		board.getPieceList().add(friendlyKnight);
		// The knight should not be allowed to move to a square occupied by a friendly piece.
		assertFalse(whiteKnight.isValidMovement(2, 2));
	}

	@Test
	@DisplayName("White Knight legal moves from center on an empty board")
	void testWhiteKnightLegalMovesFromCenterEmptyBoard() {
		// Clear the board and place the white knight in the center at (4,4).
		board.getPieceList().clear();
		whiteKnight.setPosition(4, 4);
		board.getPieceList().add(whiteKnight);
		// Expected legal moves for a knight from (4,4) are:
		// (6,5), (6,3), (5,6), (5,2), (3,6), (3,2), (2,5), (2,3) => 8 moves.
		assertEquals(8, whiteKnight.getLegalMoves(board).size());
	}

	// ========================= Black Knight Tests =========================

	@Test
	@DisplayName("Black Knight valid move: L-shaped move to (3,6)")
	void testBlackKnightValidMove_3_6() {
		// From (1,7) -> (3,6) is a valid knight move.
		assertTrue(blackKnight.isValidMovement(3, 6));
	}

	@Test
	@DisplayName("Black Knight valid move: L-shaped move to (2,5)")
	void testBlackKnightValidMove_2_5() {
		// From (1,7) -> (2,5) is valid.
		assertTrue(blackKnight.isValidMovement(2, 5));
	}

	@Test
	@DisplayName("Black Knight valid move: L-shaped move to (0,5)")
	void testBlackKnightValidMove_0_5() {
		// From (1,7) -> (0,5) is valid.
		assertTrue(blackKnight.isValidMovement(0, 5));
	}

	@Test
	@DisplayName("Black Knight invalid move: non L-shaped move")
	void testBlackKnightInvalidNonLShapeMove() {
		// Moving from (1,7) to (1,5) is not an L-shaped move.
		assertFalse(blackKnight.isValidMovement(1, 5));
	}

	@Test
	@DisplayName("Black Knight move out of board is invalid")
	void testBlackKnightMoveOutOfBoard() {
		// Attempt to move to a square outside the board boundaries.
		assertFalse(blackKnight.isValidMovement(8, 8));
	}

	@Test
	@DisplayName("Black Knight cannot move to the same position")
	void testBlackKnightMoveToSamePosition() {
		// Moving to its current position should be invalid.
		assertFalse(blackKnight.isValidMovement(blackKnight.getColumn(), blackKnight.getRow()));
	}

	@Test
	@DisplayName("Black Knight can jump over pieces")
	void testBlackKnightJumpOverPieces() {
		// Place a friendly piece (dummy knight) in an intermediate square.
		Knight blockingPiece = new Knight(board, 1, 6, false);
		board.getPieceList().add(blockingPiece);
		// Knight's move from (1,7) to (2,5) should be valid despite the obstruction.
		assertTrue(blackKnight.isValidMovement(2, 5));
	}

	@Test
	@DisplayName("Black Knight capturing opponent piece is allowed")
	void testBlackKnightCaptureOpponent() {
		// Place an enemy knight at a valid destination (2,5).
		Knight enemyKnight = new Knight(board, 2, 5, true);
		board.getPieceList().add(enemyKnight);
		// Capturing the enemy piece by moving to (2,5) is allowed.
		assertTrue(blackKnight.isValidMovement(2, 5));
	}

	@Test
	@DisplayName("Black Knight cannot capture its own piece")
	void testBlackKnightCannotCaptureOwnPiece() {
		// Place a friendly knight at a valid destination (2,5).
		Knight friendlyKnight = new Knight(board, 2, 5, false);
		board.getPieceList().add(friendlyKnight);
		// The knight should not be allowed to move to a square occupied by a friendly piece.
		assertFalse(blackKnight.isValidMovement(2, 5));
	}

	@Test
	@DisplayName("Black Knight legal moves from center on an empty board")
	void testBlackKnightLegalMovesFromCenterEmptyBoard() {
		// Clear the board and place the black knight in the center at (4,4).
		board.getPieceList().clear();
		blackKnight.setPosition(4, 4);
		board.getPieceList().add(blackKnight);
		// Expected legal moves for a knight from (4,4) are:
		// (6,5), (6,3), (5,6), (5,2), (3,6), (3,2), (2,5), (2,3) => 8 moves.
		assertEquals(8, blackKnight.getLegalMoves(board).size());
	}
}
