package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;

import java.util.List;
import java.util.UUID;

public interface ClanManagementService {
    ClanResponse createClan(UUID userId, String name, String description);
    List<ClanResponse> getAllClans();
    ClanResponse getClanById(Long clanId);
    void deleteClan(UUID requesterUserId, Long clanId);
    List<ClanMemberResponse> getMembers(Long clanId);
}
