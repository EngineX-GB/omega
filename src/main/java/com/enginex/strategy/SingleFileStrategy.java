package com.enginex.strategy;

import com.enginex.processor.DownloadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingleFileStrategy implements Strategy{

    private DownloadProcessor downloadProcessor;
    private String url;
    private String outputFileName;

    private static final Logger LOGGER = LoggerFactory.getLogger(SingleFileStrategy.class);


    public SingleFileStrategy(final DownloadProcessor downloadProcessor, final String url, final String outputFileName) {
        this.downloadProcessor = downloadProcessor;
        this.url = url;
        this.outputFileName = outputFileName;
    }

    @Override
    public void start() throws Exception {
        final String libraryDirectory = System.getProperty("library.path");
        LOGGER.info("Start downloading : " + outputFileName);
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
                extension = url.substring(indexOfPeriod, url.length() -1);
            }
        }
        downloadProcessor.download(url, libraryDirectory.concat("/").concat(outputFileName).concat(extension));
    }

}
