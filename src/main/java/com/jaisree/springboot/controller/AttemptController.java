package com.jaisree.springboot.controller;

import com.jaisree.springboot.entity.Attempt;
import com.jaisree.springboot.exception.BusinessRuleException;
import com.jaisree.springboot.service.AttemptService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class AttemptController {

    private final AttemptService attemptService;

    @Autowired
    public AttemptController(AttemptService attemptService) {
        this.attemptService = attemptService;
    }

    @PostMapping("/api/attempts/start")
    public ResponseEntity<Attempt> startAttempt(
            @RequestBody(required = false) Map<String, Object> body,
            @RequestParam(required = false) Long studentId,
            @RequestParam(required = false) Long quizId) {

        Long finalStudentId = studentId;
        Long finalQuizId = quizId;

        if (body != null) {
            if (body.get("studentId") != null) {
                finalStudentId = Long.valueOf(body.get("studentId").toString());
            }
            if (body.get("quizId") != null) {
                finalQuizId = Long.valueOf(body.get("quizId").toString());
            }
        }

        if (finalStudentId == null || finalQuizId == null) {
            throw new BusinessRuleException("Both studentId and quizId are required to start an attempt.");
        }

        Attempt attempt = attemptService.startAttempt(finalStudentId, finalQuizId);
        return new ResponseEntity<>(attempt, HttpStatus.CREATED);
    }

    @PostMapping("/api/attempts/{attemptId}/submit")
    public ResponseEntity<Attempt> submitAttempt(
            @PathVariable Long attemptId,
            @RequestBody(required = false) Map<String, Object> body) {

        Map<Long, String> userAnswers = new HashMap<>();
        boolean forceAutoSubmit = false;

        if (body != null) {
            if (body.containsKey("forceAutoSubmit")) {
                forceAutoSubmit = Boolean.parseBoolean(body.get("forceAutoSubmit").toString());
            } else if (body.containsKey("autoSubmitted")) {
                forceAutoSubmit = Boolean.parseBoolean(body.get("autoSubmitted").toString());
            }

            Object answersObj = body.get("answers");
            if (answersObj instanceof Map<?, ?> rawMap) {
                for (Map.Entry<?, ?> entry : rawMap.entrySet()) {
                    try {
                        Long qId = Long.valueOf(entry.getKey().toString());
                        String sel = entry.getValue() != null ? entry.getValue().toString() : null;
                        userAnswers.put(qId, sel);
                    } catch (NumberFormatException ignored) {
                    }
                }
            } else {
                // In case the body directly maps questionId -> selectedOption
                for (Map.Entry<String, Object> entry : body.entrySet()) {
                    if (!"forceAutoSubmit".equalsIgnoreCase(entry.getKey())
                            && !"autoSubmitted".equalsIgnoreCase(entry.getKey())) {
                        try {
                            Long qId = Long.valueOf(entry.getKey());
                            String sel = entry.getValue() != null ? entry.getValue().toString() : null;
                            userAnswers.put(qId, sel);
                        } catch (NumberFormatException ignored) {
                        }
                    }
                }
            }
        }

        Attempt result = attemptService.submitAttempt(attemptId, userAnswers, forceAutoSubmit);
        return ResponseEntity.ok(result);
    }

    @GetMapping("/api/attempts/{attemptId}")
    public ResponseEntity<Attempt> getAttemptById(@PathVariable Long attemptId) {
        return ResponseEntity.ok(attemptService.getAttemptById(attemptId));
    }

    @GetMapping("/api/attempts/student/{studentId}")
    public ResponseEntity<List<Attempt>> getAttemptsByStudentId(@PathVariable Long studentId) {
        return ResponseEntity.ok(attemptService.getAttemptsByStudentId(studentId));
    }

    @GetMapping("/api/reports")
    public ResponseEntity<Map<String, Object>> getReports() {
        return ResponseEntity.ok(attemptService.getReportsData());
    }
}
