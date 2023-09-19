package com.enginex.runner;

import com.enginex.strategy.Strategy;

import java.util.List;

public interface JobRunner {

    void run (List<Strategy> strategyList);

    void run (Strategy strategy);

    void stop();

}
