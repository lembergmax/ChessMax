/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Bishop;
import com.mlprograms.chess.game.ui.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BishopTest {

	private Board board;
	private Bishop whiteBishop;
	private Bishop blackBishop;

	@BeforeEach
	void setUp() {
		board = new Board();
		whiteBishop = new Bishop(board, 2, 0, true);
		blackBishop = new Bishop(board, 2, 7, false);
		board.getPieceList().add(whiteBishop);
		board.getPieceList().add(blackBishop);
	}

	@Test
	void testWhiteBishopValidMove() {
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testWhiteBishopInvalidMove() {
		assertFalse(whiteBishop.isValidMovement(4, 3));
	}

	@Test
	void testBlackBishopValidMove() {
		assertTrue(blackBishop.isValidMovement(4, 5));
	}

	@Test
	void testBlackBishopInvalidMove() {
		assertFalse(blackBishop.isValidMovement(4, 4));
	}

	@Test
	void testMoveCollidesWithPiece() {
		Bishop blockingBishop = new Bishop(board, 3, 1, true);
		board.getPieceList().add(blockingBishop);
		assertTrue(whiteBishop.moveCollidesWithPiece(4, 2));
	}

	@Test
	void testMoveDoesNotCollideWithPiece() {
		// Set up the board and pieces
		board.getPieceList().clear();
		board.getPieceList().add(whiteBishop);
		board.getPieceList().add(blackBishop);

		// Test that the move does not collide with any piece
		assertFalse(whiteBishop.moveCollidesWithPiece(5, 3));
	}

	@Test
	void testMoveToSamePosition() {
		assertFalse(whiteBishop.isValidMovement(whiteBishop.getColumn(), whiteBishop.getRow()));
	}

	@Test
	void testMoveOutOfBounds() {
		assertFalse(whiteBishop.isValidMovement(8, 8));
	}

	@Test
	void testCaptureOpponentPiece() {
		board.getPieceList().clear();
		Bishop blackBishopToCapture = new Bishop(board, 4, 2, false);
		board.getPieceList().add(blackBishopToCapture);
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testCannotCaptureOwnPiece() {
		board.getPieceList().clear();
		Bishop whiteBishopToBlock = new Bishop(board, 4, 2, true);
		board.getPieceList().add(whiteBishopToBlock);
		assertFalse(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testMoveDiagonallyUpRight() {
		assertTrue(whiteBishop.isValidMovement(5, 3));
	}

	@Test
	void testMoveDiagonallyUpLeft() {
		assertTrue(whiteBishop.isValidMovement(0, 2));
	}

	@Test
	void testMoveDiagonallyDownRight() {
		assertTrue(blackBishop.isValidMovement(4, 5));
	}

	@Test
	void testMoveDiagonallyDownLeft() {
		assertTrue(blackBishop.isValidMovement(0, 5));
	}

	@Test
	void testMoveBlockedByPiece() {
		board.getPieceList().clear();
		Bishop blockingBishop = new Bishop(board, 3, 3, true);
		board.getPieceList().add(blockingBishop);
		assertFalse(whiteBishop.isValidMovement(5, 5));
	}

	@Test
	void testMoveNotBlockedByPiece() {
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}
}