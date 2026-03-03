package chess;

import chess.Chess.MoveType;

import static chess.Chess.getPiece;
import static chess.Chess.isWhite;
import static chess.Chess.isBlack;
import static chess.Chess.player;
import static chess.Chess.inBounds;
import static chess.Chess.changeBoard;
import static chess.Chess.revertBoard;
import static chess.Chess.kingInCheck;

public class KingPiece extends ReturnPiece {

    boolean hasMoved = false;

    // MOVE - 1 square any direction
    // CAP - move to enemy position
    // CAST - castling: 
        // can only be done if king has not moved yet, target rook has not moved yet, and no pieces in between
        // can not be currently in check
        // king moves two spaces towards target rook
        // rook jumps over king to adjacent spot
        // e.g: king moves e1 to g1, rook moves h1 to f1
    public MoveType checkMove(char nextFile, int nextRank) {

        // castling rooks
        RookPiece leftRook = null;
        RookPiece rightRook = null;

        switch(player) {
            case white -> {
                leftRook = (getPiece('a', 1) instanceof RookPiece) ? (RookPiece) getPiece('a', 1) : leftRook;
                rightRook = (getPiece('h', 1) instanceof RookPiece) ? (RookPiece) getPiece('h', 1) : rightRook;
            }
            case black -> {
                leftRook = (getPiece('a', 8) instanceof RookPiece) ? (RookPiece) getPiece('a', 8) : leftRook;
                rightRook = (getPiece('h', 8) instanceof RookPiece) ? (RookPiece) getPiece('h', 8) : rightRook;
            }
        }

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

        if(Math.abs(deltaFile) > 1 || Math.abs(deltaRank) > 1) {
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

    // simulates legal moves and determines king safety
    public boolean hasLegalMove() {

        boolean hasMove = false;

        char file = pieceFile.name().charAt(0);

        for(int fileDir = -1; fileDir <= 1; fileDir++) {
            for(int rankDir = -1; rankDir <= 1; rankDir++) {

                if(fileDir == 0 && rankDir == 0) {
                    continue;
                }

                char checkFile = (char) (file + fileDir);
                int checkRank = pieceRank + rankDir;

                if(!inBounds(checkFile, checkRank)) {
                    continue;
                }

                MoveType move = checkMove(checkFile, checkRank);

                if(move != MoveType.MOVE && move != MoveType.CAP) {
                    continue;
                }

                char prevFile = file;
                int prevRank = pieceRank;

                ReturnPiece removed = changeBoard(this, move, checkFile, checkRank, prevFile, prevRank, null, '\0', -1, null);

                if(!kingInCheck(this)) {
                    hasMove = true;
                }

                revertBoard(this, checkFile, checkRank, prevFile, prevRank, removed, null, '\0', -1);

                if(hasMove) {
                    return hasMove;
                }

            }
        }

        return hasMove;

    }

}
