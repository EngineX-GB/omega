package com.enginex.runner.impl;

import com.enginex.handler.IPCMessageHandler;
import com.enginex.model.*;
import com.enginex.processor.*;
import com.enginex.processor.impl.*;
import com.enginex.runner.AdvancedJobRunnerImpl;
import com.enginex.runner.ApplicationRunner;
import com.enginex.runner.JobRunner;
import com.enginex.runner.JobRunnerImpl;
import com.enginex.service.AuditService;
import com.enginex.service.DiscoveryService;
import com.enginex.service.impl.AuditServiceImpl;
import com.enginex.service.impl.DiscoveryServiceImpl;
import com.enginex.strategy.SingleFileStrategy;
import com.enginex.strategy.MultiFileStrategy;
import com.enginex.strategy.Strategy;
import com.enginex.util.AppUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ApplicationRunnerImpl implements ApplicationRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(ApplicationRunnerImpl.class);

    // to update manually when developing it. Revert to PRODUCTION once packaging a release
    private static final SystemMode MODE = System.getProperty("system.mode") != null ? SystemMode.valueOf(System.getProperty("system.mode")) : SystemMode.DEV;

    @Override
    public void run(Request request) throws Exception {
        final FileAggregationProcessor aggregationProcessor;
        final CleanupProcessor cleanupProcessor;
        final DownloadProcessor downloadProcessor;
        final SystemProcessor systemProcessor;
        final JobProcessor jobProcessor;
        final DiscoveryService discoveryService;
        final AuditService auditService;
        final DiscoveryProcessor discoveryProcessor;
        final JobRunner jobRunner = new JobRunnerImpl(JobRunnerMode.CONCURRENT);

        if (MODE == SystemMode.PRODUCTION) {
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            discoveryService = new DiscoveryServiceImpl();
            auditService = new AuditServiceImpl();
            discoveryProcessor = new DiscoveryProcessorImpl(discoveryService, auditService);
            jobProcessor = new JobProcessorImpl(aggregationProcessor, cleanupProcessor, systemProcessor, downloadProcessor);
        } else if (MODE == SystemMode.DEV){
            aggregationProcessor = new AggregationProcessorImpl();
            cleanupProcessor = new CleanupProcessorImpl();
            downloadProcessor = new DownloadProcessorImpl();
            systemProcessor = new SystemProcessorImpl();
            discoveryService = new DiscoveryServiceImpl();
            auditService = new AuditServiceImpl();
            discoveryProcessor = new DiscoveryProcessorImpl(discoveryService, auditService);
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
            auditService = null;
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
                strategy = new MultiFileStrategy(request.getLink(), System.getProperty("temp.path") + "/" + UUID.randomUUID(), downloadProcessor, aggregationProcessor, cleanupProcessor, systemProcessor, null);
            }
            jobRunner.run(Arrays.asList(strategy));
        }
        else if (request.getOperation() == Operation.AUDIT) {
            int newLinkCounter = 0;
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            for (final Link link : links) {
                if (auditService.isDuplicate(link) != AuditResponseCode.DUPLICATE) {
                    LOGGER.info("New link : {} --> {}", link.getUrl(), link.getFilename());
                    newLinkCounter++;
                }
            }
            LOGGER.info("New links detected: {}", newLinkCounter);
        }
        else if (request.getOperation() == Operation.BATCH) {
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            final List<Strategy> strategyList = jobProcessor.generateStrategies(links);
            jobRunner.run(strategyList);
        }
        else if (request.getOperation() == Operation.VIEW_CONFIG) {
            LOGGER.info("Mode : " + MODE);
            LOGGER.info("library.path : " + System.getProperty("library.path"));
            LOGGER.info("temp.path : " + System.getProperty("temp.path"));
            LOGGER.info("ffmpeg.path : " + System.getProperty("ffmpeg.path"));
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
            LOGGER.info("Downloading {} links", links.size());
            final AdvancedJobRunnerImpl advancedJobRunner = new AdvancedJobRunnerImpl(jobProcessor, jobRunner, links.size());
            advancedJobRunner.start();
            for (final Link discoveryLink : links) {
                final Link link = discoveryProcessor.discover(discoveryLink);
                if (link != null) {
                    advancedJobRunner.publish(link);
                }
            }
        }
        else if (request.getOperation() == Operation.IPC) {
            //for the IPC operation, set the ipcMessageHandler to the jobProcessor
            final IPCMessageHandler ipcMessageHandler = new IPCMessageHandler();
            jobProcessor.setIpcMessageHandler(ipcMessageHandler);
            final AdvancedJobRunnerImpl advancedJobRunner = new AdvancedJobRunnerImpl(jobProcessor, jobRunner, -1);
            IPCSocketProcessor ipcSocketProcessor = new IPCSocketProcessorImpl(advancedJobRunner, discoveryProcessor, ipcMessageHandler);
            ipcSocketProcessor.execute();
            ipcSocketProcessor.startMessageDispatcher();
        }
        else if (request.getOperation() == Operation.EXPERIMENTAL) {
            LOGGER.warn("**** EXPERIMENTAL MODE ****");
            final List<Link> links = systemProcessor.readInputFile(request.getInputFilePath());
            final List<Link> nonDuplicatedLinks = new ArrayList<>();
            for (final Link discoveryLink : links) {
                final Link link = discoveryProcessor.verifyDuplicateLink(discoveryLink);
                if (link != null) {
                    nonDuplicatedLinks.add(link);
                }
            }
            if (nonDuplicatedLinks.size() > 0) {
                LOGGER.info("Downloading {} links", links.size());
                final AdvancedJobRunnerImpl advancedJobRunner = new AdvancedJobRunnerImpl(jobProcessor, jobRunner, nonDuplicatedLinks.size());
                Future<String> exec = advancedJobRunner.start();
                final ExecutorService executorService = Executors.newFixedThreadPool(5);
                List<Future<String>> futures = new ArrayList<>();
                for (final Link link : links) {
                    Future<String> result = executorService.submit(() -> {
                        if (link!= null) {
                            LOGGER.info("Perform discovery for link : {}", link.getFilename());
                            final Link discoveredLink = discoveryProcessor.discover(link);
                            if (discoveredLink != null) {
                                advancedJobRunner.publish(discoveredLink);
                            }
                        }
                        return "done";
                    });
                    futures.add(result);
                }
                for (Future<String> f : futures) {
                    System.out.println("Future result = "+f.get());
                }
                LOGGER.info("Finished discovery process thread.");
                executorService.shutdown();
            }
            else {
                LOGGER.warn("All links are considered duplicates. Exiting...");
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
            LOGGER.error("ffmpeg location cannot be found. Path specified is : [" + System.getProperty("ffmpeg.path") + "]");
            System.exit(1);
        }
    }

}
