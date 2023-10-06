package com.enginex.runner;

import com.enginex.model.JobRunnerMode;
import com.enginex.strategy.Strategy;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class JobRunnerImpl implements JobRunner {

    final ExecutorService executorService;

    public JobRunnerImpl(final JobRunnerMode mode) {
        if (mode == JobRunnerMode.SINGLE) {
            this.executorService = Executors.newSingleThreadExecutor();
        } else {
            this.executorService = Executors.newFixedThreadPool(5);
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

    public void run(final Strategy strategy) {
        executorService.submit(() -> {
            try {
                strategy.start();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public void stop() {
        if (executorService != null) {
            executorService.shutdown();
        }
    }
}
