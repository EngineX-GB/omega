package com.enginex;

import com.enginex.processor.CleanupProcessor;
import com.enginex.processor.DownloadProcessor;
import com.enginex.processor.FileAggregationProcessor;
import com.enginex.processor.impl.AggregationProcessorImpl;
import com.enginex.processor.impl.CleanupProcessorImpl;
import com.enginex.processor.impl.DownloadProcessorImpl;
import com.enginex.strategy.SingleStrategy;
import com.enginex.strategy.Strategy;
import com.enginex.util.AppUtil;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

public class Main {

    private static void initialise() throws Exception{
        if (System.getProperty("library.path") != null) {
            AppUtil.createDir(System.getProperty("library.path"));
        }
        if (System.getProperty("temp.path") != null) {
            AppUtil.createDir(System.getProperty("temp.path"));
        }
        if (System.getProperty("ffmpeg.path") == null || Files.notExists(Paths.get(System.getProperty("ffmpeg.path")))) {
            System.err.println("[ERROR] ffmpeg location cannot be found");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        initialise();
        String url = "", filename = "";
        final String tempPath = System.getProperty("temp.path");
        final String inputType = args[0];
        if (inputType.equals("-i")) {
            url = args[1];
            filename = args[2];
        }
        else if (inputType.equals("-t")) {
            System.out.println("[INFO] library.path: " + System.getProperty("library.path"));
            System.out.println("[INFO] temp.path : " + System.getProperty("temp.path"));
            System.exit(0);
        } else {
            System.err.println("[ERROR] Unsupported operation.");
            System.exit(1);
        }

        final FileAggregationProcessor aggregationProcessor = new AggregationProcessorImpl();
        final CleanupProcessor cleanupProcessor = new CleanupProcessorImpl();
        final DownloadProcessor downloadProcessor = new DownloadProcessorImpl();

        Strategy singleStrategy = new SingleStrategy(url, tempPath + "/" + UUID.randomUUID(), filename,  downloadProcessor, aggregationProcessor, cleanupProcessor);
        singleStrategy.start();

    }
}