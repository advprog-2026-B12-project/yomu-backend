package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class UserNotInClanException extends RuntimeException {
    public UserNotInClanException(String message) {
        super(message);
    }
}