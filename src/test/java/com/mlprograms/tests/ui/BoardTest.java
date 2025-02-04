/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.ui;

import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.human.Human;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

	private Board board;

	@BeforeEach
	void setUp() {
		board = new Board(new Human(), new Human(), true);
	}

	@Test
	void testInitialPieceSetup() {
		List<Piece> pieces = board.getPieceList();
		assertNotNull(pieces);
		assertEquals(32, pieces.size());
	}

	@Test
	void testPieceAtPosition() {
		Piece piece = board.getPieceAt(0, 0);
		assertNotNull(piece);
		assertEquals("Rook", piece.getName());
	}

	@Test
	void testLoadPositionFromFen() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		board.loadPositionFromFen(fen);

		assertEquals(32, board.getPieceList().size());
	}

}