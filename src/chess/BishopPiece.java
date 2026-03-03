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

public class BishopPiece extends ReturnPiece {

    // MOVE - diagonal any # of squares
    // CAP - move to enemy position
    public MoveType checkMove(char nextFile, int nextRank) {

        char file = pieceFile.name().charAt(0);

        // if target is current then illegal
        if(file == nextFile && pieceRank == nextRank) {
            return MoveType.NONE;
        }

        // change in file and rank
        double deltaFile = Math.abs(nextFile - file);
        double deltaRank = Math.abs(nextRank - pieceRank);

        // must be on diagonal vector
        if(deltaRank == 0 || deltaFile/deltaRank != 1) {
            return MoveType.NONE;
        }

        // depending on which vector the target is on, perform line of sight check
        if(nextFile > file) {
            if(nextRank > pieceRank) {
                char checkFile = (char) (file + 1);
                int checkRank = pieceRank + 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile++;
                    checkRank++;
                }
            }else {
                char checkFile = (char) (file + 1);
                int checkRank = pieceRank - 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile++;
                    checkRank--;
                }
            }
        }else {
            if(nextRank > pieceRank) {
                char checkFile = (char) (file - 1);
                int checkRank = pieceRank + 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile--;
                    checkRank++;
                }
            }else {
                char checkFile = (char) (file - 1);
                int checkRank = pieceRank - 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile--;
                    checkRank--;
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

    public boolean hasLegalMove(KingPiece currKing) {

        boolean hasMove = false;

        char file = pieceFile.name().charAt(0);

        for(int fileDir = -1; fileDir <= 1; fileDir += 2) {
            for(int rankDir = -1; rankDir <= 1; rankDir += 2) {

                char checkFile = (char) (file + fileDir);
                int checkRank = pieceRank + rankDir;

                while(inBounds(checkFile, checkRank)) {

                    MoveType move = checkMove(checkFile, checkRank);

                    if(move == MoveType.NONE) {
                        break;
                    }

                    char prevFile = file;
                    int prevRank = pieceRank;

                    ReturnPiece removed = changeBoard(this, move, checkFile, checkRank, prevFile, prevRank, null, '\0', -1, null);

                    if(!kingInCheck(currKing)) {
                        hasMove = true;
                    }

                    revertBoard(this, checkFile, checkRank, prevFile, prevRank, removed, null, '\0', -1);

                    if(hasMove) {
                        return hasMove;
                    }

                    checkFile = (char) (checkFile + fileDir);
                    checkRank = checkRank + rankDir;

                }

            }
        }

        return hasMove;

    }

}
