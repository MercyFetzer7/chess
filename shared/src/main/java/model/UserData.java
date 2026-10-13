package model;

// stores user's username, password, email
public record UserData (
        String username,
        String password,
        String email
) {}
