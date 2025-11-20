package com.notesvault.notesvault.repository;

import com.notesvault.notesvault.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {

    // Custom query method to find notes by department, courseName, and semester
    List<Note> findByDepartmentAndCourseNameAndSemester(String department, String courseName, String semester);

    // Optional: Methods to get distinct values for dropdowns (if needed)
    // List<String> findDistinctDepartments();
    // List<String> findDistinctCourseNamesByDepartment(String department);
    // List<String> findDistinctSemestersByDepartmentAndCourseName(String department, String courseName);
}