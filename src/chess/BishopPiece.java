package chess;

import java.util.*;
import chess.Chess.MoveType;

public class BishopPiece extends ReturnPiece {

    // MOVE - diagonal any # of squares
    // CAP - move to enemy position
    public MoveType checkMove(char nextFile, int nextRank) {

        char file = pieceFile.name().charAt(0);

        return checkDiagonal(file, pieceRank, nextFile, nextRank);
    }

    // static method to check for diagonal movement
    // used by BishopPiece and QueenPiece
    public static MoveType checkDiagonal(char file, int rank, char nextFile, int nextRank) {
        // if target is current then illegal
        if(file == nextFile && rank == nextRank) {
            return MoveType.NONE;
        }

        // change in file and rank
        double deltaFile = Math.abs(nextFile - file);
        double deltaRank = Math.abs(nextRank - rank);

        // must be on diagonal vector
        if(deltaRank == 0 || deltaFile/deltaRank != 1) {
            return MoveType.NONE;
        }

        // depending on which vector the target is on, perform line of sight check
        if(nextFile > file) {
            if(nextRank > rank) {
                char checkFile = (char) (file + 1);
                int checkRank = rank + 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(Chess.getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile++;
                    checkRank++;
                }
            }else {
                char checkFile = (char) (file + 1);
                int checkRank = rank - 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(Chess.getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile++;
                    checkRank--;
                }
            }
        }else {
            if(nextRank > rank) {
                char checkFile = (char) (file - 1);
                int checkRank = rank + 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(Chess.getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile--;
                    checkRank++;
                }
            }else {
                char checkFile = (char) (file - 1);
                int checkRank = rank - 1;

                while(checkFile != nextFile && checkRank != nextRank) {
                    if(Chess.getPiece(checkFile, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile--;
                    checkRank--;
                }
            }
        }

        // if target spot is empty its a MOVE
        if(Chess.getPiece(nextFile, nextRank) == null) {
            return MoveType.MOVE;
        }

        // if target spot is enemy piece it is a CAP
        // if friendly piece it is illegal
        switch(Chess.player) {
            case white -> {
                return (Chess.isBlack(Chess.getPiece(nextFile, nextRank))) ? MoveType.CAP : MoveType.NONE;
            }
            case black -> {
                return (Chess.isWhite(Chess.getPiece(nextFile, nextRank))) ? MoveType.CAP : MoveType.NONE;
            }
        }

        // should never happen
        return MoveType.NONE;
    }

}
