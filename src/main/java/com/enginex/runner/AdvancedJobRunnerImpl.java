package com.enginex.runner;

import com.enginex.model.Link;
import com.enginex.processor.JobProcessor;
import com.enginex.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class AdvancedJobRunnerImpl {

    private static final Logger LOGGER = LoggerFactory.getLogger(AdvancedJobRunnerImpl.class);

    private BlockingQueue<Link> queue = new ArrayBlockingQueue<>(100);

    private ExecutorService executorService;

    private JobProcessor jobProcessor;

    private JobRunner jobRunner;

    private int jobsToProcess;

    public AdvancedJobRunnerImpl(final JobProcessor jobProcessor, final JobRunner jobRunner, final int jobsToProcess) {
        executorService = Executors.newSingleThreadExecutor();
        this.jobProcessor = jobProcessor;
        this.jobRunner = jobRunner;
        this.jobsToProcess = jobsToProcess;
    }

    public void publish (final Link link) {
        try {
            queue.put(link);
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public Future<String> start() {
        Future<String> exec = executorService.submit(new Callable() {
            @Override
            public String call() {
                final AtomicInteger counter = new AtomicInteger(jobsToProcess);
                LOGGER.info("Running consumer thread for Advanced Job Runner");
                // if the jobsToProcess is set to -1, then this needs to run infinitely to process new links via the sockets.
                // otherwise if the jobsToProcesss is set to a number greater than 1 (i.e. the number of links), then it processes
                // the links as normal and then stops when the jobsToProcess is set to 0.
                boolean infiniteRun = jobsToProcess <= -1 ? true : false;
                while(infiniteRun || jobsToProcess > 0) {
                    if (queue.size() > 0) {
                        try {
                            final Link link = queue.poll();
                            final List<Strategy> strategyList = jobProcessor.generateStrategies(Arrays.asList(link));
                            jobRunner.run(strategyList.get(0), counter);
                            jobsToProcess--;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
                LOGGER.info("Completed all tasks....");
                executorService.shutdown();
                return "done";
            }
        });
        return exec;
    }

    public void stop() {
        if (jobRunner != null) {
            jobRunner.stop();
        }
        if (executorService != null) {
            executorService.shutdown();
        }
    }

    public void stopRunner() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }

}
