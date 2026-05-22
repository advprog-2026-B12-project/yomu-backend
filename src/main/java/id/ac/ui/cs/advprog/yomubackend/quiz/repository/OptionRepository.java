package id.ac.ui.cs.advprog.yomubackend.quiz.repository;

import id.ac.ui.cs.advprog.yomubackend.quiz.model.Option;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

public interface OptionRepository extends JpaRepository<Option, UUID> {
    List<Option> findByQuestionId(UUID questionId);

    @Modifying
    @Transactional
    @Query("DELETE FROM Option o WHERE o.id = :id")
    void deleteOptionById(@Param("id") UUID id);
}