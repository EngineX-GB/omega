package com.enginex.strategy;

import com.enginex.processor.DownloadProcessor;

public class MultiFileStrategy implements Strategy{

    private DownloadProcessor downloadProcessor;
    private String url;
    private String outputFileName;

    private static final String SINGLE_FILE_EXTENSION = ".mp4";

    public MultiFileStrategy(final DownloadProcessor downloadProcessor, final String url, final String outputFileName) {
        this.downloadProcessor = downloadProcessor;
        this.url = url;
        this.outputFileName = outputFileName;
    }

    @Override
    public void start() throws Exception {
        final String libraryDirectory = System.getProperty("library.path");
        downloadProcessor.download(url, libraryDirectory.concat("/").concat(outputFileName).concat(SINGLE_FILE_EXTENSION));
    }

}
