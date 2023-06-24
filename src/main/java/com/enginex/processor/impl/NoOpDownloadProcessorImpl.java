package com.enginex.processor.impl;

import com.enginex.processor.DownloadProcessor;

import java.io.IOException;

public class NoOpDownloadProcessorImpl implements DownloadProcessor {
    @Override
    public Boolean download(String source, String target) throws IOException {
        System.out.println("[INFO] Download : source=" + source + ", target= " + target);
        return true;
    }
}
