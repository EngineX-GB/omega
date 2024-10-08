package com.enginex.runner;

import com.enginex.model.JobRunnerMode;
import com.enginex.strategy.Strategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

public class JobRunnerImpl implements JobRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(JobRunnerImpl.class);
    final ExecutorService executorService;

    public JobRunnerImpl(final JobRunnerMode mode) {
        if (mode == JobRunnerMode.SINGLE) {
            this.executorService = Executors.newSingleThreadExecutor();
        } else {
            final Integer threadPoolSize = System.getProperty("thread.pool.size") != null ? Integer.valueOf(System.getProperty("thread.pool.size")) : 5;
            LOGGER.info("Thread pool size = {}", threadPoolSize);
            this.executorService = Executors.newFixedThreadPool(threadPoolSize);
        }
    }

    public void run(final List<Strategy> stragegyList)  {
        for (final Strategy strategy : stragegyList) {
            executorService.submit(() -> {
                try {
                    strategy.start();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            });
        }
        executorService.shutdown();
    }

    public void run(final Strategy strategy, final AtomicInteger counter) {
        executorService.submit(() -> {
            try {
                strategy.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
        counter.decrementAndGet();
        System.out.println("****** REMAINING COUNTER VALUE = " + counter + " ******");
        if (counter.get() == 0) {
            LOGGER.info("Counter is set to {}. Sending signal to shutdown the executor service in JobRunnerImpl", counter.get());
            stop();
        }
    }

    @Override
    public void stop() {
        if (executorService != null) {
            executorService.shutdown();
            System.out.println("shutdown = "+ executorService.isShutdown()+ ", terminated = "+ executorService.isTerminated());
        }
    }
}
