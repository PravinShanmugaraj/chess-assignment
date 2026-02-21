package chess;

import java.util.*;
import chess.Chess.MoveType;

public class KingPiece extends ReturnPiece {

    boolean hasMoved = false;

    // MOVE - 1 square any direction
    // CAP - move to enemy position
    // CAST - castling: 
        // can only be done if king has not moved yet, target rook has not moved yet, and no pieces in between
        // king moves two spaces towards target rook
        // rook jumps over king to adjacent spot
        // e.g: king moves e1 to g1, rook moves h1 to f1
    public MoveType checkMove(char nextFile, int nextRank, RookPiece castleRook) {

        // placeholder
        return null;
    }

}
