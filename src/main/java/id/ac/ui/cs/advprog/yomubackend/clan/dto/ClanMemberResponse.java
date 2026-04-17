package id.ac.ui.cs.advprog.yomubackend.clan.dto;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.Instant;

@Data
@AllArgsConstructor
public class ClanMemberResponse {
    private Long userId;
    private ClanMember.Role role;
    private Instant joinedAt;
}