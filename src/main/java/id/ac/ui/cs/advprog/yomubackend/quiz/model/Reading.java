package id.ac.ui.cs.advprog.yomubackend.quiz.model;

import jakarta.persistence.*;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
@Entity
@Table(name = "readings")
public class Reading {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @OneToMany(mappedBy = "reading", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Question> questions;
}