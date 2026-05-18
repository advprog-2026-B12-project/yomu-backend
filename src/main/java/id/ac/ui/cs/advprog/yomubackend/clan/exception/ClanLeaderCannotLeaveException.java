package id.ac.ui.cs.advprog.yomubackend.clan.exception;

public class ClanLeaderCannotLeaveException extends RuntimeException {
    public ClanLeaderCannotLeaveException() {
        super("Clan leader cannot leave the clan. Delete the clan instead.");
    }
}
