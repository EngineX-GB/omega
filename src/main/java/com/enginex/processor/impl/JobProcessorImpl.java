package com.enginex.processor.impl;

import com.enginex.handler.IPCMessageHandler;
import com.enginex.model.Link;
import com.enginex.model.StrategyType;
import com.enginex.processor.*;
import com.enginex.strategy.MultiFileStrategy;
import com.enginex.strategy.SingleFileStrategy;
import com.enginex.strategy.Strategy;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobProcessorImpl implements JobProcessor {

    private FileAggregationProcessor aggregationProcessor;

    private CleanupProcessor cleanupProcessor;

    private SystemProcessor systemProcessor;

    private DownloadProcessor downloadProcessor;

    private IPCMessageHandler ipcMessageHandler;

    public JobProcessorImpl(FileAggregationProcessor aggregationProcessor, CleanupProcessor cleanupProcessor, SystemProcessor systemProcessor,
                            DownloadProcessor downloadProcessor) {
            this.aggregationProcessor = aggregationProcessor;
            this.cleanupProcessor = cleanupProcessor;
            this.systemProcessor = systemProcessor;
            this.downloadProcessor = downloadProcessor;
    }

    @Override
    public List<Strategy> generateStrategies(final List<Link> links) throws Exception {
        final List<Strategy> strategyList = new ArrayList<>();
        for (final Link link : links) {
            if (link.getStrategyType() == StrategyType.MULTI_FILE) {
                strategyList.add(new MultiFileStrategy(link, System.getProperty("temp.path") + "/" + UUID.randomUUID(),
                        downloadProcessor, aggregationProcessor, cleanupProcessor, systemProcessor, ipcMessageHandler));
            } else {
                strategyList.add(new SingleFileStrategy(downloadProcessor, link.getUrl(), link.getFilename()));
            }
        }
        return strategyList;
    }

    @Override
    public void setIpcMessageHandler(IPCMessageHandler ipcMessageHandler) {
        this.ipcMessageHandler = ipcMessageHandler;
    }
}
