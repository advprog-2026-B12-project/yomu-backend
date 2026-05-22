package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class ClanNameBlankException extends RuntimeException {
    public ClanNameBlankException() {
        super("Clan name must not be blank");
    }
}
