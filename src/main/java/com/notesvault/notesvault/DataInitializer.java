package com.notesvault.notesvault;

import com.notesvault.notesvault.model.Note;
import com.notesvault.notesvault.repository.NoteRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Configuration
public class DataInitializer {

    @Bean
    public CommandLineRunner initDatabase(NoteRepository noteRepository) {
        return args -> {
            // Create a directory for notes if it doesn't exist
            Path notesDir = Paths.get("data/notes");
            if (!Files.exists(notesDir)) {
                Files.createDirectories(notesDir);
            }

            // Create some dummy PDF files for testing
            createDummyPdfFile(notesDir, "CSE-AIML_AI_SEM 1.pdf");
            createDummyPdfFile(notesDir, "CSE-DS_AI_SEM 1.pdf");
            createDummyPdfFile(notesDir, "DIP-CSE_AI_SEM 1.pdf");
            createDummyPdfFile(notesDir, "ECE_Signals_SEM 1.pdf");
            createDummyPdfFile(notesDir, "MECH_MACHINE-DESIGN.pdf");

            // Add sample notes to the database
            if (noteRepository.count() == 0) {
                noteRepository.save(new Note("CSE-AIML", "AI", "SEM 1", "SEM 1", "CSE-AIML_AI_SEM 1.pdf"));
                noteRepository.save(new Note("CSE-DS", "AI", "SEM 1", "SEM 1", "CSE-DS_AI_SEM 1.pdf"));
                noteRepository.save(new Note("DIP-CSE", "AI", "SEM 1", "SEM 1", "DIP-CSE_AI_SEM 1.pdf"));
                noteRepository.save(new Note("ECE", "Signals", "SEM 1", "SEM 1", "ECE_Signals_SEM 1.pdf"));
                noteRepository.save(new Note("ECE", "Signals", "Spring 2024", "SEM 1", "MECH_MACHINE-DESIGN.pdf"));

                System.out.println("Sample notes added to H2 database.");
            }
        };
    }

    private void createDummyPdfFile(Path directory, String fileName) throws IOException {
        Path filePath = directory.resolve(fileName);
        if (!Files.exists(filePath)) {
            // Create a very basic dummy PDF content
            String content = "%PDF-1.4\n1 0 obj<</Type/Catalog/Pages 2 0 R>>endobj\n2 0 obj<</Type/Pages/Count 1/Kids[3 0 R]>>endobj\n3 0 obj<</Type/Page/MediaBox[0 0 612 792]/Parent 2 0 R/Contents 4 0 R>>endobj\n4 0 obj<</Length 55>>stream\nBT\n/F1 24 Tf\n100 700 Td\n(Dummy PDF for " + fileName + ") Tj\nET\nendstream\n5 0 obj<</Size 6/Root 1 0 R>>endobj\nxref\n0 6\n0000000000 65535 f\n0000000009 00000 n\n0000000074 00000 n\n0000000130 00000 n\n0000000257 00000 n\n0000000371 00000 n\ntrailer<</Size 6/Root 1 0 R/Info<</Creator(PDF-Demo)>>>>startxref\n386\n%%EOF";
            Files.writeString(filePath, content);
            System.out.println("Created dummy PDF: " + filePath);
        }
    }
} 