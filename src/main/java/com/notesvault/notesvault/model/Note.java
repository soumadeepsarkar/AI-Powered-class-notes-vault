package com.notesvault.notesvault.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class Note {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String department;
    private String courseName;
    private String semester; //sem-1,sem-2
    private String moduleName; // e.g., "Mod-1", "Mod-2"
    private String filePath;   // Path to the actual PDF file

    // Constructors
    public Note() {
    }

    public Note(String department, String courseName, String semester, String moduleName, String filePath) {
        this.department = department;
        this.courseName = courseName;
        this.semester = semester;
        this.moduleName = moduleName;
        this.filePath = filePath;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDepartment() {
        return department;
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getModuleName() {
        return moduleName;
    }

    public void setModuleName(String moduleName) {
        this.moduleName = moduleName;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    @Override
    public String toString() {
        return "Note{" +
                "id=" + id +
                ", department='" + department + '\'' +
                ", courseName='" + courseName + '\'' +
                ", semester='" + semester + '\'' +
                ", moduleName='" + moduleName + '\'' +
                ", filePath='" + filePath + '\'' +
                '}';
    }
}