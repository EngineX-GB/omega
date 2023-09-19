package com.enginex.runner.impl;

import com.enginex.model.*;
import com.enginex.processor.*;
import com.enginex.processor.impl.*;
import com.enginex.runner.AdvancedJobRunnerImpl;
import com.enginex.runner.ApplicationRunner;
import com.enginex.runner.JobRunner;
import com.enginex.runner.JobRunnerImpl;
import com.enginex.service.DiscoveryService;
import com.enginex.service.impl.DiscoveryServiceImpl;
import com.enginex.strategy.SingleFileStrategy;
import com.enginex.strategy.MultiFileStrategy;
import com.enginex.strategy.Strategy;
import com.enginex.util.AppUtil;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ApplicationRunnerImpl implements ApplicationRunner {

    // to update manually when developing it. Revert to PRODUCTION once packaging a release
    private static final SystemMode MODE = SystemMode.PRODUCTION;

    @Override
    public void run(Request request) throws Exception {
        final FileAggregationProcessor aggregationProcessor;
        final CleanupProcessor cleanupProcessor;
        final DownloadProcessor downloadProcessor;
        final SystemProcessor systemProcessor;
        final JobProcessor jobProcessor;
        final DiscoveryService discoveryService;
        final DiscoveryProcessor discoveryProcessor;
        final JobRunner jobRunner = new JobRunnerImpl(JobRunnerMode.CONCURRENT);

        if (MODE == SystemMode.PRODUCTION) {
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            discoveryService = new DiscoveryServiceImpl();
            discoveryProcessor = new DiscoveryProcessorImpl(discoveryService);
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
        } else if (MODE == SystemMode.DEV){
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            discoveryService = new DiscoveryServiceImpl();
            discoveryProcessor = new DiscoveryProcessorImpl(discoveryService);
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
            System.setProperty("temp.path", System.getProperty("user.dir") + "/Desktop/omega/temp");
            System.setProperty("library.path", System.getProperty("user.dir") + "/Desktop/omega/library");
            System.setProperty("ffmpeg.path", System.getProperty("user.dir") + "/desktop/ffmpeg-master-latest-win64-gpl/bin");
        } else {
            aggregationProcessor = new NoOpAggregationProcessorImpl();
            cleanupProcessor = new NoOpCleanupProcessorImpl();
            downloadProcessor = new NoOpDownloadProcessorImpl();
            systemProcessor = new NoOpSystemProcessorImpl();
            discoveryService = null; // TODO: Add no-op implementation
            discoveryProcessor = null; //TODO: Add no-op implementation
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
            System.setProperty("temp.path", System.getProperty("user.dir") + "/Desktop/omega/temp");
            System.setProperty("library.path", System.getProperty("user.dir") + "/Desktop/omega/library");
            System.setProperty("ffmpeg.path", System.getProperty("user.dir") + "/desktop/ffmpeg-master-latest-win64-gpl/bin");
        }
        initialise();
        if (request.getOperation() == Operation.INTERACTIVE) {
            final Strategy strategy;
            if (request.getLink().getStrategyType() == StrategyType.SINGLE) {
                strategy = new SingleFileStrategy(downloadProcessor, request.getLink().getUrl(), request.getLink().getFilename());
            } else {
                strategy = new MultiFileStrategy(request.getLink().getUrl(), System.getProperty("temp.path") + "/" + UUID.randomUUID(), request.getLink().getFilename(), downloadProcessor, aggregationProcessor, cleanupProcessor, systemProcessor);
            }
            jobRunner.run(Arrays.asList(strategy));
        }
        else if (request.getOperation() == Operation.BATCH) {
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            final List<Strategy> strategyList = jobProcessor.generateStrategies(links);
            jobRunner.run(strategyList);
        }
        else if (request.getOperation() == Operation.VIEW_CONFIG) {
            System.out.println("[INFO] Mode : " + MODE);
            System.out.println("[INFO] library.path : " + System.getProperty("library.path"));
            System.out.println("[INFO] temp.path : " + System.getProperty("temp.path"));
            System.out.println("[INFO] ffmpeg.path : " + System.getProperty("ffmpeg.path"));
            System.exit(0);
        }
        else if (request.getOperation() == Operation.DISCOVER_AND_BATCH) {
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            final List<Link> resolvedLinks = discoveryProcessor.discover(links);
            final List<Strategy> strategyList = jobProcessor.generateStrategies(resolvedLinks);
            jobRunner.run(strategyList);
        }
        else if (request.getOperation() == Operation.CONCURRENT_DISCOVER_AND_BATCH) {
            //TODO: Experimental code
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            final AdvancedJobRunnerImpl advancedJobRunner = new AdvancedJobRunnerImpl(jobProcessor, jobRunner, links.size());
            advancedJobRunner.start();
            for (final Link discoveryLink : links) {
                final Link link = discoveryProcessor.discover(discoveryLink);
                if (link != null) {
                    advancedJobRunner.publish(link);
                }
            }
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
            System.err.println("[ERROR] ffmpeg location cannot be found. Path specified is : [" + System.getProperty("ffmpeg.path") + "]");
            System.exit(1);
        }
    }

}
