// File: src/main/java/com/example/countryessay/controller/EssayController.java
package com.example.countryessay.controller;

import com.example.countryessay.service.GeminiService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@Controller
public class EssayController {

    @Autowired
    private GeminiService geminiService;

    @GetMapping("/")
    public String showForm() {
        return "index";
    }

    @PostMapping("/generate")
    public String generateEssay(@RequestParam("prompt") String country, Model model) {
        String essay = geminiService.getEssay(country);
        model.addAttribute("country", country);
        model.addAttribute("essay", essay);
        return "result";
    }

    @GetMapping("/generate")
    public String handleGetToGenerate() {
        return "redirect:/"; // Redirect to form if accessed via GET
    }

    @PostMapping("/download")
    public ResponseEntity<InputStreamResource> downloadMarkdown(@RequestParam String essay, @RequestParam String country) throws IOException {
        String filename = country.replaceAll("\\s+", "_") + "_essay.md";
        File tempFile = File.createTempFile("essay", ".md");
        try (FileWriter writer = new FileWriter(tempFile)) {
            writer.write("# Essay on " + country + "\n\n" + essay);
        }
        InputStreamResource resource = new InputStreamResource(new FileInputStream(tempFile));
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + filename)
                .contentType(MediaType.parseMediaType("text/markdown"))
                .body(resource);
    }
}
