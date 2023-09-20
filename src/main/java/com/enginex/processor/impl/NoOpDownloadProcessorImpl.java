package com.enginex.processor.impl;

import com.enginex.processor.DownloadProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class NoOpDownloadProcessorImpl implements DownloadProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpDownloadProcessorImpl.class);

    @Override
    public Boolean download(String source, String target) throws IOException {
        LOGGER.info("Download : source=" + source + ", target= " + target);
        return true;
    }
}
