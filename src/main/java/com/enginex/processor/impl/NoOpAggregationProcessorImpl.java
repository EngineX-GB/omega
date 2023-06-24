package com.enginex.processor.impl;

import com.enginex.processor.FileAggregationProcessor;

public class NoOpAggregationProcessorImpl implements FileAggregationProcessor {
    @Override
    public Boolean aggregate(String directory, String libraryDirectory, String filename) throws Exception {
        System.out.println("[INFO] Aggregation : Directory = " + directory + ", libraryDirectory = " + libraryDirectory + ", filename = " + filename);
        return true;
    }
}
