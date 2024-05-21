package com.enginex.processor;

import com.enginex.handler.IPCMessageHandler;
import com.enginex.model.Link;
import com.enginex.strategy.Strategy;

import java.util.List;

public interface JobProcessor {

    List<Strategy> generateStrategies(final List<Link> links) throws Exception;

    void setIpcMessageHandler(final IPCMessageHandler ipcMessageHandler);

}
