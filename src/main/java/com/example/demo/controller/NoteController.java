package com.example.demo.controller;

import com.example.demo.entity.Note;
import com.example.demo.entity.Rating;
import com.example.demo.service.NoteService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
public class NoteController {
    @Autowired private NoteService noteService;

    @GetMapping
    public List<Note> getAllNotes(@RequestParam(required = false) Long subjectId, @RequestParam(required = false) String unit) {
        return noteService.searchNotes(subjectId, unit);
    }

    @GetMapping("/top-rated")
    public List<Note> getTopRatedNotes(@RequestParam Long subjectId) {
        return noteService.getTopRatedNotesBySubject(subjectId);
    }

    @GetMapping("/{id}")
    public Note getNote(@PathVariable Long id) {
        return noteService.getNoteById(id);
    }

    @PostMapping
    public ResponseEntity<Note> createNote(@Valid @RequestBody Note note, @RequestParam Long subjectId, @RequestParam Long uploaderId) {
        return ResponseEntity.status(HttpStatus.CREATED).body(noteService.createNote(note, subjectId, uploaderId));
    }

    @PutMapping("/{id}")
    public Note updateNote(@PathVariable Long id, @Valid @RequestBody Note note, @RequestParam Long requesterId) {
        return noteService.updateNote(id, note, requesterId);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteNote(@PathVariable Long id, @RequestParam Long requesterId) {
        noteService.deleteNote(id, requesterId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/rate")
    public Rating rateNote(@PathVariable Long id, @RequestParam Long studentId, @RequestParam int score) {
        return noteService.rateNote(id, studentId, score);
    }
}
