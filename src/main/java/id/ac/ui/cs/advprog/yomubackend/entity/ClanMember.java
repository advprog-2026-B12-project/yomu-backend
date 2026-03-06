package id.ac.ui.cs.advprog.yomubackend.entity;

import jakarta.persistence.*;
import lombok.Data;

import java.time.Instant;

@Entity
@Data
@Table(name = "clan_members", uniqueConstraints = {
        @UniqueConstraint(name = "uk_clan_members_clan_user", columnNames = {"clan_id", "user_id"}),
        @UniqueConstraint(name = "uk_clan_members_user", columnNames = {"user_id"}) // 1 user cuma boleh 1 clan (hapus ini kalau mau multi-clan)
})
public class ClanMember {

    public enum Role { LEADER, MEMBER }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // FK ke clan
    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "clan_id", nullable = false)
    private Clan clan;

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private Role role;

    @Column(nullable = false)
    private Instant joinedAt = Instant.now();
}