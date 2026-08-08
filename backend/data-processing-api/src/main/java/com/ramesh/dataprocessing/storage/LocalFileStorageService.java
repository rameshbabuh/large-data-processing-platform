package com.ramesh.dataprocessing.storage;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.Objects;

@Service
public class LocalFileStorageService {

    private final Path uploadDir = Path.of("uploads");

    public LocalFileStorageService() throws IOException {
        Files.createDirectories(uploadDir);
    }

    public Path store(MultipartFile file) throws IOException {
        Path target = uploadDir.resolve(Objects.requireNonNull(file.getOriginalFilename()));

        Files.copy(
                file.getInputStream(),
                target,
                StandardCopyOption.REPLACE_EXISTING
        );

        return target;
    }
}