package id.ac.ui.cs.advprog.yomubackend.clan.service;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.ClanMember;

import java.util.List;

public interface ClanScoreModifier {
    double calculateMultiplier(List<ClanMember> members);
}
