package com.example.demo.repository;

import com.example.demo.entity.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findBySubjectId(Long subjectId);
    List<Note> findByUnit(String unit);
    List<Note> findBySubjectIdAndUnit(Long subjectId, String unit);
    
    @Query("SELECT n FROM Note n WHERE n.subject.id = :subjectId ORDER BY (SELECT AVG(r.score) FROM Rating r WHERE r.note.id = n.id) DESC")
    List<Note> findTopRatedBySubjectId(@Param("subjectId") Long subjectId);
}
