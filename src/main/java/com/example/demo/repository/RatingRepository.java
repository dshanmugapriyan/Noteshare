package com.example.demo.repository;

import com.example.demo.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    Optional<Rating> findByNoteIdAndStudentId(Long noteId, Long studentId);
}
