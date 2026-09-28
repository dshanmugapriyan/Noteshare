package com.example.demo.web;

import com.example.demo.entity.Note;
import com.example.demo.entity.Student;
import com.example.demo.entity.Subject;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import com.example.demo.service.NoteService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/")
public class DashboardController {

    @Autowired private NoteService noteService;
    @Autowired private SubjectRepository subjectRepository;
    @Autowired private StudentRepository studentRepository;

    private final Path fileStorageLocation = Paths.get("uploads").toAbsolutePath().normalize();

    public DashboardController() {
        try {
            Files.createDirectories(this.fileStorageLocation);
        } catch (Exception ex) {
            throw new RuntimeException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    @GetMapping
    public String dashboard(Model model, HttpSession session, @RequestParam(required = false) Long subjectId, @RequestParam(required = false) String unit, @RequestParam(required = false) String fileName, @RequestParam(required = false) Boolean topRated) {
        if (session.getAttribute("loggedInUserId") == null) {
            return "redirect:/login";
        }

        if (Boolean.TRUE.equals(topRated) && subjectId != null) {
            model.addAttribute("notes", noteService.getTopRatedNotesBySubject(subjectId));
        } else {
            model.addAttribute("notes", noteService.searchNotes(subjectId, unit, fileName));
        }
        
        model.addAttribute("subjects", subjectRepository.findAll());
        model.addAttribute("totalNotes", noteService.countNotes());
        model.addAttribute("totalSubjects", subjectRepository.count());
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("selectedSubject", subjectId);
        model.addAttribute("selectedUnit", unit);
        model.addAttribute("selectedFileName", fileName);
        model.addAttribute("topRated", topRated);
        model.addAttribute("loggedInUsername", session.getAttribute("loggedInUsername"));
        return "dashboard";
    }

    @PostMapping("/web/notes")
    public String createNoteWeb(@ModelAttribute Note note, @RequestParam Long subjectId, @RequestParam("file") MultipartFile file, HttpSession session, RedirectAttributes redirectAttrs) {
        Long uploaderId = (Long) session.getAttribute("loggedInUserId");
        if (uploaderId == null) return "redirect:/login";

        try {
            // Save file
            String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path targetLocation = this.fileStorageLocation.resolve(fileName);
            Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
            
            note.setFileReference(fileName);
            noteService.createNote(note, subjectId, uploaderId);
            redirectAttrs.addFlashAttribute("successMessage", "Note uploaded successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/web/notes/{id}/delete")
    public String deleteNoteWeb(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttrs) {
        Long requesterId = (Long) session.getAttribute("loggedInUserId");
        if (requesterId == null) return "redirect:/login";

        try {
            noteService.deleteNote(id, requesterId);
            redirectAttrs.addFlashAttribute("successMessage", "Note deleted successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }

    @PostMapping("/web/notes/{id}/update")
    public String updateNoteWeb(@PathVariable Long id, @ModelAttribute Note note, @RequestParam Long subjectId, @RequestParam(value = "file", required = false) MultipartFile file, HttpSession session, RedirectAttributes redirectAttrs) {
        Long requesterId = (Long) session.getAttribute("loggedInUserId");
        if (requesterId == null) return "redirect:/login";

        try {
            if (file != null && !file.isEmpty()) {
                String fileName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
                Path targetLocation = this.fileStorageLocation.resolve(fileName);
                Files.copy(file.getInputStream(), targetLocation, StandardCopyOption.REPLACE_EXISTING);
                note.setFileReference(fileName);
            }
            
            Subject subject = new Subject();
            subject.setId(subjectId);
            note.setSubject(subject);
            noteService.updateNote(id, note, requesterId);
            redirectAttrs.addFlashAttribute("successMessage", "Note updated successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }
    
    @PostMapping("/web/subjects")
    public String addSubjectWeb(@RequestParam String name, HttpSession session, RedirectAttributes redirectAttrs) {
        if (session.getAttribute("loggedInUserId") == null) return "redirect:/login";

        try {
            Subject subject = new Subject(name);
            subjectRepository.save(subject);
            redirectAttrs.addFlashAttribute("successMessage", "Subject added successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }
    
    @PostMapping("/web/notes/{id}/rate")
    public String rateNoteWeb(@PathVariable Long id, @RequestParam int score, HttpSession session, RedirectAttributes redirectAttrs) {
        Long studentId = (Long) session.getAttribute("loggedInUserId");
        if (studentId == null) return "redirect:/login";

        try {
            noteService.rateNote(id, studentId, score);
            redirectAttrs.addFlashAttribute("successMessage", "Rated successfully!");
        } catch (Exception e) {
            redirectAttrs.addFlashAttribute("errorMessage", e.getMessage());
        }
        return "redirect:/";
    }

    @GetMapping("/files/{fileName:.+}")
    public ResponseEntity<Resource> downloadFile(@PathVariable String fileName) {
        try {
            Path filePath = this.fileStorageLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return ResponseEntity.ok()
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception ex) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
