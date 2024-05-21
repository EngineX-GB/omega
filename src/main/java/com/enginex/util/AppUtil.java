package com.enginex.util;

import com.enginex.handler.IPCMessageHandler;

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

    public static void dispatchMessage(final IPCMessageHandler ipcMessageHandler, final String message) {
        try {
            if (ipcMessageHandler != null) {
                ipcMessageHandler.getIpcMessageQueue().put(message);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

}
