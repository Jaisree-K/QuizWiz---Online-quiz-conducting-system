package com.jaisree.springboot.repository;

import com.jaisree.springboot.entity.Attempt;
import com.jaisree.springboot.entity.AttemptAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AttemptAnswerRepository extends JpaRepository<AttemptAnswer, Long> {

    @Query("SELECT aa FROM AttemptAnswer aa WHERE aa.attempt.id = :attemptId")
    List<AttemptAnswer> findByAttemptId(@Param("attemptId") Long attemptId);

    List<AttemptAnswer> findByAttempt(Attempt attempt);
}
