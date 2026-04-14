package id.ac.ui.cs.advprog.yomubackend.clan.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import id.ac.ui.cs.advprog.yomubackend.clan.entity.Clan;


public interface ClanRepository extends JpaRepository<Clan, Long> {
    boolean existsByNameIgnoreCase(String name);
}