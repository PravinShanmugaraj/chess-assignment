package chess;

import java.util.*;

public class PawnPiece extends ReturnPiece {

    // OPEN - starting move 2 squares
    // MOVE - standard forward move 1 square
    // CAP - forward diagonal capture 1 square
    // EP - en passant, diagonal capture behind enemy pawn that made a 2 square move in previous turn
    // PROM - promotion to selected piece if they reach opposite side of board
    static enum MoveType {OPEN, MOVE, CAP, EP, PROM};

    public MoveType checkMove(ReturnPiece.PieceFile f, int r) {

        // placeholder
        return null;
    }

}
