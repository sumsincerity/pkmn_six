package ru.pchelintsev.pkmn.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.pchelintsev.pkmn.models.student.Student;
import ru.pchelintsev.pkmn.services.StudentService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentController {
    private final StudentService studentService;

    @GetMapping("/all")
    public ResponseEntity<List<Student>> getAllStudents() {
        return ResponseEntity.ok(studentService.getAllStudents());
    }

    @GetMapping("/group/{group}")
    public ResponseEntity<List<Student>> getStudentsByGroup(@PathVariable String group) {
        return ResponseEntity.ok(studentService.getStudentsByGroup(group));
    }

    @GetMapping("")
    public ResponseEntity<Student> getCardByOwnerId(@RequestBody Student student) {
        return ResponseEntity.ok(studentService.getStudentByFullName(student.getFamilyName(), student.getFirstName(), student.getSurName()));
    }

    @PostMapping("")
    public ResponseEntity<Student> saveStudent(@RequestBody Student student) {
        return ResponseEntity.ok(Student.fromEntity(studentService.saveStudent(student)));
    }
}