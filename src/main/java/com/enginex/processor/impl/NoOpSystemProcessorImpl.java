package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.model.StrategyType;
import com.enginex.processor.SystemProcessor;

import java.util.Arrays;
import java.util.List;

public class NoOpSystemProcessorImpl implements SystemProcessor {
    @Override
    public void createDirectory(String directory) throws Exception {
        System.out.println("[INFO] create: directory=" + directory);
    }

    @Override
    public List<Link> readInputFile(String directory) throws Exception {
        return Arrays.asList(new Link("http://video-link-1-seg-9-f1-v1-a1.ts", "file1", StrategyType.MULTI_FILE),
                new Link("http://video-link-2-seg-9-f1-v1-a1.ts", "file2", StrategyType.MULTI_FILE));
    }
}
