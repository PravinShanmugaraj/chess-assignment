package chess;

import java.util.*;

import chess.ReturnPiece.PieceType;
import chess.ReturnPiece.PieceFile;
import chess.ReturnPlay.Message;

public class Chess {

    enum Player { white, black }

	// qualifications for each move are in piece subclasses
	enum MoveType {
		OPEN, PMOVE, PCAP, EP, PROM,	// pawn
		RMOVE, RCAP,					// rook
		NMOVE, NCAP,					// knight
		BMOVE, BCAP,					// bishop
		QMOVE, QCAP,					// queen
		KMOVE, KCAP, CAST,				// king
		NONE							// illegal move
	}

	// current player
	static Player player;

	// list of pieces for ReturnPlay
	static ArrayList<ReturnPiece> boardList = new ArrayList<ReturnPiece>();

	// session board
	// rows are rank - 1
	// columns are file - 'a'
	static ReturnPiece[][] board;

	// track whether an en passant is possible, and the pawn eligible for capture
	static boolean enpass;
	static PieceFile enpassFile;
	static int enpassRank;

	// constants
	static final int STD_LENGTH = 5;
	static final int PRM_LENGTH = 7;
	static final int DRW_LENGTH = 11;

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
		
		boolean draw = false;
		move = move.trim();

		if(move.equals("resign")) {
			return (player == Player.white) ? returnPlay(Message.RESIGN_WHITE_WINS) : returnPlay(Message.RESIGN_BLACK_WINS);
		}

		char currFile = move.substring(0,1).charAt(0);
		int currRank = Integer.parseInt(move.substring(1,2));

		char nextFile = move.substring(3, 4).charAt(0);
		int nextRank = move.length() == STD_LENGTH ? Integer.parseInt(move.substring(4)) : Integer.parseInt(move.substring(4, 5));

		ReturnPiece currPiece = null;
		String prmPiece = null;

		// check if piece exists in position
		if(board[currRank - 1][currFile - 'a'] != null) {
			currPiece = board[currRank - 1][currFile - 'a'];
		}else {
			return returnPlay(Message.ILLEGAL_MOVE);
		}

		// check for appropriate color
		switch(player) {
			case Player.white -> {
				if(!isWhite(currPiece)) {
					return returnPlay(Message.ILLEGAL_MOVE);
				}
			}
			case Player.black -> {
				if(!isBlack(currPiece)) {
					return returnPlay(Message.ILLEGAL_MOVE);
				}
			}
		}

		// special formats
		switch(move.length()) {
			case PRM_LENGTH -> prmPiece = move.substring(6);
			case DRW_LENGTH -> draw = true;
		}

		// determine move type
		MoveType toExec = switch(currPiece) {
			case PawnPiece currPawn -> currPawn.checkMove(nextFile, nextRank, prmPiece);
			case RookPiece currRook -> currRook.checkMove(nextFile, nextRank);
			case KnightPiece currKnight -> currKnight.checkMove(nextFile, nextRank);
			case BishopPiece currBishop -> currBishop.checkMove(nextFile, nextRank);
			case QueenPiece currQueen -> currQueen.checkMove(nextFile, nextRank);
			case KingPiece currKing -> currKing.checkMove(nextFile, nextRank);
			// should never happen
			default -> MoveType.NONE;
		};

		if(toExec == MoveType.NONE) {
			return returnPlay(Message.ILLEGAL_MOVE);
		}

		Message result = execMove(currPiece, toExec, nextFile, nextRank);

		return returnPlay(result);

	}
	
	
	/**
	 * This method should reset the game, and start from scratch.
	 */
	public static void start() {

		// first player is always white
		player = Player.white;

		//reset board
		boardList.clear();
		board = new ReturnPiece[8][8];

		// create and add pieces to starting board
		for(int i = 0; i < 4; i++) {

			int rank = switch(i) {
				case 0 -> 1;
				case 1 -> 2;
				case 2 -> 7;
				case 3 -> 8;
				// should never happen
				default -> -1;
			};

			// fill board and boardList
			for(PieceFile pf : PieceFile.values()) {
				ReturnPiece toAdd = addPiece(rank, pf);
				boardList.add(toAdd);

				char file = pf.name().charAt(0);
				board[rank - 1][file - 'a'] = toAdd;
			}
		}
	}

	// execute a move on a given piece, unless it puts current player in check or checkmate
	private static Message execMove(ReturnPiece currPiece, MoveType toExec, char nextFile, int nextRank) {

		// placeholder
		return null;
	}

	// create ReturnPlay for turn
	private static ReturnPlay returnPlay(Message msg) {
		
		ReturnPlay rpl = new ReturnPlay();

		rpl.piecesOnBoard = boardList;
		rpl.message = msg;

		return rpl;

	}

	// check if given piece is white
	private static boolean isWhite(ReturnPiece piece) {
		if(piece.pieceType == PieceType.WB ||
			piece.pieceType == PieceType.WR ||
			piece.pieceType == PieceType.WN ||
			piece.pieceType == PieceType.WP ||
			piece.pieceType == PieceType.WQ ||
			piece.pieceType == PieceType.WK) {
				return true;
		} else {
			return false;
		}
	}

	// check if given piece is black
	private static boolean isBlack(ReturnPiece piece) {
		if(piece.pieceType == PieceType.BB ||
			piece.pieceType == PieceType.BR ||
			piece.pieceType == PieceType.BN ||
			piece.pieceType == PieceType.BP ||
			piece.pieceType == PieceType.BQ ||
			piece.pieceType == PieceType.BK) {
				return true;
		} else {
			return false;
		}
	}

	// creates a piece to be added based on its rank and file
	private static ReturnPiece addPiece(int rank, PieceFile pf) {

		ReturnPiece rp = null;

		// create each piece
		switch(rank) {
			case 1 -> {

				// creates appropriate piece based on starting position
				switch(pf) {
					case PieceFile.a, PieceFile.h -> {
						rp = new RookPiece();
						rp.pieceType = PieceType.WR;
					}
					case PieceFile.b, PieceFile.g -> {
						rp = new KnightPiece();
						rp.pieceType = PieceType.WN;
					}
					case PieceFile.c, PieceFile.f -> {
						rp = new BishopPiece();
						rp.pieceType = PieceType.WB;
					}
					case PieceFile.d -> {
						rp = new QueenPiece();
						rp.pieceType = PieceType.WQ;
					}
					case PieceFile.e -> {
						rp = new KingPiece();
						rp.pieceType = PieceType.WK;
					}
				}

				rp.pieceRank = rank;
				rp.pieceFile = pf;

				return rp;
			}
			case 2 -> {

				// all pieces in this row are pawns
				rp = new PawnPiece();
				rp.pieceType = PieceType.WP;

				rp.pieceRank = rank;
				rp.pieceFile = pf;

				return rp;
			}
			case 7 -> {

				// all pieces in the row are pawns
				rp = new PawnPiece();
				rp.pieceType = PieceType.BP;

				rp.pieceRank = rank;
				rp.pieceFile = pf;

				return rp;
			}
			case 8 -> {

				// creates appropriate piece based on starting position
				switch(pf) {
					case PieceFile.a, PieceFile.h -> {
						rp = new RookPiece();
						rp.pieceType = PieceType.BR;
					}
					case PieceFile.b, PieceFile.g -> {
						rp = new KnightPiece();
						rp.pieceType = PieceType.BN;
					}
					case PieceFile.c, PieceFile.f -> {
						rp = new BishopPiece();
						rp.pieceType = PieceType.BB;
					}
					case PieceFile.d -> {
						rp = new QueenPiece();
						rp.pieceType = PieceType.BQ;
					}
					case PieceFile.e -> {
						rp = new KingPiece();
						rp.pieceType = PieceType.BK;
					}
				}

				rp.pieceRank = rank;
				rp.pieceFile = pf;

				return rp;
			}
			default -> {
				return null;
			}
		}
	}
}
