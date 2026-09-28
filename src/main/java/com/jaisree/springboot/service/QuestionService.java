package com.jaisree.springboot.service;

import com.jaisree.springboot.entity.Question;
import com.jaisree.springboot.entity.Quiz;
import com.jaisree.springboot.exception.BusinessRuleException;
import com.jaisree.springboot.exception.ResourceNotFoundException;
import com.jaisree.springboot.repository.QuestionRepository;
import com.jaisree.springboot.repository.QuizRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class QuestionService {

    private final QuestionRepository questionRepository;
    private final QuizRepository quizRepository;

    @Autowired
    public QuestionService(QuestionRepository questionRepository, QuizRepository quizRepository) {
        this.questionRepository = questionRepository;
        this.quizRepository = quizRepository;
    }

    public Question createQuestion(Question question) {
        validateQuestion(question);

        Long targetQuizId = question.getQuizId();
        if (targetQuizId == null && question.getQuiz() != null) {
            targetQuizId = question.getQuiz().getId();
        }

        if (targetQuizId == null) {
            throw new BusinessRuleException("A valid quiz ID is required to add a question.");
        }

        final Long finalQuizId = targetQuizId;
        Quiz quiz = quizRepository.findById(finalQuizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + finalQuizId));

        question.setQuiz(quiz);
        question.setCorrectOption(question.getCorrectOption().toUpperCase().trim());
        return questionRepository.save(question);
    }

    public List<Question> getAllQuestions() {
        return questionRepository.findAll();
    }

    public Question getQuestionById(Long id) {
        return questionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Question not found with ID: " + id));
    }

    public List<Question> getQuestionsByQuizId(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
        return questionRepository.findByQuizId(quizId);
    }

    public Question updateQuestion(Long id, Question updatedQuestion) {
        Question existing = getQuestionById(id);
        validateQuestion(updatedQuestion);

        existing.setQuestionText(updatedQuestion.getQuestionText().trim());
        existing.setOptionA(updatedQuestion.getOptionA().trim());
        existing.setOptionB(updatedQuestion.getOptionB().trim());
        existing.setOptionC(updatedQuestion.getOptionC().trim());
        existing.setOptionD(updatedQuestion.getOptionD().trim());
        existing.setCorrectOption(updatedQuestion.getCorrectOption().toUpperCase().trim());

        if (updatedQuestion.getQuizId() != null && !updatedQuestion.getQuizId().equals(existing.getQuiz().getId())) {
            Quiz quiz = quizRepository.findById(updatedQuestion.getQuizId())
                    .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + updatedQuestion.getQuizId()));
            existing.setQuiz(quiz);
        }

        return questionRepository.save(existing);
    }

    public void deleteQuestion(Long id) {
        Question existing = getQuestionById(id);
        questionRepository.delete(existing);
    }

    public void validateQuestion(Question question) {
        if (question == null) {
            throw new BusinessRuleException("Question details cannot be null.");
        }
        if (question.getQuestionText() == null || question.getQuestionText().trim().isEmpty()) {
            throw new BusinessRuleException("Question text cannot be empty.");
        }
        if (question.getOptionA() == null || question.getOptionA().trim().isEmpty()) {
            throw new BusinessRuleException("Option A cannot be empty.");
        }
        if (question.getOptionB() == null || question.getOptionB().trim().isEmpty()) {
            throw new BusinessRuleException("Option B cannot be empty.");
        }
        if (question.getOptionC() == null || question.getOptionC().trim().isEmpty()) {
            throw new BusinessRuleException("Option C cannot be empty.");
        }
        if (question.getOptionD() == null || question.getOptionD().trim().isEmpty()) {
            throw new BusinessRuleException("Option D cannot be empty.");
        }
        if (question.getCorrectOption() == null || question.getCorrectOption().trim().isEmpty()) {
            throw new BusinessRuleException("Correct option cannot be empty.");
        }
        String opt = question.getCorrectOption().trim().toUpperCase();
        if (!opt.matches("^[A-D]$")) {
            throw new BusinessRuleException("Correct option must be one of: A, B, C, D.");
        }
    }
}
