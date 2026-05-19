package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanJoinRequestResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;

import java.util.List;
import java.util.UUID;

public interface ClanJoinRequestService {
    ClanJoinRequestResponse requestToJoinClan(UUID userId, Long clanId);
    List<ClanJoinRequestResponse> getPendingJoinRequests(UUID requesterUserId, Long clanId);
    ClanMemberResponse approveJoinRequest(UUID requesterUserId, Long requestId);
    void rejectJoinRequest(UUID requesterUserId, Long requestId);
}
