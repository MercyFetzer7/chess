package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
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
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (type) {
            case KING:
                int [][] kingMoves = {{-1,1}, {0,1}, {1,1}, {-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}};

                for (int[] move : kingMoves) {
                    int row = myPosition.getRow() + move[0];
                    int col = myPosition.getColumn() + move[1];

                    if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                        ChessPosition newPosition = new ChessPosition(row, col);

                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
                break;

            case QUEEN:
                int [][] queenMoves = {{-1,1}, {0,1}, {1,1}, {-1,0}, {1,0}, {-1,-1}, {0,-1}, {1,-1}};

                for (int[] move : queenMoves) {
                    int row = myPosition.getRow();
                    int col = myPosition.getColumn();

                    while (true) {
                        row += move[0];
                        col += move[1];

                        if (row < 1 || row > 8 || col < 1 || col > 8) {
                            break;
                        }

                        ChessPosition newPosition = new ChessPosition(row, col);

                        if (board.getPiece(newPosition) == null) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                        else {
                            if (board.getPiece(newPosition).getTeamColor() != pieceColor) {
                                moves.add(new ChessMove(myPosition, newPosition, null));
                            }
                            break;
                        }
                    }
                }
                break;
            case BISHOP:
                int [][] bishopMoves = {{-1,1}, {1,1}, {-1,-1}, {1,-1}};

                for (int[] move : bishopMoves) {
                    /*int row = myPosition.getRow() + move[0];
                    int col = myPosition.getColumn() + move[1];

                    if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                        ChessPosition newPosition = new ChessPosition(row, col);

                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }*/
                }
                break;
            case PieceType.KNIGHT:
                int [][] knightMoves = {{-2,1}, {-1,2}, {1,2}, {2,1}, {2,-1}, {1,-2}, {-1,-2}, {-2,-1}};

                for (int[] move : knightMoves) {
                    int row = myPosition.getRow() + move[0];
                    int col = myPosition.getColumn() + move[1];

                    if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                        ChessPosition newPosition = new ChessPosition(row, col);

                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }
                }
                break;
            case ROOK:
                int [][] rookMoves = {{0,1}, {-1,0}, {1,0}, {0,-1}};

                for (int[] move : rookMoves) {
                    /*int row = myPosition.getRow() + move[0];
                    int col = myPosition.getColumn() + move[1];

                    if (row >= 1 && row <= 8 && col >= 1 && col <= 8) {
                        ChessPosition newPosition = new ChessPosition(row, col);

                        if (board.getPiece(newPosition) == null || board.getPiece(newPosition).getTeamColor() != pieceColor) {
                            moves.add(new ChessMove(myPosition, newPosition, null));
                        }
                    }*/
                }
                break;
            case PAWN:
                int [][] pawnMoves = {{0,1}, {0,2}};

                break;
        }
        return moves;

       /* ChessPiece piece = board.getPiece(myPosition);
        if (piece.getPieceType() == PieceType.BISHOP){
            return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
        }
        return List.of();*/
    }
}
