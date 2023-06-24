package com.enginex.processor.impl;

import com.enginex.processor.CleanupProcessor;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CleanupProcessorImpl implements CleanupProcessor {

    @Override
    public void cleanup(String dir) throws Exception {
        Path directory = Paths.get(dir);
        System.out.println("[INFO] Cleaning up the temp files");
        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                Files.delete(file);
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                Files.delete(dir);
                return FileVisitResult.CONTINUE;
            }
        });
    }
}
