/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Queen;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class QueenTest {

	private Board board;
	private Queen whiteQueen;
	private Queen blackQueen;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
		board.getPieceList().clear();
		whiteQueen = new Queen(board, 3, 0, true);
		blackQueen = new Queen(board, 3, 7, false);
		board.getPieceList().add(whiteQueen);
		board.getPieceList().add(blackQueen);
	}

	@Test
	void testWhiteQueenValidMoveVertical() {
		assertTrue(whiteQueen.isValidMovement(3, 5));
	}

	@Test
	void testWhiteQueenValidMoveHorizontal() {
		assertTrue(whiteQueen.isValidMovement(5, 0));
	}

	@Test
	void testWhiteQueenValidMoveDiagonal() {
		assertTrue(whiteQueen.isValidMovement(5, 2));
	}

	@Test
	void testWhiteQueenInvalidMove() {
		assertFalse(whiteQueen.isValidMovement(4, 2));
	}

	@Test
	void testBlackQueenValidMoveVertical() {
		assertTrue(blackQueen.isValidMovement(3, 2));
	}

	@Test
	void testBlackQueenValidMoveHorizontal() {
		assertTrue(blackQueen.isValidMovement(5, 7));
	}

	@Test
	void testBlackQueenValidMoveDiagonal() {
		assertTrue(blackQueen.isValidMovement(5, 5));
	}

	@Test
	void testBlackQueenInvalidMove() {
		assertFalse(blackQueen.isValidMovement(4, 5));
	}

	@Test
	void testMoveToSamePosition() {
		assertFalse(whiteQueen.isValidMovement(whiteQueen.getColumn(), whiteQueen.getRow()));
	}

	@Test
	void testMoveOutOfBounds() {
		assertFalse(whiteQueen.isValidMovement(8, 8));
	}

	@Test
	void testCaptureOpponentPiece() {
		Queen blackQueenToCapture = new Queen(board, 5, 2, false);
		board.getPieceList().add(blackQueenToCapture);
		assertTrue(whiteQueen.isValidMovement(5, 2));
	}

	@Test
	void testCannotCaptureOwnPiece() {
		Queen whiteQueenToBlock = new Queen(board, 5, 2, true);
		board.getPieceList().add(whiteQueenToBlock);
		assertFalse(whiteQueen.isValidMovement(5, 2));
	}

	@Test
	void testMoveBlockedByPiece() {
		Queen blockingQueen = new Queen(board, 4, 1, true);
		board.getPieceList().add(blockingQueen);
		assertFalse(whiteQueen.isValidMovement(5, 2));
	}

	@Test
	void testMoveNotBlockedByPiece() {
		assertTrue(whiteQueen.isValidMovement(5, 2));
	}
}