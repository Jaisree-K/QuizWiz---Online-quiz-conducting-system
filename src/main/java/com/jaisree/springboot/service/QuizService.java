package com.jaisree.springboot.service;

import com.jaisree.springboot.entity.Question;
import com.jaisree.springboot.entity.Quiz;
import com.jaisree.springboot.exception.BusinessRuleException;
import com.jaisree.springboot.exception.ResourceNotFoundException;
import com.jaisree.springboot.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuizService {

    private final QuizRepository quizRepository;

    @Autowired
    public QuizService(QuizRepository quizRepository) {
        this.quizRepository = quizRepository;
    }

    public Quiz createQuiz(Quiz quiz) {
        validateQuiz(quiz);
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        List<Quiz> quizzes = quizRepository.findAllByOrderByIdDesc();
        for (Quiz q : quizzes) {
            q.setQuestionCount(q.getQuestions() != null ? q.getQuestions().size() : 0);
        }
        return quizzes;
    }

    public Quiz getQuizById(Long id) {
        Quiz quiz = quizRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + id));
        quiz.setQuestionCount(quiz.getQuestions() != null ? quiz.getQuestions().size() : 0);
        return quiz;
    }

    public Quiz updateQuiz(Long id, Quiz updatedQuiz) {
        Quiz existingQuiz = getQuizById(id);
        validateQuiz(updatedQuiz);

        existingQuiz.setTitle(updatedQuiz.getTitle().trim());
        existingQuiz.setDescription(updatedQuiz.getDescription());
        existingQuiz.setCategory(updatedQuiz.getCategory().trim());
        existingQuiz.setDurationMinutes(updatedQuiz.getDurationMinutes());

        return quizRepository.save(existingQuiz);
    }

    public void deleteQuiz(Long id) {
        Quiz existingQuiz = getQuizById(id);
        quizRepository.delete(existingQuiz);
    }

    public Quiz addQuestionToQuiz(Long quizId, Question question) {
        Quiz quiz = getQuizById(quizId);
        question.setQuiz(quiz);
        quiz.getQuestions().add(question);
        return quizRepository.save(quiz);
    }

    public void validateQuiz(Quiz quiz) {
        if (quiz == null) {
            throw new BusinessRuleException("Quiz details cannot be null.");
        }
        if (quiz.getTitle() == null || quiz.getTitle().trim().isEmpty()) {
            throw new BusinessRuleException("Quiz title cannot be empty.");
        }
        if (quiz.getCategory() == null || quiz.getCategory().trim().isEmpty()) {
            throw new BusinessRuleException("Quiz category cannot be empty.");
        }
        if (quiz.getDurationMinutes() == null || quiz.getDurationMinutes() <= 0) {
            throw new BusinessRuleException("Quiz duration must be greater than 0 minutes.");
        }
    }
}
