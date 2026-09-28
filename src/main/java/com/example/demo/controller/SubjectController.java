package com.example.demo.controller;

import com.example.demo.entity.Subject;
import com.example.demo.repository.SubjectRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/subjects")
public class SubjectController {
    @Autowired private SubjectRepository subjectRepository;

    @GetMapping
    public List<Subject> getAllSubjects() { return subjectRepository.findAll(); }

    @PostMapping
    public Subject createSubject(@Valid @RequestBody Subject subject) { return subjectRepository.save(subject); }
}
