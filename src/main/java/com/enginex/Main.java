package com.enginex;

import com.enginex.model.JobRunnerMode;
import com.enginex.model.SystemMode;
import com.enginex.processor.CleanupProcessor;
import com.enginex.processor.DownloadProcessor;
import com.enginex.processor.FileAggregationProcessor;
import com.enginex.processor.SystemProcessor;
import com.enginex.processor.impl.*;
import com.enginex.runner.JobRunner;
import com.enginex.runner.JobRunnerImpl;
import com.enginex.strategy.SingleStrategy;
import com.enginex.strategy.Strategy;
import com.enginex.util.AppUtil;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.UUID;

public class Main {

    private static final SystemMode MODE = SystemMode.TEST;

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

        final FileAggregationProcessor aggregationProcessor;
        final CleanupProcessor cleanupProcessor;
        final DownloadProcessor downloadProcessor;
        final SystemProcessor systemProcessor;
        final JobRunner jobRunner = new JobRunnerImpl(JobRunnerMode.SINGLE);

        if (MODE == SystemMode.PRODUCTION) {
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
        } else if (MODE == SystemMode.DEV){
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            System.setProperty("temp.path", "C:/Users/rm_82/Desktop/omega/temp");
            System.setProperty("library.path", "C:/Users/rm_82/Desktop/omega/library");
            System.setProperty("ffmpeg.path", "C:/Users/rm_82/Desktop/ffmpeg-master-latest-win64-gpl/bin");
        } else {
            aggregationProcessor = new NoOpAggregationProcessorImpl();
            cleanupProcessor = new NoOpCleanupProcessorImpl();
            downloadProcessor = new NoOpDownloadProcessorImpl();
            systemProcessor = new NoOpSystemProcessorImpl();
            System.setProperty("temp.path", "C:/Users/rm_82/Desktop/omega/temp");
            System.setProperty("library.path", "C:/Users/rm_82/Desktop/omega/library");
            System.setProperty("ffmpeg.path", "C:/Users/rm_82/Desktop/ffmpeg-master-latest-win64-gpl/bin");
        }

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


        Strategy singleStrategy = new SingleStrategy(url, tempPath + "/" + UUID.randomUUID(), filename,  downloadProcessor, aggregationProcessor, cleanupProcessor, systemProcessor);
        jobRunner.run(Arrays.asList(singleStrategy));

    }
}