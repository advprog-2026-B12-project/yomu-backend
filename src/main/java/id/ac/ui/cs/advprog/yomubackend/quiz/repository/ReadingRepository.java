package id.ac.ui.cs.advprog.yomubackend.quiz.repository;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Reading;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface ReadingRepository extends JpaRepository<Reading, UUID> {}