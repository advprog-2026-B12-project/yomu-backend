package id.ac.ui.cs.advprog.yomubackend.quiz.repository;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Question;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface QuestionRepository extends JpaRepository<Question, UUID> {
    List<Question> findByReadingId(UUID readingId);
}