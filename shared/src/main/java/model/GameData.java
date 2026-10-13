package model;
import chess.ChessGame;

// stores the game's ID, players, name, and hess state
public record GameData (
        int gameID,
        String whiteUsername,
        String blackUsername,
        String gameName,
        ChessGame game
) {}