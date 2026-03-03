package chess;

import chess.Chess.MoveType;
import chess.Chess.Player;

import static chess.Chess.getPiece;
import static chess.Chess.isWhite;
import static chess.Chess.isBlack;
import static chess.Chess.player;
import static chess.Chess.enpassTarget;
import static chess.Chess.enpassFile;
import static chess.Chess.enpassRank;
import static chess.Chess.inBounds;
import static chess.Chess.changeBoard;
import static chess.Chess.revertBoard;
import static chess.Chess.kingInCheck;

public class PawnPiece extends ReturnPiece {

    // OPEN - starting move 2 squares
    // MOVE - standard forward move 1 square
    // CAP - forward diagonal capture 1 square
    // EP - en passant, diagonal capture behind enemy pawn that made a 2 square move in previous turn
    // PROM - promotion to selected piece if they reach opposite side of board
    public MoveType checkMove(char nextFile, int nextRank, String prmPiece) {

        char file = pieceFile.name().charAt(0);

        // if move is current position then illegal
        if(file == nextFile && pieceRank == nextRank) {
            return MoveType.NONE;
        }

        // corresponding values for each player since pawns only move forward in rank
        int startRank = (player == Player.white) ? 2 : 7;
        int openRank = (player == Player.white) ? 4 : 5;
        int legalDirection = (player == Player.white) ? 1 : -1;
        int promRank = (player == Player.white) ? 8 : 1;

        // OPEN
        if(file == nextFile && pieceRank == startRank && nextRank == openRank) {
            if(getPiece(nextFile, nextRank) == null) {
                enpassTarget = player;
                enpassFile = nextFile;
                enpassRank = nextRank;

                return MoveType.OPEN;
            }else {
                return MoveType.NONE;
            }
        }

        int deltaFile = nextFile - file;
        int deltaRank = nextRank - pieceRank;

        if(deltaRank != legalDirection || Math.abs(deltaFile) > 1) {
            return MoveType.NONE;
        }

        // if straight, check for MOVE or PROM
        // if diagonal, check for CAP or EP
        if(deltaFile == 0) {
            if(getPiece(nextFile, nextRank) != null) {
                return MoveType.NONE;
            }else {
                if(nextRank == promRank) {
                    return MoveType.PROM;
                }else {
                    return (prmPiece == null) ? MoveType.MOVE : MoveType.NONE;
                }
            }
        }else {
            if(enpassTarget != null) {
                int epDeltaFile = Math.abs(enpassFile - file);

                if(epDeltaFile == 1 && enpassRank == pieceRank && nextFile == enpassFile && getPiece(nextFile, nextRank) == null) {
                    return MoveType.EP;
                }
            }

            if(getPiece(nextFile, nextRank) == null) {
                return MoveType.NONE;
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
        }

        // should never happen
        return MoveType.NONE;

    }

    // simulates legal moves and determines king safety
    public boolean hasLegalMove(KingPiece currKing) {

        boolean hasMove = false;

        char file = pieceFile.name().charAt(0);

        int deltaRank = (player == Player.white) ? 1 : -1;

        int checkRank = pieceRank + deltaRank;

        if(inBounds(file, checkRank)) {
            
            MoveType move = checkMove(file, checkRank, null);

            if(move == MoveType.MOVE || move == MoveType.PROM) {

                char prevFile = file;
                int prevRank = pieceRank;

                ReturnPiece removed = changeBoard(this, move, file, checkRank, prevFile, prevRank, null, '\0', -1, null);

                if(!kingInCheck(currKing)) {
                    hasMove = true;
                }

                revertBoard(this, file, checkRank, prevFile, prevRank, removed, null, '\0', -1);

                if(hasMove) {
                    return hasMove;
                }

            }

        }

        checkRank += deltaRank;

        if(inBounds(file, checkRank)) {
            
            MoveType move = checkMove(file, checkRank, null);

            if(move == MoveType.OPEN) {

                char prevFile = file;
                int prevRank = pieceRank;

                ReturnPiece removed = changeBoard(this, move, file, checkRank, prevFile, prevRank, null, '\0', -1, null);

                if(!kingInCheck(currKing)) {
                    hasMove = true;
                }

                revertBoard(this, file, checkRank, prevFile, prevRank, removed, null, '\0', -1);

                if(hasMove) {
                    return hasMove;
                }

            }

        }

        checkRank -= deltaRank;

        for(int deltaFile = -1; deltaFile <= 1; deltaFile += 2) {

            char checkFile = (char) (file + deltaFile);

            if(!inBounds(checkFile, checkRank)) {
                continue;
            }

            MoveType move = checkMove(checkFile, checkRank, null);

            if(move == MoveType.CAP || move == MoveType.EP) {

                char prevFile = file;
                int prevRank = pieceRank;

                ReturnPiece removed = changeBoard(this, move, file, checkRank, prevFile, prevRank, null, '\0', -1, null);

                if(!kingInCheck(currKing)) {
                    hasMove = true;
                }

                revertBoard(this, file, checkRank, prevFile, prevRank, removed, null, '\0', -1);

                if(hasMove) {
                    return hasMove;
                }

            }

        }

        return hasMove;

    }

}
