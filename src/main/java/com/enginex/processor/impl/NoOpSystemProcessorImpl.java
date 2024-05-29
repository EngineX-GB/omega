package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.model.StrategyType;
import com.enginex.processor.SystemProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;

public class NoOpSystemProcessorImpl implements SystemProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(NoOpSystemProcessorImpl.class);
    @Override
    public void createDirectory(String directory) throws Exception {
        LOGGER.info("create: directory=" + directory);
    }

    @Override
    public List<Link> readInputFile(String directory) throws Exception {
        return Arrays.asList(new Link(1, "http://video-link-1-seg-9-f1-v1-a1.ts", "file1", StrategyType.MULTI_FILE),
                new Link(2, "http://video-link-2-seg-9-f1-v1-a1.ts", "file2", StrategyType.MULTI_FILE));
    }
}
