/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Rook;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class RookTest {

	private Board board;
	private Rook whiteRook;
	private Rook blackRook;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
		board.getPieceList().clear();
		whiteRook = new Rook(board, 0, 0, true);
		blackRook = new Rook(board, 0, 7, false);
		board.getPieceList().add(whiteRook);
		board.getPieceList().add(blackRook);
	}

	@Test
	void testWhiteRookValidMoveVertical() {
		assertTrue(whiteRook.isValidMovement(0, 5));
	}

	@Test
	void testWhiteRookValidMoveHorizontal() {
		assertTrue(whiteRook.isValidMovement(5, 0));
	}

	@Test
	void testWhiteRookInvalidMoveDiagonal() {
		assertFalse(whiteRook.isValidMovement(5, 5));
	}

	@Test
	void testWhiteRookInvalidMoveLShape() {
		assertFalse(whiteRook.isValidMovement(1, 2));
	}

	@Test
	void testBlackRookValidMoveVertical() {
		assertTrue(blackRook.isValidMovement(0, 2));
	}

	@Test
	void testBlackRookValidMoveHorizontal() {
		assertTrue(blackRook.isValidMovement(5, 7));
	}

	@Test
	void testBlackRookInvalidMoveDiagonal() {
		assertFalse(blackRook.isValidMovement(5, 2));
	}

	@Test
	void testBlackRookInvalidMoveLShape() {
		assertFalse(blackRook.isValidMovement(1, 5));
	}

	@Test
	void testMoveToSamePosition() {
		assertFalse(whiteRook.isValidMovement(whiteRook.getColumn(), whiteRook.getRow()));
	}

	@Test
	void testMoveOutOfBounds() {
		assertFalse(whiteRook.isValidMovement(8, 8));
	}

	@Test
	void testCaptureOpponentPiece() {
		Rook blackRookToCapture = new Rook(board, 0, 5, false);
		board.getPieceList().add(blackRookToCapture);
		assertTrue(whiteRook.isValidMovement(0, 5));
	}

	@Test
	void testCannotCaptureOwnPiece() {
		Rook whiteRookToBlock = new Rook(board, 0, 5, true);
		board.getPieceList().add(whiteRookToBlock);
		assertFalse(whiteRook.isValidMovement(0, 5));
	}

	@Test
	void testMoveBlockedByPiece() {
		Rook blockingRook = new Rook(board, 0, 3, true);
		board.getPieceList().add(blockingRook);
		assertFalse(whiteRook.isValidMovement(0, 5));
	}

	@Test
	void testMoveNotBlockedByPiece() {
		assertTrue(whiteRook.isValidMovement(0, 5));
	}
}