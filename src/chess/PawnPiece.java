package chess;

import java.util.*;
import chess.Chess.MoveType;

public class PawnPiece extends ReturnPiece {

    // OPEN - starting move 2 squares
    // MOVE - standard forward move 1 square
    // CAP - forward diagonal capture 1 square
    // EP - en passant, diagonal capture behind enemy pawn that made a 2 square move in previous turn
    // PROM - promotion to selected piece if they reach opposite side of board
    public MoveType checkMove(char nextFile, int nextRank, String prmPiece) {

        // placeholder
        return null;
    }

}
