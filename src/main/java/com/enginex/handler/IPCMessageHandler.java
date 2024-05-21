package com.enginex.handler;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class IPCMessageHandler {

    private final BlockingQueue<String> ipcMessageQueue;

    public IPCMessageHandler() {
        this.ipcMessageQueue = new ArrayBlockingQueue<>(100);
    }

    public BlockingQueue<String> getIpcMessageQueue() {
        return ipcMessageQueue;
    }

}
