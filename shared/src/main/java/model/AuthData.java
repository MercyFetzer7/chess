package model;

// connects an authentication token to a username
public record AuthData (
        String authToken,
        String username
) {}

