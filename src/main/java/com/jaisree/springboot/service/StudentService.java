package com.jaisree.springboot.service;

import com.jaisree.springboot.entity.Student;
import com.jaisree.springboot.exception.BusinessRuleException;
import com.jaisree.springboot.exception.ResourceNotFoundException;
import com.jaisree.springboot.repository.StudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class StudentService {

    private final StudentRepository studentRepository;

    @Autowired
    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student createStudent(Student student) {
        if (student == null) {
            throw new BusinessRuleException("Student details cannot be null.");
        }
        if (student.getEmail() == null || student.getEmail().trim().isEmpty()) {
            throw new BusinessRuleException("Student email cannot be empty.");
        }
        String normalizedEmail = student.getEmail().trim().toLowerCase();
        if (studentRepository.existsByEmail(normalizedEmail)) {
            throw new BusinessRuleException("A student with email '" + student.getEmail() + "' already exists.");
        }
        student.setEmail(normalizedEmail);
        return studentRepository.save(student);
    }

    public List<Student> getAllStudents() {
        return studentRepository.findAll();
    }

    public Student getStudentById(Long id) {
        return studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with ID: " + id));
    }

    public Optional<Student> findStudentByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        return studentRepository.findByEmail(email.trim().toLowerCase());
    }
}
