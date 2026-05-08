package id.ac.ui.cs.advprog.yomubackend.clan.league;

public interface MemberStatProvider {
    MemberStat getStatForUser(Long userId);
}