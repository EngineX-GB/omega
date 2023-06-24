package com.enginex.processor;

public interface FileAggregationProcessor {

    Boolean aggregate(String directory, String libraryDirectory, String filename) throws Exception;

}
