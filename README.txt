DESIGN PLAN:

Game session:
- manage board with ArrayList of polymorphic ReturnPiece objects 
- track current player with Player object (supplied to us as enum)

Classes:
- supplied:
    - ReturnPiece: contains pieceFile, pieceRank, pieceType, toString(), and equals()
    - ReturnPlay: contains piecesOnBoard and message
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
    - RookPiece and KingPiece will both have a hasMoved boolean for castling

Chess.start():
- set starting player to white
- clear the board ArrayList
- Nested loop; outer loop iterates 4 times for each starting rank; inner loop iterates through all enum values for the starting files
- call addPiece function for each position

Chess.addPiece():
- takes int and PieceFile from the loop as arguments
- use switch case to determine correct pieceType, pieceFile, and pieceRank for new ReturnPiece (polymorphic); add it to board ArrayList

Chess.play() (in progress):
- we can assume move will be properly formatted aside from leading or trailing whitespace, which can be removed with trim()
- potential flow for each play:
    - check for special move format (castling, promotion, etc.)
    - check if move is legal for that piece, and determine move type (move, capture, special move)
    - perform move: update piece position, remove any captured pieces from the board ArrayList
    - return ReturnPlay (may return earlier if illegal move or resign)
    

IDEAS/COMMENTS: