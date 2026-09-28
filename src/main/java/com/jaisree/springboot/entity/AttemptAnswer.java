package com.jaisree.springboot.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

@Entity
@Table(name = "attempt_answers")
public class AttemptAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attempt_id", nullable = false)
    @JsonIgnoreProperties({"answers", "student"})
    private Attempt attempt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "question_id", nullable = false)
    @JsonIgnoreProperties({"quiz"})
    private Question question;

    @Column(name = "selected_option", length = 5)
    private String selectedOption;

    @Column(nullable = false)
    private boolean correct;

    @Transient
    private Long questionId;

    public AttemptAnswer() {
    }

    public AttemptAnswer(Attempt attempt, Question question, String selectedOption, boolean correct) {
        this.attempt = attempt;
        this.question = question;
        this.selectedOption = selectedOption;
        this.correct = correct;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Attempt getAttempt() {
        return attempt;
    }

    public void setAttempt(Attempt attempt) {
        this.attempt = attempt;
    }

    public Question getQuestion() {
        return question;
    }

    public void setQuestion(Question question) {
        this.question = question;
    }

    public String getSelectedOption() {
        return selectedOption;
    }

    public void setSelectedOption(String selectedOption) {
        this.selectedOption = selectedOption;
    }

    public boolean isCorrect() {
        return correct;
    }

    public void setCorrect(boolean correct) {
        this.correct = correct;
    }

    public Long getQuestionId() {
        if (questionId != null) {
            return questionId;
        }
        return question != null ? question.getId() : null;
    }

    public void setQuestionId(Long questionId) {
        this.questionId = questionId;
    }
}
