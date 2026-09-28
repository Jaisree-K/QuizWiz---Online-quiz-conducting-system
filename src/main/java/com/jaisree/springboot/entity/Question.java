package com.jaisree.springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

@Entity
@Table(name = "questions")
public class Question {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Question text cannot be empty")
    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;

    @NotBlank(message = "Option A cannot be empty")
    @Column(name = "option_a", nullable = false)
    private String optionA;

    @NotBlank(message = "Option B cannot be empty")
    @Column(name = "option_b", nullable = false)
    private String optionB;

    @NotBlank(message = "Option C cannot be empty")
    @Column(name = "option_c", nullable = false)
    private String optionC;

    @NotBlank(message = "Option D cannot be empty")
    @Column(name = "option_d", nullable = false)
    private String optionD;

    @NotBlank(message = "Correct option cannot be empty")
    @Pattern(regexp = "^[A-Da-d]$", message = "Correct option must be A, B, C, or D")
    @Column(name = "correct_option", nullable = false, length = 5)
    private String correctOption;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "quiz_id", nullable = false)
    @JsonIgnoreProperties({"questions", "attempts"})
    private Quiz quiz;

    @Transient
    private Long quizId;

    public Question() {
    }

    public Question(String questionText, String optionA, String optionB, String optionC, String optionD, String correctOption, Quiz quiz) {
        this.questionText = questionText;
        this.optionA = optionA;
        this.optionB = optionB;
        this.optionC = optionC;
        this.optionD = optionD;
        this.correctOption = correctOption != null ? correctOption.toUpperCase() : null;
        this.quiz = quiz;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getQuestionText() {
        return questionText;
    }

    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }

    public String getOptionA() {
        return optionA;
    }

    public void setOptionA(String optionA) {
        this.optionA = optionA;
    }

    public String getOptionB() {
        return optionB;
    }

    public void setOptionB(String optionB) {
        this.optionB = optionB;
    }

    public String getOptionC() {
        return optionC;
    }

    public void setOptionC(String optionC) {
        this.optionC = optionC;
    }

    public String getOptionD() {
        return optionD;
    }

    public void setOptionD(String optionD) {
        this.optionD = optionD;
    }

    public String getCorrectOption() {
        return correctOption;
    }

    public void setCorrectOption(String correctOption) {
        this.correctOption = correctOption != null ? correctOption.toUpperCase().trim() : null;
    }

    public Quiz getQuiz() {
        return quiz;
    }

    public void setQuiz(Quiz quiz) {
        this.quiz = quiz;
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
}
