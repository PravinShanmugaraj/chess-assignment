package chess;

import chess.Chess.MoveType;

import static chess.BishopPiece.checkDiagonal;
import static chess.RookPiece.checkStraight;

public class QueenPiece extends ReturnPiece {

    // MOVE - any number of squares on any vector
    // CAP - move to enemy position
    public MoveType checkMove(char nextFile, int nextRank) {

        char file = pieceFile.name().charAt(0);

        MoveType diagonal = checkDiagonal(file, pieceRank, nextFile, nextRank);

        if(diagonal != MoveType.NONE) {
            return diagonal;
        }
        
        return checkStraight(file, pieceRank, nextFile, nextRank);
    }

}
