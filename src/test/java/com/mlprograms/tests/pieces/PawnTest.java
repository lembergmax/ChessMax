/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.pieces;

import com.mlprograms.chess.game.pieces.Pawn;
import com.mlprograms.chess.game.ui.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PawnTest {

	private Board board;
	private Pawn whitePawn;
	private Pawn blackPawn;

	@BeforeEach
	void setUp() {
		board = new Board();
		whitePawn = new Pawn(board, 4, 6, true); // Weiße Bauern starten in der 6. Reihe
		blackPawn = new Pawn(board, 4, 1, false); // Schwarze Bauern starten in der 1. Reihe
		board.getPieceList().add(whitePawn);
		board.getPieceList().add(blackPawn);
	}

	@Test
	void testWhitePawnSingleMoveForward() {
		assertTrue(whitePawn.isValidMovement(4, 5));
	}

	@Test
	void testWhitePawnDoubleMoveForward() {
		assertTrue(whitePawn.isValidMovement(4, 4));
	}

	@Test
	void testWhitePawnInvalidMove() {
		assertFalse(whitePawn.isValidMovement(4, 3));
	}

	@Test
	void testBlackPawnSingleMoveForward() {
		assertTrue(blackPawn.isValidMovement(4, 2));
	}

	@Test
	void testBlackPawnDoubleMoveForward() {
		assertTrue(blackPawn.isValidMovement(4, 3));
	}

	@Test
	void testBlackPawnInvalidMove() {
		assertFalse(blackPawn.isValidMovement(4, 4));
	}

	@Test
	void testWhitePawnCaptureLeft() {
		Pawn blackPawnToCapture = new Pawn(board, 3, 5, false);
		board.getPieceList().add(blackPawnToCapture);
		assertTrue(whitePawn.isValidMovement(3, 5));
	}

	@Test
	void testWhitePawnCaptureRight() {
		Pawn blackPawnToCapture = new Pawn(board, 5, 5, false);
		board.getPieceList().add(blackPawnToCapture);
		assertTrue(whitePawn.isValidMovement(5, 5));
	}

	@Test
	void testBlackPawnCaptureLeft() {
		Pawn whitePawnToCapture = new Pawn(board, 3, 2, true);
		board.getPieceList().add(whitePawnToCapture);
		assertTrue(blackPawn.isValidMovement(3, 2));
	}

	@Test
	void testBlackPawnCaptureRight() {
		Pawn whitePawnToCapture = new Pawn(board, 5, 2, true);
		board.getPieceList().add(whitePawnToCapture);
		assertTrue(blackPawn.isValidMovement(5, 2));
	}

	@Test
	void testWhitePawnEnPassantLeft() {
		board.setEnPassantTile(board.getTileNumber(3, 5));
		assertTrue(whitePawn.isValidMovement(3, 5));
	}

	@Test
	void testWhitePawnEnPassantRight() {
		board.setEnPassantTile(board.getTileNumber(5, 5));
		assertTrue(whitePawn.isValidMovement(5, 5));
	}

	@Test
	void testBlackPawnEnPassantLeft() {
		board.setEnPassantTile(board.getTileNumber(3, 2));
		assertTrue(blackPawn.isValidMovement(3, 2));
	}

	@Test
	void testBlackPawnEnPassantRight() {
		board.setEnPassantTile(board.getTileNumber(5, 2));
		assertTrue(blackPawn.isValidMovement(5, 2));
	}
}