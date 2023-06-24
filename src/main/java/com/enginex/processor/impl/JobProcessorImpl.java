package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.processor.*;
import com.enginex.strategy.SingleStrategy;
import com.enginex.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobProcessorImpl implements JobProcessor {

    private FileAggregationProcessor aggregationProcessor;

    private CleanupProcessor cleanupProcessor;

    private SystemProcessor systemProcessor;

    private DownloadProcessor downloadProcessor;

    public JobProcessorImpl(FileAggregationProcessor aggregationProcessor, CleanupProcessor cleanupProcessor, SystemProcessor systemProcessor,
                            DownloadProcessor downloadProcessor) {
            this.aggregationProcessor = aggregationProcessor;
            this.cleanupProcessor = cleanupProcessor;
            this.systemProcessor = systemProcessor;
            this.downloadProcessor = downloadProcessor;
    }

    @Override
    public List<Strategy> generateSrategies(final List<Link> links) throws Exception {
        final List<Strategy> strategyList = new ArrayList<>();
        for (final Link link : links) {
            strategyList.add(new SingleStrategy(link.getUrl(), System.getProperty("temp.path") + "/" + UUID.randomUUID(), link.getFilename(),
                    downloadProcessor, aggregationProcessor, cleanupProcessor, systemProcessor));
        }
        return strategyList;
    }
}
