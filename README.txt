DESIGN PLAN:

Game session:
- manage board with ArrayList of polymorphic ReturnPiece objects 
- track current player with Player object (supplied to us as enum)
- track special moves (en passant, etc.)

Classes:
- supplied:
    - Chess: controls the game, contains enum Player, play(), start(), and any added methods or structures
    - PlayChess (DO NOT SUBMIT): driver class, contains main(), printBoard(), makeBlankBoard(), printPiecesOnBoard()
    - ReturnPiece (DO NOT MODIFY/SUBMIT): contains pieceFile, pieceRank, pieceType, toString(), and equals()
    - ReturnPlay (DO NOT MODIFY/SUBMIT): contains piecesOnBoard and message
- to be added:
    - PawnPiece
    - RookPiece
    - KnightPiece
    - BishopPiece
    - QueenPiece
    - KingPiece
    - Each piece will extend ReturnPiece and have the following:
        - static enum MoveType (will contain corresponding moves for that piece)
        - checkMove() (will check if the requested move is legal, and if yes then return MoveType value)

start():
- Chess class
- set starting player to white
- clear the board ArrayList
- Nested loop; outer loop iterates 4 times for each starting rank; inner loop iterates through all enum values for the starting files
- call addPiece function for each position

addPiece():
- Chess class
- takes int and PieceFile from the loop as arguments
- use switch case to determine correct pieceType, pieceFile, and pieceRank for new ReturnPiece (polymorphic); add it to board ArrayList

play() (in progress):
- Chess class
- we can assume move will be properly formatted aside from leading or trailing whitespace, which can be removed with trim()
- potential flow for each play:
    - check for special move format (castling, promotion, etc.)
    - check if move is legal for that piece, and determine move type (move, capture, special move)
    - perform move: update piece position, remove any captured pieces from the board ArrayList
    - return ReturnPlay (may return earlier if illegal move or resign)

checkMove() (in progress):
- one for each ReturnPiece subclass
- PawnPiece:
    - OPEN:
        - must be in starting rank
        - target file is the same
        - target rank is current+2 or current-2 depending on color
        - target position is empty
    - MOVE:
        - target file is the same
        - target rank is current+1 or current-1 depending on color
        - target position is empty
    - CAP:
        - target file is current+1 or current-1
        - target rank is current+1 or current-1 depending on color
        - target position is occupied by enemy piece
    - EP: 
        - global variables to track if en passant is possible, and which pawn is eligible for capture
        - enemy must have made a 2 square pawn move on last turn
        - our pawn is in rank 4 or 5 depending on color
        - eligible pawn is either one space to left or right
        - target position is one square behind eligible pawn, and empty
    - PROM:
        - must be in rank 2 or 7 depending on color
        - target rank is 1 or 8 depending on color
        - promotion piece is specified
- RookPiece:
    - 
- KnightPiece:
    - 
- BishopPiece:
    - 
- QueenPiece:
    - 
- KingPiece:
    - 


IDEAS/COMMENTS: