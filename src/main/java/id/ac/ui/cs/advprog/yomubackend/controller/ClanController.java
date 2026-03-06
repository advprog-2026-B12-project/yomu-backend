package id.ac.ui.cs.advprog.yomubackend.controller;

import id.ac.ui.cs.advprog.yomubackend.dto.CreateClanRequest;
import id.ac.ui.cs.advprog.yomubackend.dto.JoinClanRequest;
import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import id.ac.ui.cs.advprog.yomubackend.entity.ClanMember;
import id.ac.ui.cs.advprog.yomubackend.repository.ClanRepository;
import id.ac.ui.cs.advprog.yomubackend.service.ClanService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/clans")
public class ClanController {

    private final ClanRepository clanRepository;
    private final ClanService clanService;

    public ClanController(ClanRepository clanRepository, ClanService clanService) {
        this.clanRepository = clanRepository;
        this.clanService = clanService;
    }

    // list clans (biar frontend bisa pilih)
    @GetMapping
    public List<Clan> listClans() {
        return clanRepository.findAll();
    }

    // create clan
    @PostMapping
    public Clan createClan(@RequestBody CreateClanRequest req) {
        return clanService.createClan(req.getUserId(), req.getName(), req.getDescription());
    }

    // join clan
    @PostMapping("/{clanId}/join")
    public ClanMember joinClan(@PathVariable Long clanId, @RequestBody JoinClanRequest req) {
        return clanService.joinClan(req.getUserId(), clanId);
    }
}