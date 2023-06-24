package com.enginex.processor;

import com.enginex.model.Link;
import com.enginex.strategy.Strategy;

import java.util.List;

public interface JobProcessor {

    List<Strategy> generateSrategies(final List<Link> links) throws Exception;

}
