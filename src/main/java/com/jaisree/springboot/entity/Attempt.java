package com.jaisree.springboot.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "attempts")
public class Attempt {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "student_id", nullable = false)
    @JsonIgnoreProperties({"attempts"})
    private Student student;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnoreProperties({"attempts", "questions"})
    private Quiz quiz;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "start_time", nullable = false)
    private LocalDateTime startTime;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "submit_time")
    private LocalDateTime submitTime;

    private Integer score = 0;

    @Column(name = "total_questions")
    private Integer totalQuestions = 0;

    @Column(nullable = false, length = 20)
    private String status; // STARTED, SUBMITTED, AUTO_SUBMITTED

    @OneToMany(mappedBy = "attempt", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties({"attempt"})
    private List<AttemptAnswer> answers = new ArrayList<>();

    @Transient
    private Integer correctAnswers;

    @Transient
    private Integer incorrectAnswers;

    @Transient
    private Integer unanswered;

    @Transient
    private Double percentage;

    @Transient
    private Long studentId;

    @Transient
    private Long quizId;

    @Transient
    private Map<Long, String> responses;

    public Attempt() {
    }

    public Attempt(Student student, Quiz quiz, LocalDateTime startTime, String status) {
        this.student = student;
        this.quiz = quiz;
        this.startTime = startTime;
        this.status = status;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public void setStartTime(LocalDateTime startTime) {
        this.startTime = startTime;
    }

    public LocalDateTime getSubmitTime() {
        return submitTime;
    }

    public void setSubmitTime(LocalDateTime submitTime) {
        this.submitTime = submitTime;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public Integer getTotalQuestions() {
        return totalQuestions;
    }

    public void setTotalQuestions(Integer totalQuestions) {
        this.totalQuestions = totalQuestions;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AttemptAnswer> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AttemptAnswer> answers) {
        this.answers = answers;
    }

    public Integer getCorrectAnswers() {
        return correctAnswers;
    }

    public void setCorrectAnswers(Integer correctAnswers) {
        this.correctAnswers = correctAnswers;
    }

    public Integer getIncorrectAnswers() {
        return incorrectAnswers;
    }

    public void setIncorrectAnswers(Integer incorrectAnswers) {
        this.incorrectAnswers = incorrectAnswers;
    }

    public Integer getUnanswered() {
        return unanswered;
    }

    public void setUnanswered(Integer unanswered) {
        this.unanswered = unanswered;
    }

    public Double getPercentage() {
        return percentage;
    }

    public void setPercentage(Double percentage) {
        this.percentage = percentage;
    }

    public Long getStudentId() {
        if (studentId != null) {
            return studentId;
        }
        return student != null ? student.getId() : null;
    }

    public void setStudentId(Long studentId) {
        this.studentId = studentId;
    }

    public Long getQuizId() {
        if (quizId != null) {
            return quizId;
        }
        return quiz != null ? quiz.getId() : null;
    }

    public void setQuizId(Long quizId) {
        this.quizId = quizId;
    }

    public Map<Long, String> getResponses() {
        return responses;
    }

    public void setResponses(Map<Long, String> responses) {
        this.responses = responses;
    }

    public String getStudentName() {
        return student != null ? student.getName() : null;
    }

    public String getQuizTitle() {
        return quiz != null ? quiz.getTitle() : null;
    }
}
