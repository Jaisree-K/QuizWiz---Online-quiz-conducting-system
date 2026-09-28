package com.jaisree.springboot.repository;

import com.jaisree.springboot.entity.Quiz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {
    List<Quiz> findByCategory(String category);
    List<Quiz> findAllByOrderByIdDesc();
}
