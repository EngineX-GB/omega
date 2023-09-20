package com.enginex.processor.impl;

import com.enginex.processor.CleanupProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpCleanupProcessorImpl implements CleanupProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpCleanupProcessorImpl.class);
    @Override
    public void cleanup(String directory) throws Exception {
        LOGGER.info("Cleanup : directory = " + directory);
    }
}
