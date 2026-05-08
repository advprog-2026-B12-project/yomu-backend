package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@AllArgsConstructor
public class ClanMemberResponse {
    private UUID userId;
    private ClanMember.Role role;
    private Instant joinedAt;
}