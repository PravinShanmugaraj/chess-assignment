// Pravin Shanmugaraj, Aditya Jadhav
package chess;

import java.util.*;

import chess.ReturnPiece.PieceType;
import chess.ReturnPiece.PieceFile;
import chess.ReturnPlay.Message;

public class Chess {

    enum Player { white, black }

	// qualifications for each move are in piece subclasses
	enum MoveType {
		MOVE, CAP,					// general moves
		OPEN, EP, PROM,				// pawn specials
		CAST_KSIDE, CAST_QSIDE,		// castling
		NONE						// illegal
	}

	// current player
	static Player player;

	// list of pieces for ReturnPlay
	static ArrayList<ReturnPiece> boardList = new ArrayList<ReturnPiece>();

	// for determining check/checkmate
	static ArrayList<ReturnPiece> whiteList = new ArrayList<ReturnPiece>();
	static ArrayList<ReturnPiece> blackList = new ArrayList<ReturnPiece>();

	// session board
	// rows are 8 - rank
	// columns are file - 'a'
	static ReturnPiece[][] board;

	// constant access to kings for check and checkmate
	static ReturnPiece whiteKing = null, blackKing = null;

	// track whether an en passant is possible, and the pawn eligible for capture
	static Player enpassTarget = null;
	static char enpassFile = '\0';
	static int enpassRank = -1;

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
		
		// reset enpass availability after other player's turn
		if(enpassTarget == player) {
			enpassTarget = null;
			enpassFile = '\0';
			enpassRank = -1;
		}

		boolean draw = false;
		move = move.trim();

		if(move.equals("resign")) {
			return (player == Player.white) ? returnPlay(Message.RESIGN_BLACK_WINS) : returnPlay(Message.RESIGN_WHITE_WINS);
		}

		char currFile = move.substring(0,1).charAt(0);
		int currRank = Integer.parseInt(move.substring(1,2));

		char nextFile = move.substring(3, 4).charAt(0);
		int nextRank = Integer.parseInt(move.substring(4, 5));

		ReturnPiece currPiece = null;
		String prmPiece = null;

		// check if piece exists in position
		if(getPiece(currFile, currRank) != null) {
			currPiece = getPiece(currFile, currRank);
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

		if(prmPiece != null && !(currPiece instanceof PawnPiece)) {
			return returnPlay(Message.ILLEGAL_MOVE);
		}

		// determine move type
		MoveType toExec = checkMove(currPiece, nextFile, nextRank, prmPiece);

		if(toExec == MoveType.NONE) {
			return returnPlay(Message.ILLEGAL_MOVE);
		}

		Message result = execMove(currPiece, toExec, nextFile, nextRank, prmPiece);

		// draw
		if(draw && result != Message.CHECKMATE_WHITE_WINS && result != Message.CHECKMATE_BLACK_WINS) {
			result = Message.DRAW;
		}

		if(result != Message.ILLEGAL_MOVE) {
			player = (player == Player.white) ? Player.black : Player.white;
		}

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
		whiteList.clear();
		blackList.clear();
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
				ReturnPiece toAdd = addPiece(pf, rank);
				boardList.add(toAdd);

				char file = pf.name().charAt(0);
				board[8 - rank][file - 'a'] = toAdd;

				if(isWhite(toAdd)) {
					whiteList.add(toAdd);
				}else {
					blackList.add(toAdd);
				}

				// maintain constant access to King pieces
				if(toAdd instanceof KingPiece && isWhite(toAdd)) {
					whiteKing = toAdd;
				}else if(toAdd instanceof KingPiece && isBlack(toAdd)) {
					blackKing = toAdd;
				}
			}
		}
	}

	// execute a move on a given piece, unless it puts current player in check or checkmate
	private static Message execMove(ReturnPiece currPiece, MoveType toExec, char nextFile, int nextRank, String prmPiece) {

		// for check/checkmate
		KingPiece currKing = (player == Player.white) ? (KingPiece) whiteKing : (KingPiece) blackKing;
		KingPiece oppKing = (player == Player.white) ? (KingPiece) blackKing : (KingPiece) whiteKing;

		// store several values if need to revert:
		// last position of current piece
		char prevFile = currPiece.pieceFile.name().charAt(0);
		int prevRank = currPiece.pieceRank;

		// removed piece if CAP or EP
		ReturnPiece removed = null;

		// rook if CAST
		RookPiece castleRook = null;
		char prevRookFile = '\0';
		int prevRookRank = -1;

		if(toExec == MoveType.CAST_KSIDE || toExec == MoveType.CAST_QSIDE) {
			castleRook = (toExec == MoveType.CAST_KSIDE) ? (RookPiece) getPiece('h', nextRank) : (RookPiece) getPiece('a', nextRank);
			prevRookFile = castleRook.pieceFile.name().charAt(0);
			prevRookRank = castleRook.pieceRank;
		}

		removed = changeBoard(currPiece, toExec, nextFile, nextRank, prevFile, prevRank, castleRook, prevRookFile, prevRookRank, prmPiece);

		// check if move puts player in self check/checkmate
		if(kingInCheck(currKing)) {
			revertBoard(currPiece, nextFile, nextRank, prevFile, prevRank, removed, castleRook, prevRookFile, prevRookRank);
			return Message.ILLEGAL_MOVE;
		}

		// track castling eligibility
		if(currPiece instanceof KingPiece) {
			((KingPiece) currPiece).hasMoved = true;
		}else if(currPiece instanceof RookPiece) {
			((RookPiece) currPiece).hasMoved = true;
		}

		// check if opponent is in check/checkmate
		return kingStatus(oppKing);
	}

	// revert move in case of illegal move
	static void revertBoard(ReturnPiece currPiece, char currFile, int currRank, char prevFile, int prevRank, ReturnPiece removed,
		RookPiece castleRook, char prevRookFile, int prevRookRank) {

		ReturnPiece promoted = getPiece(currFile, currRank);

		char currRookFile = (castleRook != null) ? castleRook.pieceFile.name().charAt(0) : '\0';
		int currRookRank = (castleRook != null) ? castleRook.pieceRank : -1;

		board[8 - prevRank][prevFile - 'a'] = currPiece;
		board[8 - currRank][currFile - 'a'] = null;

		currPiece.pieceFile = PieceFile.valueOf(String.valueOf(prevFile));
		currPiece.pieceRank = prevRank;

		if(removed != null) {
			board[8 - currRank][currFile - 'a'] = removed;
			boardList.add(removed);
			
			if(isWhite(removed)) {
				whiteList.add(removed);
			}else {
				blackList.add(removed);
			}
		}

		if(castleRook != null) {
			board[8 - prevRookRank][prevRookFile - 'a'] = castleRook;
			board[8 - currRookRank][currRookFile - 'a'] = null;
			castleRook.pieceFile = PieceFile.valueOf(String.valueOf(prevRookFile));
		}

		if(promoted != currPiece) {
			boardList.remove(promoted);
			boardList.add(currPiece);
		}

	}

	// make changes to board and boardList
	static ReturnPiece changeBoard(ReturnPiece currPiece, MoveType toExec, char nextFile, int nextRank, char prevFile, int prevRank,
		RookPiece castleRook, char prevRookFile, int prevRookRank, String prmPiece) {

		ReturnPiece removed = null;

		// remove captured piece
		if(toExec == MoveType.CAP) {
			removed = removePiece(nextFile, nextRank);
		}else if(toExec == MoveType.EP) {
			switch(player) {
				case white -> {
					removed = removePiece(nextFile, nextRank - 1);
				}
				case black -> {
					removed = removePiece(nextFile, nextRank + 1);
				}
			}
		}

		// move
		board[8 - nextRank][nextFile - 'a'] = currPiece;
		board[8 - prevRank][prevFile - 'a'] = null;

		currPiece.pieceFile = PieceFile.valueOf(String.valueOf(nextFile));
		currPiece.pieceRank = nextRank;

		// move castling rook
		if(toExec == MoveType.CAST_KSIDE) {
			board[8 - prevRookRank][(prevRookFile - 2) - 'a'] = castleRook;
			board[8 - prevRookRank][prevRookFile - 'a'] = null;
			castleRook.pieceFile = PieceFile.valueOf(String.valueOf((char)(prevRookFile - 2)));
		}else if(toExec == MoveType.CAST_QSIDE){
			board[8 - prevRookRank][(prevRookFile + 3) - 'a'] = castleRook;
			board[8 - prevRookRank][prevRookFile - 'a'] = null;
			castleRook.pieceFile = PieceFile.valueOf(String.valueOf((char)(prevRookFile + 3)));
		}

		// promotion
		if(toExec == MoveType.PROM) {
			prmPiece = (prmPiece == null) ? "Q" : prmPiece;
			promPiece(currPiece, nextFile, nextRank, prmPiece);
		}

		return removed;

	}

	// determine if given king is under attack
	private static Message kingStatus(KingPiece currKing) {

		if(!kingInCheck(currKing)) {
			return null;
		}

		if(canDefend(currKing)) {
			return Message.CHECK;
		}

		return (currKing == whiteKing) ? Message.CHECKMATE_BLACK_WINS : Message.CHECKMATE_WHITE_WINS;

	}

	// current king under attack
	static boolean kingInCheck(KingPiece currKing) {

		// simulate as attacking player
		Player real = player;
		player = (currKing == whiteKing) ? Player.black : Player.white;
		ArrayList<ReturnPiece> oppList = (currKing == whiteKing) ? blackList : whiteList;

		char kingFile = currKing.pieceFile.name().charAt(0);
		int kingRank = currKing.pieceRank;

		for(ReturnPiece rp : oppList) {
			if(checkMove(rp, kingFile, kingRank, null) == MoveType.CAP) {
				player = real;
				return true;
			}
		}

		player = real;
		return false;

	}

	// player in check can make a move to get out of check
	private static boolean canDefend(KingPiece currKing) {

		ArrayList<ReturnPiece> currList = (currKing == whiteKing) ? whiteList : blackList;
		ArrayList<ReturnPiece> stableCopy = new ArrayList<ReturnPiece>();

		for(ReturnPiece rp : currList) {
			stableCopy.add(rp);
		}

		// simulate as defending player
		Player real = player;
		player = (currKing == whiteKing) ? Player.white : Player.black;

		for(ReturnPiece rp : stableCopy) {
			if(hasLegalMove(rp, currKing)) {
				player = real;
				return true;
			}
		}

		player = real;

		return false;

	}

	// call appropriate hasLegalMove for piece type
	private static boolean hasLegalMove(ReturnPiece currPiece, KingPiece currKing) {

		switch(currPiece) {
			case PawnPiece checkPawn -> {
				return checkPawn.hasLegalMove(currKing);
			}
			case RookPiece checkRook -> {
				return checkRook.hasLegalMove(currKing);
			}
			case KnightPiece checkKnight -> {
				return checkKnight.hasLegalMove(currKing);
			}
			case BishopPiece checkBishop -> {
				return checkBishop.hasLegalMove(currKing);
			}
			case QueenPiece checkQueen -> {
				return checkQueen.hasLegalMove(currKing);
			}
			case KingPiece checkKing -> {
				return checkKing.hasLegalMove();
			}
			// should never happen
			default -> {
				return false;
			}
		}

	}

	// call appropriate checkMove for piece type
	private static MoveType checkMove(ReturnPiece currPiece, char nextFile, int nextRank, String prmPiece) {

		switch(currPiece) {
			case PawnPiece checkPawn -> {
				return checkPawn.checkMove(nextFile, nextRank, prmPiece);
			}
			case RookPiece checkRook -> {
				return checkRook.checkMove(nextFile, nextRank);
			}
			case KnightPiece checkKnight -> {
				return checkKnight.checkMove(nextFile, nextRank);
			}
			case BishopPiece checkBishop -> {
				return checkBishop.checkMove(nextFile, nextRank);
			}
			case QueenPiece checkQueen -> {
				return checkQueen.checkMove(nextFile, nextRank);
			}
			case KingPiece checkKing -> {
				return checkKing.checkMove(nextFile, nextRank);
			}
			// should never happen
			default -> {
				return MoveType.NONE;
			}
		}

	}

	// promote pawn to given piece
	private static void promPiece(ReturnPiece currPawn, char pFile, int pRank, String prmPiece) {

		// create new piece of correct type and replace pawn
		switch(prmPiece) {
			case "Q" -> {
				QueenPiece newQ = new QueenPiece();

				newQ.pieceType = (player == Player.white) ? PieceType.WQ : PieceType.BQ;
				newQ.pieceFile = currPawn.pieceFile;
				newQ.pieceRank = currPawn.pieceRank;

				board[8 - pRank][pFile - 'a'] = newQ;
				boardList.remove(currPawn);
				boardList.add(newQ);
			}
			case "R" -> {
				RookPiece newR = new RookPiece();

				newR.pieceType = (player == Player.white) ? PieceType.WR : PieceType.BR;
				newR.pieceFile = currPawn.pieceFile;
				newR.pieceRank = currPawn.pieceRank;

				board[8 - pRank][pFile - 'a'] = newR;
				boardList.remove(currPawn);
				boardList.add(newR);
			}
			case "B" -> {
				BishopPiece newB = new BishopPiece();

				newB.pieceType = (player == Player.white) ? PieceType.WB : PieceType.BB;
				newB.pieceFile = currPawn.pieceFile;
				newB.pieceRank = currPawn.pieceRank;

				board[8 - pRank][pFile - 'a'] = newB;
				boardList.remove(currPawn);
				boardList.add(newB);
			}
			case "N" -> {
				KnightPiece newN = new KnightPiece();

				newN.pieceType = (player == Player.white) ? PieceType.WN : PieceType.BN;
				newN.pieceFile = currPawn.pieceFile;
				newN.pieceRank = currPawn.pieceRank;

				board[8 - pRank][pFile - 'a'] = newN;
				boardList.remove(currPawn);
				boardList.add(newN);
			}
		}

	}

	// create ReturnPlay for turn
	private static ReturnPlay returnPlay(Message msg) {
		
		ReturnPlay rpl = new ReturnPlay();

		rpl.piecesOnBoard = boardList;
		rpl.message = msg;

		return rpl;

	}

	// check if given piece is white
	static boolean isWhite(ReturnPiece piece) {
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
	static boolean isBlack(ReturnPiece piece) {
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

	// quality of life
	static ReturnPiece getPiece(char file, int rank) {

		return board[8 - rank][file - 'a'];

	}

	// remove a piece from board, boardList, and whiteList/blackList
	private static ReturnPiece removePiece(char file, int rank) {
		ReturnPiece removed = getPiece(file, rank);
		board[8 - rank][file - 'a'] = null;
		boardList.remove(removed);

		if(isWhite(removed)) {
			whiteList.remove(removed);
		}else {
			blackList.remove(removed);
		}

		return removed;
	}

	// check if position is within board
	static boolean inBounds(char file, int rank) {

		if(file < 'a' || file > 'h' || rank < 1 || rank > 8) {
			return false;
		}

		return true;

	}

	// creates a piece to be added based on its rank and file
	private static ReturnPiece addPiece(PieceFile pf, int rank) {

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
			// should never happen
			default -> {
				return null;
			}
		}
	}
}
