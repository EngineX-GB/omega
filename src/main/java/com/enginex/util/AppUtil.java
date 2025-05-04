package com.enginex.util;

import com.enginex.handler.IPCMessageHandler;
import com.enginex.model.EnrichedLink;
import com.enginex.model.Link;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.format.DateTimeFormatter;

public final class AppUtil {

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private AppUtil() {}

    public static void createDir(final String dir) throws IOException {
        Path path = Paths.get(dir);
        if (Files.notExists(path)) {
            Files.createDirectories(path);
        }
    }

    public static void dispatchMessage(final IPCMessageHandler ipcMessageHandler, final Link link, final String updatedStatus) {
        try {
            if (ipcMessageHandler != null && link instanceof EnrichedLink) {
                EnrichedLink updatedLinkStatus = EnrichedLink.of((EnrichedLink) link, updatedStatus);
                ipcMessageHandler.getIpcMessageQueue().put(MAPPER.writeValueAsString(updatedLinkStatus));
            }
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    public static DateTimeFormatter getDateTimeFormatter() {
        return DateTimeFormatter.ofPattern("HH:mm:ss:SSS");
    }

}
