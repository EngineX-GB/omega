package com.enginex.strategy;

import com.enginex.model.Link;
import com.enginex.processor.DownloadProcessor;
import com.enginex.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDateTime;

public class SingleFileStrategy implements Strategy{

    private DownloadProcessor downloadProcessor;
    private String url;
    private String outputFileName;
    private Link link;

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileStrategy.class);


    public SingleFileStrategy(final Link link, final DownloadProcessor downloadProcessor, final String url, final String outputFileName) {
        this.link = link;
        this.downloadProcessor = downloadProcessor;
        this.url = url;
        this.outputFileName = outputFileName;
    }

    @Override
    public void start() throws Exception {
        final String libraryDirectory = System.getProperty("library.path");
        LOGGER.info("Start downloading [{}] : {}", link.getNumber(), link.getFilename());
        String extension = "";
        final int indexOfQueryMark = url.lastIndexOf('?');
        if (indexOfQueryMark != -1) {
            final String urlWithoutQueryString = url.substring(0, indexOfQueryMark + 1);
            final int indexOfPeriod = urlWithoutQueryString.lastIndexOf('.');
            if (indexOfPeriod != -1) {
                extension = urlWithoutQueryString.substring(indexOfPeriod, urlWithoutQueryString.length() -1);
            }
        }
        else {
            final int indexOfPeriod = url.lastIndexOf('.');
            if (indexOfPeriod != -1) {
                extension = url.substring(indexOfPeriod, url.length());
            }
        }
        downloadProcessor.download(url, getOutputFileName(libraryDirectory, outputFileName, extension));
        LOGGER.info("Finished downloading [{}] : {}", link.getNumber(), link.getFilename());
    }

    private String getOutputFileName(final String libraryDirectory, final String outputFileName, final String extension) {
        final String fullFilePath = libraryDirectory.concat("/").concat(outputFileName).concat(extension);
        if (Files.exists(Paths.get(fullFilePath))) {
            final String revisedOutputFileName = outputFileName.concat("-")
                    .concat(LocalDateTime.now().format(AppUtil.getDateTimeFormatter()).replace(":", "_"));
            return libraryDirectory.concat("/").concat(revisedOutputFileName).concat(extension);
        }
        return fullFilePath;
    }

}
