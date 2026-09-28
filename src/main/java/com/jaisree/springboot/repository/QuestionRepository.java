package com.jaisree.springboot.repository;

import com.jaisree.springboot.entity.Question;
import com.jaisree.springboot.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    @Query("SELECT q FROM Question q WHERE q.quiz.id = :quizId")
    List<Question> findByQuizId(@Param("quizId") Long quizId);

    List<Question> findByQuiz(Quiz quiz);

    @Query("SELECT COUNT(q) FROM Question q WHERE q.quiz.id = :quizId")
    long countByQuizId(@Param("quizId") Long quizId);

    @Modifying
    @Query("DELETE FROM Question q WHERE q.quiz.id = :quizId")
    void deleteByQuizId(@Param("quizId") Long quizId);
}
