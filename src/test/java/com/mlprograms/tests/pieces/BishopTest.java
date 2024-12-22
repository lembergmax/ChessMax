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
		board.getPieceList().clear();
		whiteBishop = new Bishop(board, 2, 0, true);
		blackBishop = new Bishop(board, 2, 7, false);
		board.getPieceList().add(whiteBishop);
		board.getPieceList().add(blackBishop);
	}

	@Test
	void testWhiteBishopValidMoveDiagonalUpRight() {
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testWhiteBishopValidMoveDiagonalUpLeft() {
		assertTrue(whiteBishop.isValidMovement(0, 2));
	}

	@Test
	void testWhiteBishopValidMoveDiagonalDownRight() {
		whiteBishop.setPosition(4, 4);
		assertTrue(whiteBishop.isValidMovement(6, 6));
	}

	@Test
	void testWhiteBishopValidMoveDiagonalDownLeft() {
		whiteBishop.setPosition(4, 4);
		assertTrue(whiteBishop.isValidMovement(2, 6));
	}

	@Test
	void testWhiteBishopInvalidMoveHorizontal() {
		assertFalse(whiteBishop.isValidMovement(4, 0));
	}

	@Test
	void testWhiteBishopInvalidMoveVertical() {
		assertFalse(whiteBishop.isValidMovement(2, 2));
	}

	@Test
	void testBlackBishopValidMoveDiagonalUpRight() {
		blackBishop.setPosition(4, 4);
		assertTrue(blackBishop.isValidMovement(6, 6));
	}

	@Test
	void testBlackBishopValidMoveDiagonalUpLeft() {
		blackBishop.setPosition(4, 4);
		assertTrue(blackBishop.isValidMovement(2, 6));
	}

	@Test
	void testBlackBishopValidMoveDiagonalDownRight() {
		assertTrue(blackBishop.isValidMovement(4, 5));
	}

	@Test
	void testBlackBishopValidMoveDiagonalDownLeft() {
		assertTrue(blackBishop.isValidMovement(0, 5));
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
		Bishop blackBishopToCapture = new Bishop(board, 4, 2, false);
		board.getPieceList().add(blackBishopToCapture);
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testCannotCaptureOwnPiece() {
		Bishop whiteBishopToBlock = new Bishop(board, 4, 2, true);
		board.getPieceList().add(whiteBishopToBlock);
		assertFalse(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testMoveBlockedByPiece() {
		Bishop blockingBishop = new Bishop(board, 3, 1, true);
		board.getPieceList().add(blockingBishop);
		assertFalse(whiteBishop.isValidMovement(4, 2));
	}

	@Test
	void testMoveNotBlockedByPiece() {
		assertTrue(whiteBishop.isValidMovement(4, 2));
	}
}