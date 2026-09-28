package com.example.demo;

import com.example.demo.entity.Student;
import com.example.demo.entity.Subject;
import com.example.demo.repository.StudentRepository;
import com.example.demo.repository.SubjectRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DemoData {

    @Bean
    public CommandLineRunner initData(StudentRepository studentRepository, SubjectRepository subjectRepository) {
        return args -> {
            if (studentRepository.count() == 0) {
                Student admin = new Student("admin", "admin@noteshare.com", "admin123");
                admin.setRole("ADMIN");
                studentRepository.save(admin);
                
                Student user1 = new Student("alice", "alice@noteshare.com", "password123");
                studentRepository.save(user1);
                
                Student user2 = new Student("bob", "bob@noteshare.com", "password123");
                studentRepository.save(user2);
            }
            if (subjectRepository.count() == 0) {
                subjectRepository.save(new Subject("Mathematics"));
                subjectRepository.save(new Subject("Computer Science"));
                subjectRepository.save(new Subject("Physics"));
            }
        };
    }
}
