package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class JoinRequestAlreadyResolvedException extends RuntimeException {
    public JoinRequestAlreadyResolvedException() {
        super("Clan join request is already resolved");
    }
}
