package chess;

import java.util.*;

public class Chess {

    enum Player { white, black }

	// track current player
	static Player player;

	// track pieces on board
	static ArrayList<ReturnPiece> board = new ArrayList<ReturnPiece>();

	// track whether an en passant is possible, and the pawn eligible for capture
	static boolean enpass;
	static ReturnPiece.PieceFile enpassFile;
	static int enpassRank;

	/**
	 * Plays the next move for whichever player has the turn.
	 * 
	 * @param move String for next move, e.g. "a2 a3"
	 * 
	 * @return A ReturnPlay instance that contains the result of the move.
	 *         See the section "The Chess class" in the assignment description for details of
	 *         the contents of the returned ReturnPlay instance.
	 */
	public static ReturnPlay play(String move) {
		
		// NOT FINAL, TESTING PIECE MOVEMENT
		ReturnPlay rpl = new ReturnPlay();
		
		move = move.trim();
		String currPos = move.substring(0, 2);
		String nextPos = move.substring(3);

		for(ReturnPiece rp : board) {
			if(currPos.equals(rp.toString().substring(0, 2))) {
				rp.pieceFile = ReturnPiece.PieceFile.valueOf(nextPos.substring(0, 1));
				rp.pieceRank = Integer.parseInt(nextPos.substring(1));
				break;
			}
		}

		rpl.piecesOnBoard = board;
		rpl.message = null;

		return rpl;

	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {

		// first player is always white
		player = Player.white;

		//reset board
		board.clear();

		// create and add pieces to starting board
		for(int i = 0; i < 4; i++) {
			for(ReturnPiece.PieceFile pf : ReturnPiece.PieceFile.values()) {
				board.add(addPiece(i, pf));
			}
		}
	}

	// creates a piece to be added based on its rank and file
	private static ReturnPiece addPiece(int i, ReturnPiece.PieceFile pf) {

		// create ReturnPiece object
		ReturnPiece rp;

		// i corresponds to a rank: 0 -> 1, 1 -> 2, 2 -> 7, 3 -> 8
		switch(i) {
			case 0 -> {

				// creates appropriate piece based on starting position
				if(pf == ReturnPiece.PieceFile.a || pf == ReturnPiece.PieceFile.h) {
					rp = new RookPiece();
					rp.pieceType = ReturnPiece.PieceType.WR;
				}else if(pf == ReturnPiece.PieceFile.b || pf == ReturnPiece.PieceFile.g) {
					rp = new KnightPiece();
					rp.pieceType = ReturnPiece.PieceType.WN;
				}else if(pf == ReturnPiece.PieceFile.c || pf == ReturnPiece.PieceFile.f) {
					rp = new BishopPiece();
					rp.pieceType = ReturnPiece.PieceType.WB;
				}else if(pf == ReturnPiece.PieceFile.d) {
					rp = new QueenPiece();
					rp.pieceType = ReturnPiece.PieceType.WQ;
				}else {
					rp = new KingPiece();
					rp.pieceType = ReturnPiece.PieceType.WK;
				}

				rp.pieceRank = 1;
				rp.pieceFile = pf;

				return rp;
			}
			case 1 -> {

				// all pieces in this row are pawns
				rp = new PawnPiece();
				rp.pieceType = ReturnPiece.PieceType.WP;

				rp.pieceRank = 2;
				rp.pieceFile = pf;

				return rp;
			}
			case 2 -> {

				// all pieces in the row are pawns
				rp = new PawnPiece();
				rp.pieceType = ReturnPiece.PieceType.BP;

				rp.pieceRank = 7;
				rp.pieceFile = pf;

				return rp;
			}
			case 3 -> {

				// creates appropriate piece based on starting position
				if(pf == ReturnPiece.PieceFile.a || pf == ReturnPiece.PieceFile.h) {
					rp = new RookPiece();
					rp.pieceType = ReturnPiece.PieceType.BR;
				}else if(pf == ReturnPiece.PieceFile.b || pf == ReturnPiece.PieceFile.g) {
					rp = new KnightPiece();
					rp.pieceType = ReturnPiece.PieceType.BN;
				}else if(pf == ReturnPiece.PieceFile.c || pf == ReturnPiece.PieceFile.f) {
					rp = new BishopPiece();
					rp.pieceType = ReturnPiece.PieceType.BB;
				}else if(pf == ReturnPiece.PieceFile.d) {
					rp = new QueenPiece();
					rp.pieceType = ReturnPiece.PieceType.BQ;
				}else {
					rp = new KingPiece();
					rp.pieceType = ReturnPiece.PieceType.BK;
				}

				rp.pieceRank = 8;
				rp.pieceFile = pf;

				return rp;
			}
			default -> {
				return null;
			}
		}
	}
}
