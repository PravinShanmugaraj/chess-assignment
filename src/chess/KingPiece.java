package chess;

import chess.Chess.MoveType;

import static chess.Chess.getPiece;
import static chess.Chess.isWhite;
import static chess.Chess.isBlack;
import static chess.Chess.player;

public class KingPiece extends ReturnPiece {

    boolean hasMoved = false;

    // MOVE - 1 square any direction
    // CAP - move to enemy position
    // CAST - castling: 
        // can only be done if king has not moved yet, target rook has not moved yet, and no pieces in between
        // king moves two spaces towards target rook
        // rook jumps over king to adjacent spot
        // e.g: king moves e1 to g1, rook moves h1 to f1
    public MoveType checkMove(char nextFile, int nextRank) {

        // castling rooks
        RookPiece leftRook = (getPiece('a', 1) instanceof RookPiece) ? (RookPiece) getPiece('a', 1) : null;
        RookPiece rightRook = (getPiece('h', 1) instanceof RookPiece) ? (RookPiece) getPiece('h', 1) : null;

        char file = pieceFile.name().charAt(0);

        // if move is current position then illegal
        if(file == nextFile && pieceRank == nextRank) {
            return MoveType.NONE;
        }

        int deltaFile = nextFile - file;
        int deltaRank = nextRank - pieceRank;
        MoveType castle = null;

        // castling check
        if(hasMoved == false && nextRank == pieceRank && Math.abs(deltaFile) == 2) {
            switch(deltaFile) {
                case 2 -> {
                    if(rightRook != null && rightRook.hasMoved == false) {
                        char checkFile = (char) (file + 1);

                        while(getPiece(checkFile, pieceRank) != rightRook) {
                            if(getPiece(checkFile, pieceRank) != null) {
                                break;
                            }
                            checkFile++;
                        }

                        if(getPiece(checkFile, pieceRank) == rightRook) {
                            castle = MoveType.CAST_KSIDE;
                        }
                    }
                }
                case -2 -> {
                    if(leftRook != null && leftRook.hasMoved == false) {
                        char checkFile = (char) (file - 1);

                        while(getPiece(checkFile, pieceRank) != leftRook) {
                            if(getPiece(checkFile, pieceRank) != null) {
                                break;
                            }
                            checkFile--;
                        }

                        if(getPiece(checkFile, pieceRank) == leftRook) {
                            castle = MoveType.CAST_QSIDE;
                        }
                    }
                }
            }
        }

        if(castle != null) {
            return castle;
        }

        if(deltaFile > 1 || deltaRank > 1) {
            return MoveType.NONE;
        }

        // if target spot is empty its a MOVE
        if(getPiece(nextFile, nextRank) == null) {
            return MoveType.MOVE;
        }

        // if target spot is enemy piece it is a CAP
        // if friendly piece it is illegal
        switch(player) {
            case white -> {
                return (isBlack(getPiece(nextFile, nextRank))) ? MoveType.CAP : MoveType.NONE;
            }
            case black -> {
                return (isWhite(getPiece(nextFile, nextRank))) ? MoveType.CAP : MoveType.NONE;
            }
        }

        // should never happen
        return MoveType.NONE;

    }

}
