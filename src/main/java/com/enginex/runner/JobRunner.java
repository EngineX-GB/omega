package com.enginex.runner;

import com.enginex.strategy.Strategy;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public interface JobRunner {

    void run (List<Strategy> strategyList);

    void run (Strategy strategy, AtomicInteger counter);

    void stop();

}
