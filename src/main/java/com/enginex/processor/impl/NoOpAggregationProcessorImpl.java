package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.processor.FileAggregationProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NoOpAggregationProcessorImpl implements FileAggregationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpAggregationProcessorImpl.class);
    @Override
    public Boolean aggregate(String directory, String libraryDirectory, Link link) throws Exception {
        LOGGER.info("Aggregation : Directory = " + directory + ", libraryDirectory = " + libraryDirectory + ", filename = " + link.getFilename());
        return true;
    }
}
