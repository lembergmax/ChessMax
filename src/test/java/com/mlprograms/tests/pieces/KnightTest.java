/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Knight;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import com.mlprograms.chess.utils.Logger;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class KnightTest {

	private Board board;
	private Knight whiteKnight;
	private Knight blackKnight;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
		whiteKnight = new Knight(board, 1, 7, true);
		blackKnight = new Knight(board, 1, 0, false);
		board.getPieceList().add(whiteKnight);
		board.getPieceList().add(blackKnight);
	}

	@Test
	void testWhiteKnightValidMove() {
		assertTrue(whiteKnight.isValidMovement(2, 5));
	}

	@Test
	void testWhiteKnightInvalidMove() {
		assertFalse(whiteKnight.isValidMovement(1, 5));
	}

	@Test
	void testBlackKnightValidMove() {
		assertTrue(blackKnight.isValidMovement(2, 2));
	}

	@Test
	void testBlackKnightInvalidMove() {
		assertFalse(blackKnight.isValidMovement(1, 2));
	}

	@Test
	void testMoveToSamePosition() {
		assertFalse(whiteKnight.isValidMovement(whiteKnight.getColumn(), whiteKnight.getRow()));
	}

	@Test
	void testMoveOutOfBounds() {
		assertFalse(whiteKnight.isValidMovement(8, 8));
	}

	@Test
	void testCaptureOpponentPiece() {
		Knight blackKnightToCapture = new Knight(board, 2, 5, false);
		board.getPieceList().add(blackKnightToCapture);
		assertTrue(whiteKnight.isValidMovement(2, 5));
	}

	@Test
	void testCannotCaptureOwnPiece() {
		Knight whiteKnightToBlock = new Knight(board, 2, 5, true);
		board.getPieceList().add(whiteKnightToBlock);
		assertFalse(whiteKnight.isValidMovement(2, 5));
	}

	@Test
	void testMoveLShapeUpRight() {
		assertTrue(whiteKnight.isValidMovement(2, 5));
	}

	@Test
	void testMoveLShapeUpLeft() {
		assertTrue(whiteKnight.isValidMovement(0, 5));
	}

	@Test
	void testMoveLShapeDownRight() {
		assertTrue(blackKnight.isValidMovement(2, 2));
	}

	@Test
	void testMoveLShapeDownLeft() {
		assertTrue(blackKnight.isValidMovement(0, 2));
	}

	@Test
	void testMoveLShapeRightUp() {
		assertTrue(whiteKnight.isValidMovement(0, 5));
	}

	@Test
	void testMoveLShapeRightDown() {
		blackKnight.setPosition(3, 1);
		assertTrue(blackKnight.isValidMovement(5, 2));
	}

	@Test
	void testMoveLShapeLeftUp() {
		assertTrue(whiteKnight.isValidMovement(0, 5));
	}

	@Test
	void testMoveLShapeLeftDown() {
		blackKnight.setPosition(3, 1);
		assertTrue(blackKnight.isValidMovement(1, 2));
	}

	@Test
	void testMoveBlockedByPiece() {
		Knight blockingKnight = new Knight(board, 2, 5, true);
		board.getPieceList().add(blockingKnight);
		assertFalse(whiteKnight.isValidMovement(2, 5));
	}

	@Test
	void testMoveNotBlockedByPiece() {
		assertTrue(whiteKnight.isValidMovement(2, 5));
	}
}