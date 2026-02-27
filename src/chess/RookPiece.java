package chess;

import java.util.*;
import chess.Chess.MoveType;

public class RookPiece extends ReturnPiece {

    boolean hasMoved = false;

    // MOVE - standard move any # of square horizontally or vertically
    // CAP - move to an enemy position
    public MoveType checkMove(char nextFile, int nextRank) {

        char file = pieceFile.name().charAt(0);

        return checkStraight(file, pieceRank, nextFile, nextRank);

    }

    // static method to check for straight movement
    // used by RookPiece and QueenPiece
    public static MoveType checkStraight(char file, int rank, char nextFile, int nextRank) {
        // if move is current position then illegal
        if(file == nextFile && rank == nextRank) {
            return MoveType.NONE;
        }

        // if not on a straight vector then illegal
        if(file != nextFile && rank != nextRank) {
            return MoveType.NONE;
        }

        // peform line of sight based on vector
        if(file == nextFile) {
            if(nextRank > rank) {
                int checkRank = rank + 1;

                while(checkRank != nextRank) {
                    if(Chess.getPiece(file, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkRank++;
                }
            }else {
                int checkRank = rank - 1;

                while(checkRank != nextRank) {
                    if(Chess.getPiece(file, checkRank) != null) {
                        return MoveType.NONE;
                    }
                    checkRank--;
                }
            }
        }else {
            if(nextFile > file) {
                char checkFile = (char) (file + 1);

                while(checkFile != nextFile) {
                    if(Chess.getPiece(checkFile, rank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile++;
                }
            }else {
                char checkFile = (char) (file - 1);

                while(checkFile != nextFile) {
                    if(Chess.getPiece(checkFile, rank) != null) {
                        return MoveType.NONE;
                    }
                    checkFile--;
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
