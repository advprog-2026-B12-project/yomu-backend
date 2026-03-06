package id.ac.ui.cs.advprog.yomubackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "clans", uniqueConstraints = {
        @UniqueConstraint(name = "uk_clans_name", columnNames = {"name"})
})
public class Clan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 80)
    private String name;

    @Column(length = 300)
    private String description;

    // sementara pakai Long aja (ga FK ke user dulu biar ga nabrak modul 1)
    @Column(nullable = false)
    private Long leaderUserId;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();
}