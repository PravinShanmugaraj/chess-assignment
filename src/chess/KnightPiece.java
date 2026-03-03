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

    // simulates legal moves and determines king safety
    public boolean hasLegalMove(KingPiece currKing) {

        boolean hasMove = false;

        char file = pieceFile.name().charAt(0);

        for(int fileDir = -2; fileDir <= 2; fileDir++) {
            for(int rankDir = -2; rankDir <= 2; rankDir++) {

                if(Math.abs(fileDir * rankDir) != 2) {
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

                if(!kingInCheck(currKing)) {
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
