package id.ac.ui.cs.advprog.yomubackend.clan.mapper;

import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanMemberResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanJoinRequestResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.dto.ClanResponse;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanJoinRequest;
import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import org.springframework.stereotype.Component;

@Component
public class ClanMapper {

    public ClanResponse toClanResponse(Clan clan, long memberCount) {
        return new ClanResponse(
                clan.getId(),
                clan.getName(),
                clan.getDescription(),
                clan.getLeaderUserId(),
                clan.getDivision(),
                memberCount,
                clan.getCreatedAt()
        );
    }

    public ClanMemberResponse toMemberResponse(ClanMember member) {
        return new ClanMemberResponse(
                member.getUserId(),
                member.getRole(),
                member.getJoinedAt()
        );
    }

    public ClanJoinRequestResponse toJoinRequestResponse(ClanJoinRequest request) {
        Clan clan = request.getClan();

        return new ClanJoinRequestResponse(
                request.getId(),
                clan.getId(),
                clan.getName(),
                request.getUserId(),
                request.getStatus().name(),
                request.getRequestedAt(),
                request.getResolvedAt()
        );
    }
}
