package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;

    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(
            ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case KING:
                int [][] kingMoves = {{-1,1}, {0,1}, {1,1}, {-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}};
                addJumpMoves(board, myPosition, moves, kingMoves);
                break;

            case QUEEN:
                int [][] queenMoves = {{-1,1}, {0,1}, {1,1}, {-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}};
                addSlidingMoves(board, myPosition, moves, queenMoves);
                break;

            case BISHOP:
                int [][] bishopMoves = {{-1,1}, {1,1}, {-1,-1}, {1,-1}};
                addSlidingMoves(board, myPosition, moves, bishopMoves);
                break;

            case PieceType.KNIGHT:
                int [][] knightMoves = {{-2,1}, {-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}};
                addJumpMoves(board, myPosition, moves, knightMoves);

                break;
            case ROOK:
                int [][] rookMoves = {{0,1}, {-1,0}, {1,0}, {0,-1}};
                addSlidingMoves(board, myPosition, moves, rookMoves);
                break;

            case PAWN:
                int direction;
                if (pieceColor == ChessGame.TeamColor.WHITE){
                    direction = 1;
                }
                else{
                    direction = -1;
                }
                int row = myPosition.getRow();
                int col = myPosition.getColumn();

                // move forward one
                ChessPosition newPosition = new ChessPosition(row + direction, col);
                if (board.getPiece(newPosition) == null) {
                    // promotion
                    addPawnMove(myPosition,newPosition, moves);
                    // move forward two
                    if (pieceColor == ChessGame.TeamColor.WHITE && row == 2 || pieceColor == ChessGame.TeamColor.BLACK && row == 7) {
                        newPosition = new ChessPosition(row + direction * 2, col);
                        if (board.getPiece(newPosition) == null) {
                            moves.add(new ChessMove(myPosition, newPosition,null));
                        }
                    }
                }
                // Capture left and right
                int[] pawnColumns = {col - 1, col + 1};

                for (int newCol : pawnColumns) {
                    if (row + direction >= 1 && row + direction <= 8 && newCol >= 1 && newCol <= 8) {
                         newPosition = new ChessPosition(row + direction, newCol);
                        if (board.getPiece(newPosition) != null && board.getPiece(newPosition).getTeamColor() != pieceColor) {
                            // promotion
                            addPawnMove(myPosition, newPosition, moves);
                        }
                    }
                }
                break;
        }
        return moves;
    }

    private void addJumpMoves(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int[][] directions) {

        for (int[] move : directions) {
            int row = myPosition.getRow() + move[0];
            int col = myPosition.getColumn() + move[1];

            if (row < 1 || row > 8 || col < 1 || col > 8) {
                continue;
            }

            ChessPosition newPosition = new ChessPosition(row, col);
            ChessPiece target = board.getPiece(newPosition);

            if (target == null || target.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(myPosition, newPosition, null));
            }
        }
    }

    private void addSlidingMoves(
            ChessBoard board,
            ChessPosition myPosition,
            Collection<ChessMove> moves,
            int[][] directions) {

        for (int[] move : directions) {
            int row = myPosition.getRow();
            int col = myPosition.getColumn();

            while (true) {
                row += move[0];
                col += move[1];

                if (row < 1 || row > 8 || col < 1 || col > 8) {
                    break;
                }

                ChessPosition newPosition = new ChessPosition(row, col);
                ChessPiece target = board.getPiece(newPosition);

                if (target == null) {
                    moves.add(new ChessMove(myPosition, newPosition, null));
                } else {
                    if (target.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, newPosition, null));
                    }
                    break;
                }
            }
        }
    }

    private void addPawnMove(
            ChessPosition myPosition,
            ChessPosition newPosition,
            Collection<ChessMove> moves) {

        if (newPosition.getRow() == 8 || newPosition.getRow() == 1) {
            moves.add(new ChessMove(myPosition, newPosition, PieceType.QUEEN));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.ROOK));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.BISHOP));
            moves.add(new ChessMove(myPosition, newPosition, PieceType.KNIGHT));
        } else {
            moves.add(new ChessMove(myPosition, newPosition, null));
        }
    }

}
