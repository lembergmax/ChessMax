package com.mlprograms.tests.ui;

import com.mlprograms.chess.game.action.Move;
import com.mlprograms.chess.game.ui.Board;
import com.mlprograms.chess.game.pieces.Piece;
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
	void testInvalidMove() {
		Piece piece = board.getPieceAt(0, 1);
		assertNotNull(piece, "Es sollte eine Figur auf der Position (0, 1) geben.");
		board.makeMove(new Move(board, piece, 0, 4));
		assertEquals(piece, board.getPieceAt(0, 1), "Die Figur sollte sich immer noch auf der Position (0, 1) befinden.");
		assertNull(board.getPieceAt(0, 4), "Es sollte keine Figur auf der Position (0, 4) geben.");
	}

	@Test
	void testCapturePiece() {
		Piece whitePawn = board.getPieceAt(0, 1);
		Piece blackPawn = board.getPieceAt(0, 6);
		assertNotNull(whitePawn, "Es sollte eine weiße Figur auf der Position (0, 1) geben.");
		assertNotNull(blackPawn, "Es sollte eine schwarze Figur auf der Position (0, 6) geben.");
		board.makeMove(new Move(board, whitePawn, 0, 3));
		board.makeMove(new Move(board, blackPawn, 0, 4));
		board.makeMove(new Move(board, whitePawn, 0, 4));
		assertEquals(whitePawn, board.getPieceAt(0, 4), "Die weiße Figur sollte sich jetzt auf der Position (0, 4) befinden.");
		assertNull(board.getPieceAt(0, 3), "Es sollte keine Figur mehr auf der Position (0, 3) geben.");
		assertNull(board.getPieceAt(0, 6), "Es sollte keine Figur mehr auf der Position (0, 6) geben.");
	}
}