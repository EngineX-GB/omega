package com.enginex.runner.impl;

import com.enginex.model.*;
import com.enginex.processor.*;
import com.enginex.processor.impl.*;
import com.enginex.runner.ApplicationRunner;
import com.enginex.runner.JobRunner;
import com.enginex.runner.JobRunnerImpl;
import com.enginex.strategy.SingleStrategy;
import com.enginex.strategy.Strategy;
import com.enginex.util.AppUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ApplicationRunnerImpl implements ApplicationRunner {
    private static final SystemMode MODE = SystemMode.PRODUCTION;

    @Override
    public void run(Request request) throws Exception {
        final FileAggregationProcessor aggregationProcessor;
        final CleanupProcessor cleanupProcessor;
        final DownloadProcessor downloadProcessor;
        final SystemProcessor systemProcessor;
        final JobProcessor jobProcessor;
        final JobRunner jobRunner = new JobRunnerImpl(JobRunnerMode.SINGLE);

        if (MODE == SystemMode.PRODUCTION) {
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
        } else if (MODE == SystemMode.DEV){
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
            System.setProperty("temp.path", "C:/Users/rm_82/Desktop/omega/temp");
            System.setProperty("library.path", "C:/Users/rm_82/Desktop/omega/library");
            System.setProperty("ffmpeg.path", "C:/Users/rm_82/Desktop/ffmpeg-master-latest-win64-gpl/bin");
        } else {
            aggregationProcessor = new NoOpAggregationProcessorImpl();
            cleanupProcessor = new NoOpCleanupProcessorImpl();
            downloadProcessor = new NoOpDownloadProcessorImpl();
            systemProcessor = new NoOpSystemProcessorImpl();
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
            System.setProperty("temp.path", "C:/Users/rm_82/Desktop/omega/temp");
            System.setProperty("library.path", "C:/Users/rm_82/Desktop/omega/library");
            System.setProperty("ffmpeg.path", "C:/Users/rm_82/Desktop/ffmpeg-master-latest-win64-gpl/bin");
        }
        initialise();
        if (request.getOperation() == Operation.INTERACTIVE) {
            final Strategy singleStrategy = new SingleStrategy(request.getLink().getUrl(), System.getProperty("temp.path") + "/" + UUID.randomUUID(), request.getLink().getFilename(),  downloadProcessor, aggregationProcessor, cleanupProcessor, systemProcessor);
            jobRunner.run(Arrays.asList(singleStrategy));
        }
        else if (request.getOperation() == Operation.BATCH) {
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            final List<Strategy> strategyList = jobProcessor.generateSrategies(links);
            jobRunner.run(strategyList);
        }
        else if (request.getOperation() == Operation.VIEW_CONFIG) {
            System.out.println("[INFO] library.path: " + System.getProperty("library.path"));
            System.out.println("[INFO] temp.path : " + System.getProperty("temp.path"));
            System.exit(0);
        }
    }

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

}
