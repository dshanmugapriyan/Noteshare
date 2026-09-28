package com.example.demo.web;

import com.example.demo.entity.Student;
import com.example.demo.repository.StudentRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class LoginController {

    @Autowired
    private StudentRepository studentRepository;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        Optional<Student> studentOpt = studentRepository.findByUsername(username);
        
        if (studentOpt.isEmpty() || !password.equals(studentOpt.get().getPassword())) {
            model.addAttribute("errorMessage", "Invalid username or password.");
            return "login";
        }

        Student student = studentOpt.get();
        session.setAttribute("loggedInUserId", student.getId());
        session.setAttribute("loggedInUsername", student.getUsername());
        session.setAttribute("loggedInUserRole", student.getRole());

        return "redirect:/";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "register";
    }

    @PostMapping("/register")
    public String register(@RequestParam String username, @RequestParam String email, @RequestParam String password, Model model) {
        if (studentRepository.findByUsername(username).isPresent()) {
            model.addAttribute("errorMessage", "Username already exists. Please choose another one.");
            return "register";
        }
        
        Student newStudent = new Student(username, email, password);
        studentRepository.save(newStudent);
        model.addAttribute("successMessage", "Account created successfully! You can now log in.");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
