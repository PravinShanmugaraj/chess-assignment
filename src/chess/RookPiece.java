package chess;

import java.util.*;

public class RookPiece extends ReturnPiece {

    // MOVE - standard move any # of square horizontally or vertically
    // CAP - move to an enemy position
    static enum MoveType {MOVE, CAP};
    
    // for castling
    boolean hasMoved = false;

    public MoveType checkMove(ReturnPiece.PieceFile f, int r) {

        // placeholder
        return null;
    }

}
