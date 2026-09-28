package com.jaisree.springboot.repository;

import com.jaisree.springboot.entity.Attempt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AttemptRepository extends JpaRepository<Attempt, Long> {

    @Query("SELECT a FROM Attempt a WHERE a.student.id = :studentId")
    List<Attempt> findByStudentId(@Param("studentId") Long studentId);

    @Query("SELECT a FROM Attempt a WHERE a.student.id = :studentId ORDER BY a.startTime DESC")
    List<Attempt> findByStudentIdOrderByStartTimeDesc(@Param("studentId") Long studentId);

    @Query("SELECT a FROM Attempt a WHERE a.quiz.id = :quizId")
    List<Attempt> findByQuizId(@Param("quizId") Long quizId);

    @Query("SELECT a FROM Attempt a WHERE a.student.id = :studentId AND a.quiz.id = :quizId")
    Optional<Attempt> findByStudentIdAndQuizId(@Param("studentId") Long studentId, @Param("quizId") Long quizId);

    @Query("SELECT COUNT(a) > 0 FROM Attempt a WHERE a.student.id = :studentId AND a.quiz.id = :quizId")
    boolean existsByStudentIdAndQuizId(@Param("studentId") Long studentId, @Param("quizId") Long quizId);

    List<Attempt> findAllByOrderByStartTimeDesc();
}
