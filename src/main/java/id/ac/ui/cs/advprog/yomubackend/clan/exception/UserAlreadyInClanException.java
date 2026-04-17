package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class UserAlreadyInClanException extends RuntimeException {
    public UserAlreadyInClanException(String message) {
        super(message);
    }
}