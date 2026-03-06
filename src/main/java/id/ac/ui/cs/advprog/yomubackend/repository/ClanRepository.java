package id.ac.ui.cs.advprog.yomubackend.repository;

import id.ac.ui.cs.advprog.yomubackend.entity.Clan;
import org.springframework.data.jpa.repository.JpaRepository;


public interface ClanRepository extends JpaRepository<Clan, Long> {
    boolean existsByNameIgnoreCase(String name);
}