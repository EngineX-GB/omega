package com.enginex.strategy;

import com.enginex.processor.CleanupProcessor;
import com.enginex.processor.DownloadProcessor;
import com.enginex.processor.FileAggregationProcessor;
import com.enginex.processor.SystemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MultiFileStrategy implements Strategy {

    static final Integer MAX_FILE_SIZE = 2000;

    final String url;
    final String directory;
    private final DownloadProcessor downloadProcessor;
    private final FileAggregationProcessor aggregationProcessor;
    private final CleanupProcessor cleanupProcessor;
    private final SystemProcessor systemProcessor;

    private String filename;

    private static final Logger LOGGER = LoggerFactory.getLogger(MultiFileStrategy.class);

    static final Pattern pattern = Pattern.compile("seg(\\-[0-9]+\\-)[a-z0-9\\-]+\\.ts");
    public MultiFileStrategy(final String url, String directory, String filename, final DownloadProcessor downloadProcessor,
                             final FileAggregationProcessor aggregationProcessor,
                             final CleanupProcessor cleanupProcessor, final SystemProcessor systemProcessor) {
        this.url = url;
        this.directory = directory;
        this.filename = filename;
        this.downloadProcessor = downloadProcessor;
        this.aggregationProcessor = aggregationProcessor;
        this.cleanupProcessor = cleanupProcessor;
        this.systemProcessor = systemProcessor;
    }

    @Override
    public void start() throws Exception {
        final String libraryDirectory = System.getProperty("library.path");
        systemProcessor.createDirectory(directory);
        Matcher matcher = pattern.matcher(url);
        if (matcher.find()) {
            LOGGER.info("Start downloading : " + filename);
            final String templateFilename = matcher.group(0).replace(matcher.group(1), "-{d}-");
            final String templateUrl = url.replace(matcher.group(0), templateFilename);
            // now download the files in numerical order
            for (int i = 1; i < MAX_FILE_SIZE; i++ ) {
                try {
                    downloadProcessor.download(templateUrl.replace("{d}", String.valueOf(i)), generateOutputFileName(directory, i));
                } catch (IOException e) {
                    LOGGER.warn(e.getMessage() +". Breaking loop");
                    break;
                }
            }
            Boolean aggregationResult = aggregationProcessor.aggregate(directory, libraryDirectory, filename);
            if (aggregationResult) {
                cleanupProcessor.cleanup(directory);
            }
        }
    }

    private String generateOutputFileName(final String directory, final int clipNumber) {
        return new StringBuffer().append(directory).append("/").append(numericalOutputLabelling(clipNumber)).append(clipNumber).append(".ts").toString();
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
