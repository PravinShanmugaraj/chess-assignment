package chess;

import chess.Chess.MoveType;
import static chess.Chess.inBounds;
import static chess.Chess.changeBoard;
import static chess.Chess.revertBoard;
import static chess.Chess.kingInCheck;

public class QueenPiece extends ReturnPiece {

    // MOVE - any number of squares on any vector
    // CAP - move to enemy position
    public MoveType checkMove(char nextFile, int nextRank) {

        RookPiece straight = new RookPiece();
        BishopPiece diagonal = new BishopPiece();

        straight.pieceFile = this.pieceFile;
        straight.pieceRank = this.pieceRank;
        diagonal.pieceFile = this.pieceFile;
        diagonal.pieceRank = this.pieceRank;

        MoveType isDiagonal = diagonal.checkMove(nextFile, nextRank);

        if(isDiagonal != MoveType.NONE) {
            return isDiagonal;
        }
        
        return straight.checkMove(nextFile, nextRank);
    }

    // simulates legal moves and determines king safety
    public boolean hasLegalMove(KingPiece currKing) {
        
        boolean hasMove = false;

        char file = pieceFile.name().charAt(0);

        for(int fileDir = -1; fileDir <= 1; fileDir++) {
            for(int rankDir = -1; rankDir <= 1; rankDir++) {

                if(fileDir == 0 && rankDir == 0) {
                    continue;
                }

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
