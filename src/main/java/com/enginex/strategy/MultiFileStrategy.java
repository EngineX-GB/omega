package com.enginex.strategy;

import com.enginex.handler.IPCMessageHandler;
import com.enginex.model.Link;
import com.enginex.processor.CleanupProcessor;
import com.enginex.processor.DownloadProcessor;
import com.enginex.processor.FileAggregationProcessor;
import com.enginex.processor.SystemProcessor;
import com.enginex.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiFileStrategy implements Strategy {

    static final Integer MAX_FILE_SIZE = 2000;

    final String directory;
    private final DownloadProcessor downloadProcessor;
    private final FileAggregationProcessor aggregationProcessor;
    private final CleanupProcessor cleanupProcessor;
    private final SystemProcessor systemProcessor;
    private final IPCMessageHandler ipcMessageHandler;

    private Link link;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiFileStrategy.class);

    static final Pattern pattern = Pattern.compile("seg(\\-[0-9]+\\-)[a-z0-9\\-]+\\.(ts|m4s)");
    public MultiFileStrategy(final Link link, String directory, final DownloadProcessor downloadProcessor,
                             final FileAggregationProcessor aggregationProcessor,
                             final CleanupProcessor cleanupProcessor, final SystemProcessor systemProcessor,
                             final IPCMessageHandler ipcMessageHandler) {
        this.link = link;
        this.directory = directory;
        this.downloadProcessor = downloadProcessor;
        this.aggregationProcessor = aggregationProcessor;
        this.cleanupProcessor = cleanupProcessor;
        this.systemProcessor = systemProcessor;
        this.ipcMessageHandler = ipcMessageHandler;
    }

    @Override
    public void start() throws Exception {
        final String libraryDirectory = System.getProperty("library.path");
        systemProcessor.createDirectory(directory);
        Matcher matcher = pattern.matcher(link.getUrl());
        if (matcher.find()) {
            LOGGER.info("Start downloading [{}] : {}", link.getNumber(), link.getFilename());
            AppUtil.dispatchMessage(ipcMessageHandler, link, "DOWNLOADING");
            final String templateFilename = matcher.group(0).replace(matcher.group(1), "-{d}-");
            final String fileExtension = checkFileExtension(matcher.group(2));
            final String templateUrl = link.getUrl().replace(matcher.group(0), templateFilename);
            // now download the files in numerical order
            for (int i = 1; i < MAX_FILE_SIZE; i++ ) {
                try {
                    downloadProcessor.download(templateUrl.replace("{d}", String.valueOf(i)), generateOutputFileName(directory, i, fileExtension));
                } catch (IOException e) {
                    LOGGER.warn(e.getMessage() +". Breaking loop");
                    break;
                }
            }
            AppUtil.dispatchMessage(ipcMessageHandler, link, "AGGREGATING");
            Boolean aggregationResult = aggregationProcessor.aggregate(directory, libraryDirectory, link);
            if (aggregationResult) {
                AppUtil.dispatchMessage(ipcMessageHandler, link, "FINALISING");
                cleanupProcessor.cleanup(directory, link);
            }
        }
    }

    private String generateOutputFileName(final String directory, final int clipNumber, final String fileExtension) {
        return new StringBuffer().append(directory).append("/").append(numericalOutputLabelling(clipNumber)).append(clipNumber).append(fileExtension).toString();
    }

    private String checkFileExtension(final String fileExtension) {
        if (fileExtension == null) {
            LOGGER.warn("File Extension is null. Defaulting the extension to be .ts");
            return ".ts";
        }
        return "." + fileExtension;
    }

    private String numericalOutputLabelling(int clipNumber) {
        if (clipNumber < 10) {
            return "0000";
        }
        if (clipNumber < 100) {
            return "000";
        }
        if (clipNumber < 1000) {
            return "00";
        }
        return "0";
    }
}
