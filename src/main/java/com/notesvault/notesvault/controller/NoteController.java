package com.notesvault.notesvault.controller;

import com.notesvault.notesvault.model.Note;
import com.notesvault.notesvault.repository.NoteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin(origins = "http://localhost:8080") // Allow frontend to access (adjust port as needed)
public class NoteController {

    @Autowired
    private NoteRepository noteRepository;

    // A simple endpoint to get all notes (for testing/admin)
    @GetMapping
    public List<Note> getAllNotes() {
        return noteRepository.findAll();
    }

    // Endpoint to search for notes based on criteria
    // Example: GET /api/notes/search?department=CSE&courseName=DSA&semester=Fall 2023
    @GetMapping("/search")
    public List<Note> searchNotes(
            @RequestParam String department,
            @RequestParam String courseName,
            @RequestParam String semester) {
        return noteRepository.findByDepartmentAndCourseNameAndSemester(department, courseName, semester);
    }

    // Endpoint to download a single note PDF
    // Example: GET /api/notes/download/1
    @GetMapping("/download/{id}")
    public ResponseEntity<Resource> downloadNote(@PathVariable Long id) {
        Note note = noteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Note not found with id: " + id));

        Path filePath = Paths.get("data/notes").resolve(note.getFilePath()).normalize();
        try {
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists() && resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType("application/pdf"))
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                        .body(resource);
            } else {
                throw new RuntimeException("File not found or not readable: " + note.getFilePath());
            }
        } catch (MalformedURLException ex) {
            throw new RuntimeException("Error reading file path: " + note.getFilePath(), ex);
        }
    }

    // Endpoint to download all selected notes as a single ZIP file
    // Example: GET /api/notes/download/all?ids=1,2,3
    @GetMapping("/download/all")
    public ResponseEntity<Resource> downloadAllNotes(@RequestParam List<Long> ids) throws IOException {
        List<Note> notes = noteRepository.findAllById(ids);

        if (notes.isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        // Create a temporary zip file
        File zipFile = File.createTempFile("notes_vault_all", ".zip");
        try (FileOutputStream fos = new FileOutputStream(zipFile);
             ZipOutputStream zipOut = new ZipOutputStream(fos)) {

            for (Note note : notes) {
                Path filePath = Paths.get("data/notes").resolve(note.getFilePath()).normalize();
                File fileToZip = filePath.toFile();

                if (fileToZip.exists() && fileToZip.isFile()) {
                    zipOut.putNextEntry(new ZipEntry(fileToZip.getName()));
                    java.nio.file.Files.copy(fileToZip.toPath(), zipOut);
                    zipOut.closeEntry();
                } else {
                    System.err.println("Warning: File not found or not readable for note ID " + note.getId() + ": " + note.getFilePath());
                }
            }
        }

        Path zipPath = zipFile.toPath();
        Resource resource = new UrlResource(zipPath.toUri());

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/zip"))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"NotesVault_AllInOne.zip\"")
                .body(resource);
    }
}