package com.eduardo.apisystem.controller;

import jakarta.annotation.PostConstruct;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.bind.annotation.RestController;


import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;


@RestController
@RequestMapping("/api/images")
public class ImageUploadController {

    private final Path uploadDir = Paths.get("uploads");

    @PostConstruct
    public void init() throws IOException {
        if (!Files.exists(uploadDir)) {
            Files.createDirectories(uploadDir);
        }
    }

    @PostMapping("/upload")
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uniqueName = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            Path filePath = uploadDir.resolve(uniqueName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // Aqui usa sua porta real (8150)
            String imageUrl = "http://localhost:8150/api/images/download/" + URLEncoder.encode(uniqueName, StandardCharsets.UTF_8).replace("+", "%20")
                        ;

            Map<String, String> response = new HashMap<>();
            response.put("path", imageUrl); // <- frontend vai pegar isso
            response.put("uniqueName", uniqueName);

            return ResponseEntity.ok(response);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Erro ao salvar imagem"));
        }
    }

    @GetMapping("/download/{filename:.+}")
    public ResponseEntity<Resource> downloadImage(@PathVariable String filename) {
        try {
            Path file = uploadDir.resolve(filename);
            Resource resource = new UrlResource(file.toUri());

            if (!resource.exists()) {
                return ResponseEntity.notFound().build();
            }

            String contentType = Files.probeContentType(file);
            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType != null ? contentType : "application/octet-stream"))
                    .body(resource);

        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }
    }
}
