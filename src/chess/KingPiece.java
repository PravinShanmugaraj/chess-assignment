package chess;

import java.util.*;

public class KingPiece extends ReturnPiece {

    // MOVE - 1 square any direction
    // CAP - move to enemy position
    // CAST - castling: 
        // can only be done if king has not moved yet, target rook has not moved yet, and no pieces in between
        // king moves two spaces towards target rook
        // rook jumps over king to adjacent spot
        // e.g: king moves e1 to g1, rook moves h1 to f1
    static enum MoveType {MOVE, CAP, CAST};

    public MoveType checkMove(ReturnPiece.PieceFile f, int r) {

        // placeholder
        return null;
    }

}
