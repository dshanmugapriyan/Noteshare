package com.example.demo.service;

import com.example.demo.entity.Note;
import com.example.demo.entity.Rating;
import com.example.demo.entity.Student;
import com.example.demo.entity.Subject;
import com.example.demo.exception.BusinessRuleException;
import com.example.demo.exception.ResourceNotFoundException;
import com.example.demo.repository.NoteRepository;
import com.example.demo.repository.RatingRepository;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class NoteService {
    @Autowired private NoteRepository noteRepository;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private StudentRepository studentRepository;
    @Autowired private RatingRepository ratingRepository;

    public List<Note> getAllNotes() { return noteRepository.findAll(); }
    public Note getNoteById(Long id) { 
        return noteRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Note not found")); 
    }

    public List<Note> searchNotes(Long subjectId, String unit) {
        if (subjectId != null && unit != null && !unit.isEmpty()) {
            return noteRepository.findBySubjectIdAndUnit(subjectId, unit);
        } else if (subjectId != null) {
            return noteRepository.findBySubjectId(subjectId);
        } else if (unit != null && !unit.isEmpty()) {
            return noteRepository.findByUnit(unit);
        }
        return noteRepository.findAll();
    }

    public List<Note> getTopRatedNotesBySubject(Long subjectId) {
        return noteRepository.findTopRatedBySubjectId(subjectId);
    }

    @Transactional
    public Note createNote(Note note, Long subjectId, Long uploaderId) {
        Subject subject = subjectRepository.findById(subjectId).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
        Student uploader = studentRepository.findById(uploaderId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        note.setSubject(subject);
        note.setUploader(uploader);
        return noteRepository.save(note);
    }

    @Transactional
    public Note updateNote(Long id, Note updatedNote, Long requesterId) {
        Note existingNote = getNoteById(id);
        Student requester = studentRepository.findById(requesterId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!existingNote.getUploader().getId().equals(requesterId)) {
            throw new BusinessRuleException("Only the uploader can update their own note");
        }
        
        existingNote.setTitle(updatedNote.getTitle());
        existingNote.setUnit(updatedNote.getUnit());
        if (updatedNote.getFileReference() != null) {
            existingNote.setFileReference(updatedNote.getFileReference());
        }
        if (updatedNote.getSubject() != null && updatedNote.getSubject().getId() != null) {
            Subject subject = subjectRepository.findById(updatedNote.getSubject().getId()).orElseThrow(() -> new ResourceNotFoundException("Subject not found"));
            existingNote.setSubject(subject);
        }
        
        return noteRepository.save(existingNote);
    }

    @Transactional
    public void deleteNote(Long id, Long requesterId) {
        Note existingNote = getNoteById(id);
        Student requester = studentRepository.findById(requesterId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        if (!existingNote.getUploader().getId().equals(requesterId)) {
            throw new BusinessRuleException("Only the original uploader can delete their own note");
        }
        
        noteRepository.deleteById(id);
    }

    @Transactional
    public Rating rateNote(Long noteId, Long studentId, int score) {
        Note note = getNoteById(noteId);
        Student student = studentRepository.findById(studentId).orElseThrow(() -> new ResourceNotFoundException("Student not found"));
        
        Optional<Rating> existingRating = ratingRepository.findByNoteIdAndStudentId(noteId, studentId);
        if (existingRating.isPresent()) {
            throw new BusinessRuleException("A student can rate a specific note only once");
        }
        
        Rating rating = new Rating();
        rating.setScore(score);
        rating.setNote(note);
        rating.setStudent(student);
        
        return ratingRepository.save(rating);
    }
    
    public long countNotes() {
        return noteRepository.count();
    }
}
