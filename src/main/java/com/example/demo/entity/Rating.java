package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;

@Entity
public class Rating {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Min(1)
    @Max(5)
    private int score;

    @ManyToOne(optional = false)
    @JoinColumn(name = "note_id")
    private Note note;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student student;

    public Rating() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public int getScore() { return score; }
    public void setScore(int score) { this.score = score; }
    public Note getNote() { return note; }
    public void setNote(Note note) { this.note = note; }
    public Student getStudent() { return student; }
    public void setStudent(Student student) { this.student = student; }
}
