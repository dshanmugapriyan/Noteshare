package com.example.demo.controller;

import com.example.demo.entity.Subject;
import com.example.demo.repository.SubjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {

    @Autowired
    private SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> getAllSubjects() {
        return subjectRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Subject> getSubject(@PathVariable Long id) {
        return subjectRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public Subject createSubject(@Valid @RequestBody Subject subject) {
        return subjectRepository.save(subject);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Subject> updateSubject(@PathVariable Long id, @Valid @RequestBody Subject subjectDetails) {
        return subjectRepository.findById(id)
                .map(subject -> {
                    subject.setName(subjectDetails.getName());
                    // Update other fields as necessary
                    return ResponseEntity.ok(subjectRepository.save(subject));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public void deleteSubject(@PathVariable Long id) {
        subjectRepository.deleteById(id);
    }
}