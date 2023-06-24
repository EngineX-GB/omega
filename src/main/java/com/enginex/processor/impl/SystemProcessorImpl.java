package com.enginex.processor.impl;

import com.enginex.processor.SystemProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class SystemProcessorImpl implements SystemProcessor {
    @Override
    public void createDirectory(String directory) throws Exception{
        final Path dirpath = Paths.get(directory);
        if (Files.notExists(dirpath)) {
            Files.createDirectories(dirpath);
        }
    }
}
