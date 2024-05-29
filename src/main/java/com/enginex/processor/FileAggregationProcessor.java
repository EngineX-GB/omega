package com.enginex.processor;

import com.enginex.model.Link;

public interface FileAggregationProcessor {

    Boolean aggregate(String directory, String libraryDirectory, Link link) throws Exception;

}
