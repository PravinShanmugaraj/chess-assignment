package chess;

import java.util.*;

public class KnightPiece extends ReturnPiece {

    // MOVE - L shape in any direction
    // CAP - move to enemy position
    // (can jump over any piece)
    static enum MoveType {MOVE, CAP};

    public MoveType checkMove(ReturnPiece.PieceFile f, int r) {

        // placeholder
        return null;
    }

}
