package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class UnauthorizedClanActionException extends RuntimeException {
    public UnauthorizedClanActionException(String message) {
        super(message);
    }
}