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
    public MoveType checkMove(char nextFile, int nextRank) {

        RookPiece leftRook = (Chess.getPiece('a', 1) instanceof RookPiece) ? (RookPiece) Chess.getPiece('a', 1) : null;
        RookPiece rightRook = (Chess.getPiece('h', 1) instanceof RookPiece) ? (RookPiece) Chess.getPiece('h', 1) : null;

        char file = pieceFile.name().charAt(0);

        // if move is current position then illegal
        if(file == nextFile && pieceRank == nextRank) {
            return MoveType.NONE;
        }

        int deltaFile = nextFile - file;
        int deltaRank = nextRank - pieceRank;
        MoveType castle = null;

        if(hasMoved == false && nextRank == pieceRank && Math.abs(deltaFile) == 2) {
            System.out.println("king available");
            switch(deltaFile) {
                case 2 -> {
                    System.out.println("moving right");
                    if(rightRook != null && rightRook.hasMoved == false) {
                        System.out.println("right rook available");
                        char checkFile = (char) (file + 1);

                        while(Chess.getPiece(checkFile, pieceRank) != rightRook) {
                            if(Chess.getPiece(checkFile, pieceRank) != null) {
                                System.out.println("line of sight blocked");
                                break;
                            }
                            checkFile++;
                        }

                        if(Chess.getPiece(checkFile, pieceRank) == rightRook) {
                            castle = MoveType.CAST_KSIDE;
                            System.out.println("castle available");
                        }
                    }
                }
                case -2 -> {
                    System.out.println("moving left");
                    if(leftRook != null && leftRook.hasMoved == false) {
                        System.out.println("left rook available");
                        char checkFile = (char) (file - 1);

                        while(Chess.getPiece(checkFile, pieceRank) != leftRook) {
                            if(Chess.getPiece(checkFile, pieceRank) != null) {
                                System.out.println("line of sight blocked");
                                break;
                            }
                            checkFile--;
                        }

                        if(Chess.getPiece(checkFile, pieceRank) == leftRook) {
                            castle = MoveType.CAST_QSIDE;
                            System.out.println("castle available");
                        }
                    }
                }
            }
        }

        if(castle != null) {
            return castle;
        }

        if(deltaFile > 1 || deltaRank > 1) {
            return MoveType.NONE;
        }

        // if target spot is empty its a MOVE
        if(Chess.getPiece(nextFile, nextRank) == null) {
            return MoveType.MOVE;
        }

        // if target spot is enemy piece it is a CAP
        // if friendly piece it is illegal
        switch(Chess.player) {
            case white -> {
                return (Chess.isBlack(Chess.getPiece(nextFile, nextRank))) ? MoveType.CAP : MoveType.NONE;
            }
            case black -> {
                return (Chess.isWhite(Chess.getPiece(nextFile, nextRank))) ? MoveType.CAP : MoveType.NONE;
            }
        }

        // should never happen
        return MoveType.NONE;

    }

}
