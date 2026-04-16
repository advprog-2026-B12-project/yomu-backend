package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class ClanNotFoundException extends RuntimeException {
    public ClanNotFoundException(String message) {
        super(message);
    }
}