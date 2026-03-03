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

public class RookPiece extends ReturnPiece {

    boolean hasMoved = false;

    // MOVE - standard move any # of square horizontally or vertically
    // CAP - move to an enemy position
    public MoveType checkMove(char nextFile, int nextRank) {

        char file = pieceFile.name().charAt(0);

        // if move is current position then illegal
        if(file == nextFile && pieceRank == nextRank) {
            return MoveType.NONE;
        }

        // if not on a straight vector then illegal
        if(file != nextFile && pieceRank != nextRank) {
            return MoveType.NONE;
        }

        // peform line of sight based on vector
        if(file == nextFile) {
            if(nextRank > pieceRank) {
                int checkRank = pieceRank + 1;

                while(checkRank != nextRank) {
                    if(getPiece(file, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkRank++;
                }
            }else {
                int checkRank = pieceRank - 1;

                while(checkRank != nextRank) {
                    if(getPiece(file, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkRank--;
                }
            }
        }else {
            if(nextFile > file) {
                char checkFile = (char) (file + 1);

                while(checkFile != nextFile) {
                    if(getPiece(checkFile, pieceRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile++;
                }
            }else {
                char checkFile = (char) (file - 1);

                while(checkFile != nextFile) {
                    if(getPiece(checkFile, pieceRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile--;
                }
            }
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
    public boolean hasLegalMove(KingPiece currKing) {

        boolean hasMove = false;

        char file = pieceFile.name().charAt(0);

        for(int fileDir = -1; fileDir <= 1; fileDir += 2) {

            char checkFile = (char) (file + fileDir);

            while(inBounds(checkFile, pieceRank)) {

                MoveType move = checkMove(checkFile, pieceRank);

                if(move == MoveType.NONE) {
                    break;
                }

                char prevFile = file;
                
                ReturnPiece removed = changeBoard(this, move, checkFile, pieceRank, prevFile, pieceRank, null, '\0', -1, null);

                if(!kingInCheck(currKing)) {
                    hasMove = true;
                }

                revertBoard(this, checkFile, pieceRank, prevFile, pieceRank, removed, null, '\0', -1);

                if(hasMove) {
                    return hasMove;
                }

                checkFile = (char) (checkFile + fileDir);

            }

        }

        for(int rankDir = -1; rankDir <= 1; rankDir += 2) {

            int checkRank = pieceRank + rankDir;

            while(inBounds(file, checkRank)) {

                MoveType move = checkMove(file, checkRank);

                if(move == MoveType.NONE) {
                    break;
                }

                int prevRank = pieceRank;
                
                ReturnPiece removed = changeBoard(this, move, file, checkRank, file, prevRank, null, '\0', -1, null);

                if(!kingInCheck(currKing)) {
                    hasMove = true;
                }

                revertBoard(this, file, checkRank, file, prevRank, removed, null, '\0', -1);

                if(hasMove) {
                    return hasMove;
                }

                checkRank = checkRank + rankDir;

            }

        }

        return hasMove;

    }

}
