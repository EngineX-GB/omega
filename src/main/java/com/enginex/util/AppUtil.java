package com.enginex.util;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class AppUtil {

    private AppUtil() {}

    public static void createDir(final String dir) throws IOException {
        Path path = Paths.get(dir);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }

}
