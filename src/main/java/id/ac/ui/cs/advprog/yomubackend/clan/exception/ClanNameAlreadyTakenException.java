package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class ClanNameAlreadyTakenException extends RuntimeException {
    public ClanNameAlreadyTakenException(String name) {
        super("Clan name already exists: " + name);
    }
}
