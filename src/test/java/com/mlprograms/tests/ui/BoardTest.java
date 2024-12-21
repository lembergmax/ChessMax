/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.tests.ui;

import com.mlprograms.chess.game.action.Move;
import com.mlprograms.chess.game.pieces.Pawn;
import com.mlprograms.chess.game.pieces.Piece;
import com.mlprograms.chess.game.ui.Board;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class BoardTest {

	private Board board;

	@BeforeEach
	void setUp() {
		board = new Board();
	}

	@Test
	void testInitialPieceSetup() {
		List<Piece> pieces = board.getPieceList();
		assertNotNull(pieces, "Die Liste der Figuren sollte nicht null sein.");
		assertEquals(32, pieces.size(), "Es sollten 32 Figuren auf dem Brett sein.");
	}

	@Test
	void testPieceAtPosition() {
		Piece piece = board.getPieceAt(0, 0);
		assertNotNull(piece, "Es sollte eine Figur auf der Position (0, 0) geben.");
		assertEquals("Rook", piece.getName(), "Die Figur auf der Position (0, 0) sollte ein Turm sein.");
	}

	@Test
	void testMovePiece() {
		Piece piece = board.getPieceAt(0, 1);
		assertNotNull(piece, "Es sollte eine Figur auf der Position (0, 1) geben.");
		board.makeMove(new Move(board, piece, 0, 3));
		assertNull(board.getPieceAt(0, 1), "Es sollte keine Figur mehr auf der Position (0, 1) geben.");
		assertEquals(piece, board.getPieceAt(0, 3), "Die Figur sollte sich jetzt auf der Position (0, 3) befinden.");
	}

	@Test
	void testLoadPositionFromFen() {
		String fen = "rnbqkbnr/pppppppp/8/8/8/8/PPPPPPPP/RNBQKBNR w KQkq - 0 1";
		board.loadPositionFromFen(fen);
		assertEquals(32, board.getPieceList().size(), "Es sollten 32 Figuren auf dem Brett sein.");
	}

	@Test
	void testMakeMove() {
		Piece piece = board.getPieceAt(1, 1);
		Move move = new Move(board, piece, 1, 3);
		board.makeMove(move);
		assertEquals(piece, board.getPieceAt(1, 3), "Die Figur sollte sich jetzt auf der Position (1, 3) befinden.");
	}

	@Test
	void testCapturePiece() {
		Piece whitePawn = new Pawn(board, 4, 6, true);
		Piece blackPawn = new Pawn(board, 4, 4, false);
		board.getPieceList().add(whitePawn);
		board.getPieceList().add(blackPawn);
		Move move = new Move(board, whitePawn, 4, 4);
		board.makeMove(move);
		assertNull(board.getPieceAt(4, 6), "Es sollte keine Figur mehr auf der Position (4, 6) geben.");
		assertEquals(whitePawn, board.getPieceAt(4, 4), "Die weiße Figur sollte sich jetzt auf der Position (4, 4) befinden.");
		assertFalse(board.getPieceList().contains(blackPawn), "Die schwarze Figur sollte entfernt worden sein.");
	}
}