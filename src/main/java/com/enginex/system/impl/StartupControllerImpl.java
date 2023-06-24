package com.enginex.system.impl;

import com.enginex.system.StartupController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class StartupControllerImpl implements StartupController {
    @Override
    public void createTempDirectory(String filePath) throws IOException {
        Path tempDirectory = Paths.get(filePath);
        if (Files.notExists(tempDirectory)) {
            Files.createDirectories(tempDirectory);
        }
    }

    @Override
    public void createLibraryDirectory(String filePath) throws IOException {
        Path libraryDirectory = Paths.get(filePath);
        if (Files.notExists(libraryDirectory)) {
            Files.createDirectories(libraryDirectory);
        }
    }
}
