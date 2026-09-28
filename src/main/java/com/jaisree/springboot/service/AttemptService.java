package com.jaisree.springboot.service;

import com.jaisree.springboot.entity.*;
import com.jaisree.springboot.exception.BusinessRuleException;
import com.jaisree.springboot.exception.ResourceNotFoundException;
import com.jaisree.springboot.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;

@Service
@Transactional
public class AttemptService {

    private final AttemptRepository attemptRepository;
    private final AttemptAnswerRepository attemptAnswerRepository;
    private final StudentRepository studentRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    @Autowired
    public AttemptService(AttemptRepository attemptRepository,
                          AttemptAnswerRepository attemptAnswerRepository,
                          StudentRepository studentRepository,
                          QuizRepository quizRepository,
                          QuestionRepository questionRepository) {
        this.attemptRepository = attemptRepository;
        this.attemptAnswerRepository = attemptAnswerRepository;
        this.studentRepository = studentRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    public Attempt startAttempt(Long studentId, Long quizId) {
        if (studentId == null) {
            throw new BusinessRuleException("Student ID cannot be null.");
        }
        if (quizId == null) {
            throw new BusinessRuleException("Quiz ID cannot be null.");
        }

        Student student = studentRepository.findById(studentId)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + studentId));

        Quiz quiz = quizRepository.findById(quizId)
                .orElseThrow(() -> new ResourceNotFoundException("Quiz not found with ID: " + quizId));

        // MANDATORY BUSINESS RULE: Duplicate Attempt Check
        if (attemptRepository.existsByStudentIdAndQuizId(studentId, quizId)) {
            throw new BusinessRuleException("You have already attempted this quiz.");
        }

        List<Question> questions = questionRepository.findByQuizId(quizId);
        if (questions == null || questions.isEmpty()) {
            throw new BusinessRuleException("This quiz has no questions yet and cannot be attempted.");
        }

        Attempt attempt = new Attempt();
        attempt.setStudent(student);
        attempt.setQuiz(quiz);
        attempt.setStartTime(LocalDateTime.now());
        attempt.setStatus("STARTED");
        attempt.setTotalQuestions(questions.size());
        attempt.setScore(0);

        Attempt savedAttempt = attemptRepository.save(attempt);
        populateAttemptStatistics(savedAttempt);
        return savedAttempt;
    }

    public Attempt submitAttempt(Long attemptId, Map<Long, String> userAnswers, boolean forceAutoSubmit) {
        Attempt attempt = attemptRepository.findById(attemptId)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with ID: " + attemptId));

        // If already submitted, return current state
        if ("SUBMITTED".equals(attempt.getStatus()) || "AUTO_SUBMITTED".equals(attempt.getStatus())) {
            populateAttemptStatistics(attempt);
            return attempt;
        }

        Quiz quiz = attempt.getQuiz();
        LocalDateTime startTime = attempt.getStartTime();
        LocalDateTime now = LocalDateTime.now();

        // MANDATORY BUSINESS RULE: Timer validation
        // Quiz duration in minutes; add 10 seconds grace period for network latency
        LocalDateTime allowedEndTime = startTime.plusMinutes(quiz.getDurationMinutes()).plusSeconds(10);
        boolean isExpired = forceAutoSubmit || now.isAfter(allowedEndTime);

        String status = isExpired ? "AUTO_SUBMITTED" : "SUBMITTED";

        List<Question> questions = questionRepository.findByQuizId(quiz.getId());
        int totalQuestions = questions.size();
        int correct = 0;
        int incorrect = 0;
        int unanswered = 0;

        List<AttemptAnswer> answersList = new ArrayList<>();

        for (Question q : questions) {
            String selected = (userAnswers != null) ? userAnswers.get(q.getId()) : null;
            if (selected != null) {
                selected = selected.trim().toUpperCase();
            }

            boolean isCorrect = false;
            if (selected == null || selected.isEmpty()) {
                unanswered++;
                selected = null;
            } else {
                if (selected.equalsIgnoreCase(q.getCorrectOption().trim())) {
                    correct++;
                    isCorrect = true;
                } else {
                    incorrect++;
                }
            }

            AttemptAnswer answer = new AttemptAnswer();
            answer.setAttempt(attempt);
            answer.setQuestion(q);
            answer.setSelectedOption(selected);
            answer.setCorrect(isCorrect);
            answersList.add(answer);
        }

        // Update answers collection (CascadeType.ALL + orphanRemoval = true handles persistence)
        attempt.getAnswers().clear();
        for (AttemptAnswer ans : answersList) {
            ans.setAttempt(attempt);
            attempt.getAnswers().add(ans);
        }

        attempt.setSubmitTime(now);
        attempt.setScore(correct);
        attempt.setTotalQuestions(totalQuestions);
        attempt.setStatus(status);

        Attempt savedAttempt = attemptRepository.save(attempt);
        populateAttemptStatistics(savedAttempt);
        return savedAttempt;
    }

    public Attempt getAttemptById(Long id) {
        Attempt attempt = attemptRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Attempt not found with ID: " + id));
        populateAttemptStatistics(attempt);
        return attempt;
    }

    public List<Attempt> getAttemptsByStudentId(Long studentId) {
        if (!studentRepository.existsById(studentId)) {
            throw new ResourceNotFoundException("Student not found with ID: " + studentId);
        }
        List<Attempt> list = attemptRepository.findByStudentIdOrderByStartTimeDesc(studentId);
        for (Attempt att : list) {
            populateAttemptStatistics(att);
        }
        return list;
    }

    public List<Attempt> getAttemptsByQuizId(Long quizId) {
        if (!quizRepository.existsById(quizId)) {
            throw new ResourceNotFoundException("Quiz not found with ID: " + quizId);
        }
        List<Attempt> list = attemptRepository.findByQuizId(quizId);
        for (Attempt att : list) {
            populateAttemptStatistics(att);
        }
        return list;
    }

    public Map<String, Object> getReportsData() {
        List<Attempt> allAttempts = attemptRepository.findAllByOrderByStartTimeDesc();
        List<Map<String, Object>> records = new ArrayList<>();

        int totalAttempts = allAttempts.size();
        int totalScore = 0;
        int highestScore = 0;
        int lowestScore = totalAttempts > 0 ? Integer.MAX_VALUE : 0;

        for (Attempt a : allAttempts) {
            populateAttemptStatistics(a);
            int score = a.getScore() != null ? a.getScore() : 0;
            totalScore += score;
            if (score > highestScore) {
                highestScore = score;
            }
            if (score < lowestScore) {
                lowestScore = score;
            }

            Map<String, Object> map = new HashMap<>();
            map.put("attemptId", a.getId());
            map.put("studentId", a.getStudent() != null ? a.getStudent().getId() : null);
            map.put("studentName", a.getStudent() != null ? a.getStudent().getName() : "Unknown");
            map.put("studentEmail", a.getStudent() != null ? a.getStudent().getEmail() : "N/A");
            map.put("className", a.getStudent() != null ? a.getStudent().getClassName() : "N/A");
            map.put("quizId", a.getQuiz() != null ? a.getQuiz().getId() : null);
            map.put("quizTitle", a.getQuiz() != null ? a.getQuiz().getTitle() : "Unknown");
            map.put("category", a.getQuiz() != null ? a.getQuiz().getCategory() : "N/A");
            map.put("score", a.getScore());
            map.put("totalQuestions", a.getTotalQuestions());
            map.put("percentage", a.getPercentage());
            map.put("correctAnswers", a.getCorrectAnswers());
            map.put("incorrectAnswers", a.getIncorrectAnswers());
            map.put("unanswered", a.getUnanswered());
            map.put("startTime", a.getStartTime());
            map.put("submitTime", a.getSubmitTime());
            map.put("status", a.getStatus());
            records.add(map);
        }

        if (lowestScore == Integer.MAX_VALUE) {
            lowestScore = 0;
        }

        double averageScore = totalAttempts > 0 ? (double) totalScore / totalAttempts : 0.0;
        averageScore = Math.round(averageScore * 100.0) / 100.0;

        Map<String, Object> response = new HashMap<>();
        response.put("totalAttempts", totalAttempts);
        response.put("averageScore", averageScore);
        response.put("highestScore", highestScore);
        response.put("lowestScore", lowestScore);
        response.put("totalQuizzes", quizRepository.count());
        response.put("totalStudents", studentRepository.count());
        response.put("attempts", records);

        return response;
    }

    public void populateAttemptStatistics(Attempt attempt) {
        if (attempt == null) return;

        int total = attempt.getTotalQuestions() != null ? attempt.getTotalQuestions() : 0;
        int corr = attempt.getScore() != null ? attempt.getScore() : 0;
        int unans = 0;
        int incorr = 0;

        List<AttemptAnswer> answers = attempt.getAnswers();
        if (answers != null && !answers.isEmpty()) {
            for (AttemptAnswer ans : answers) {
                if (ans.getSelectedOption() == null || ans.getSelectedOption().trim().isEmpty()) {
                    unans++;
                } else if (!ans.isCorrect()) {
                    incorr++;
                }
            }
        } else if ("SUBMITTED".equals(attempt.getStatus()) || "AUTO_SUBMITTED".equals(attempt.getStatus())) {
            unans = Math.max(0, total - corr);
        }

        attempt.setCorrectAnswers(corr);
        attempt.setIncorrectAnswers(incorr);
        attempt.setUnanswered(unans);

        double pct = (total > 0) ? ((double) corr / total) * 100.0 : 0.0;
        attempt.setPercentage(Math.round(pct * 100.0) / 100.0);
    }
}
