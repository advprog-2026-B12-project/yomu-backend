package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class PendingJoinRequestAlreadyExistsException extends RuntimeException {
    public PendingJoinRequestAlreadyExistsException() {
        super("User already has a pending clan join request");
    }
}
