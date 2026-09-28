package com.example.demo.controller;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {
    @Autowired private StudentRepository studentRepository;

    @GetMapping
    public List<Student> getAllStudents() { return studentRepository.findAll(); }

    @PostMapping
    public Student createStudent(@Valid @RequestBody Student student) { return studentRepository.save(student); }
}
