package id.ac.ui.cs.advprog.yomubackend.clan.league;

import java.util.UUID;

public interface MemberStatProvider {
    MemberStat getStatForUser(UUID userId);
}