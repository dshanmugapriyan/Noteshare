package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

@Entity
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Title is required")
    private String title;

    @NotBlank(message = "Unit is required")
    private String unit;

    @NotBlank(message = "File reference is required")
    private String fileReference;

    @ManyToOne(optional = false)
    @JoinColumn(name = "subject_id")
    private Subject subject;

    @ManyToOne(optional = false)
    @JoinColumn(name = "student_id")
    private Student uploader;

    @OneToMany(mappedBy = "note", cascade = CascadeType.ALL)
    private List<Rating> ratings;

    public Note() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getFileReference() { return fileReference; }
    public void setFileReference(String fileReference) { this.fileReference = fileReference; }
    public Subject getSubject() { return subject; }
    public void setSubject(Subject subject) { this.subject = subject; }
    public Student getUploader() { return uploader; }
    public void setUploader(Student uploader) { this.uploader = uploader; }
    public List<Rating> getRatings() { return ratings; }
    public void setRatings(List<Rating> ratings) { this.ratings = ratings; }
    
    public double getAverageRating() {
        if (ratings == null || ratings.isEmpty()) return 0.0;
        return ratings.stream().mapToInt(Rating::getScore).average().orElse(0.0);
    }
}
