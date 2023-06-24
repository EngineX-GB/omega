package com.enginex.processor.impl;

import com.enginex.processor.DownloadProcessor;

import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;

public class DownloadProcessorImpl implements DownloadProcessor {

    @Override
    public Boolean download(String source, String target) throws IOException {
        URL url = new URL(source);
        ReadableByteChannel readableByteChannel = Channels.newChannel(url.openStream());
        FileOutputStream fileOutputStream = new FileOutputStream(target);
        FileChannel fileChannel = fileOutputStream.getChannel();
        fileOutputStream.getChannel()
                .transferFrom(readableByteChannel, 0, Long.MAX_VALUE);
        return true;
    }
}
