package com.example.elijah_backend.controller;

import com.example.elijah_backend.model.Option;
import com.example.elijah_backend.repository.OptionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Optional;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/options")
public class OptionController {

    private static final String IMAGE_UPLOAD_DIR = "uploads/";

    @PostMapping("/upload")
    public ResponseEntity<String> uploadImage(@RequestParam("file") MultipartFile file) throws IOException {
        // Create the directory if it doesn't exist
        File directory = new File(IMAGE_UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file to the server
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        // Return the full URL for accessing the image
        String fileUrl = "http://10.0.2.2:8080/uploads/" + fileName;
        return ResponseEntity.ok("Image uploaded successfully");
    }

    @PostMapping("/create-with-image")
    public ResponseEntity<Option> createOptionWithImage(
            @RequestParam("name") String name,
            @RequestParam("category") String category,
            @RequestParam("file") MultipartFile file) throws IOException {

        // Save the image
        String imagePath = saveImage(file);

        // Create a new option and set its properties
        Option option = new Option();
        option.setName(name);
        option.setImagePath(imagePath);  // Save the path of the uploaded image

        // Save the option in the database
        Option savedOption = optionRepository.save(option);

        return ResponseEntity.ok(savedOption);
    }

    private String saveImage(MultipartFile file) throws IOException {
        // Create the directory if it doesn't exist
        File directory = new File(IMAGE_UPLOAD_DIR);
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Save the file to the server
        String fileName = file.getOriginalFilename();
        Path filePath = Paths.get(IMAGE_UPLOAD_DIR + fileName);
        Files.write(filePath, file.getBytes());

        return fileName;  // Return the file name as the image path
    }

    @Autowired
    private OptionRepository optionRepository;

    // Get all options
    @GetMapping
    public List<Option> getAllOptions() {
        return optionRepository.findAll();
    }

    // Get an option by ID
    @GetMapping("/{id}")
    public Optional<Option> getOptionById(@PathVariable Long id) {
        return optionRepository.findById(id);
    }

    // Create a new option
    @PostMapping
    public Option createOption(@RequestBody Option option) {
        return optionRepository.save(option);
    }

    // Update an existing option
    @PutMapping("/{id}")
    public Option updateOption(@PathVariable Long id, @RequestBody Option optionDetails) {
        return optionRepository.findById(id).map(option -> {
            option.setName(optionDetails.getName());
            option.setImagePath(optionDetails.getImagePath());
            option.setPrimaryCategory(optionDetails.getPrimaryCategory()); // Update the category
            return optionRepository.save(option);
        }).orElseThrow(() -> new RuntimeException("Option not found with id " + id));
    }

    // Delete an option
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteOption(@PathVariable Long id) {
        Option option = optionRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Option not found with id: " + id));

        optionRepository.delete(option);
        return ResponseEntity.noContent().build();
    }
}