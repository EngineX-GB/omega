package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.processor.CleanupProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class CleanupProcessorImpl implements CleanupProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(CleanupProcessor.class);

    @Override
    public void cleanup(String dir, Link link) throws Exception {
        Path directory = Paths.get(dir);
        LOGGER.info("Cleaning up the temp files [{}]", link.getNumber());
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
