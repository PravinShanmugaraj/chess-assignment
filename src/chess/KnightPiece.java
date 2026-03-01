package chess;

import chess.Chess.MoveType;

import static chess.Chess.getPiece;
import static chess.Chess.isWhite;
import static chess.Chess.isBlack;
import static chess.Chess.player;

public class KnightPiece extends ReturnPiece {

    // MOVE - L shape in any direction
    // CAP - move to enemy position
    // (can jump over any piece)
    public MoveType checkMove(char nextFile, int nextRank) {

        char file = pieceFile.name().charAt(0);

        int deltaFile = Math.abs(nextFile - file);
        int deltaRank = Math.abs(nextRank - pieceRank);

        // legal positions for knight to move
        if(!(deltaFile == 1 && deltaRank == 2) &&
            !(deltaFile == 2 && deltaRank == 1)) {
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
