package com.enginex.processor.impl;

import com.enginex.processor.CleanupProcessor;

public class NoOpCleanupProcessorImpl implements CleanupProcessor {
    @Override
    public void cleanup(String directory) throws Exception {
        System.out.println("[INFO] Cleanup : directory = " + directory);
    }
}
