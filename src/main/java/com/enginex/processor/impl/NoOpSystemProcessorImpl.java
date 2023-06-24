package com.enginex.processor.impl;

import com.enginex.processor.SystemProcessor;

public class NoOpSystemProcessorImpl implements SystemProcessor {
    @Override
    public void createDirectory(String directory) throws Exception {
        System.out.println("[INFO] create: directory=" + directory);
    }
}
