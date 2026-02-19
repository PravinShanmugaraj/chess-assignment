DESIGN PLAN:

Game session:
- manage board with 2D array of polymorphic ReturnPiece objects
- keep ArrayList of pieces for ReturnPlay
- track current player with Player object (supplied to us as enum)
- track special moves (en passant, etc.)

Classes:
- supplied:
    - Chess: controls the game, contains enum Player, play(), start(), and any added methods or structures
    - PlayChess (DO NOT SUBMIT): driver class, contains main(), printBoard(), makeBlankBoard(), printPiecesOnBoard()
    - ReturnPiece (DO NOT MODIFY/SUBMIT): contains pieceFile, pieceRank, pieceType, toString(), and equals()
    - ReturnPlay (DO NOT MODIFY/SUBMIT): contains piecesOnBoard and message
- added:
    - PawnPiece
    - RookPiece
    - KnightPiece
    - BishopPiece
    - QueenPiece
    - KingPiece
    - Each piece will extend ReturnPiece and have:
        - checkMove() (will check if the requested move is legal, and if yes then return MoveType value)
    - RookPiece and KingPiece will also have hasMoved() boolean for castling

start():
- Chess class
- set starting player to white
- clear board and boardList
- Nested loop to add pieces to board and boardList with addPiece()

addPiece():
- Chess class
- takes int and PieceFile from the loop as arguments
- switch case to determine correct pieceType, pieceFile, and pieceRank for new ReturnPiece (polymorphic)
- return the new ReturnPiece

play() (in progress):
- Chess class
- we can assume move will be properly formatted aside from leading or trailing whitespace, which can be removed with trim()
- flow for each play:
    -> check for resign
    -> extract current and next position values
    -> check if piece exists
    -> check if appropriate color
    -> set respective fields for special formats
    -> determine what move type is being made
    -> if not illegal, attempt to execute the move
    -> return the resulting ReturnPlay of the execution

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
    - to be added
- KnightPiece:
    - to be added
- BishopPiece:
    - to be added
- QueenPiece:
    - to be added
- KingPiece:
    - to be added

execMove() (in progress): 
- Chess class
- determine if move will result in self check/checkmate
- if legal, modify board and boardList
- determine if other player is in check/checkmate

returnPlay():
- returns a new ReturnPlay object with the boardList and given Message

isWhite()/isBlack():
- check if given piece is the respective color


IDEAS/COMMENTS: