package com.enginex.processor.impl;

import com.enginex.handler.IPCMessageHandler;
import com.enginex.model.EnrichedLink;
import com.enginex.model.Link;
import com.enginex.processor.DiscoveryProcessor;
import com.enginex.processor.IPCSocketProcessor;
import com.enginex.runner.AdvancedJobRunnerImpl;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.*;

public class IPCSocketProcessorImpl implements IPCSocketProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(IPCSocketProcessorImpl.class);

    private static final ObjectMapper MAPPER = new ObjectMapper();

    private static ServerSocket serverSocket;

    private static final int PORT = 9876;

    private AdvancedJobRunnerImpl advancedJobRunner;

    private DiscoveryProcessor discoveryProcessor;

    private IPCMessageHandler ipcMessageHandler;

    public IPCSocketProcessorImpl(final AdvancedJobRunnerImpl advancedJobRunner, final DiscoveryProcessor discoveryProcessor,
                                  final IPCMessageHandler ipcMessageHandler) {
        this.advancedJobRunner = advancedJobRunner;
        this.discoveryProcessor = discoveryProcessor;
        this.ipcMessageHandler = ipcMessageHandler;
    }

    @Override
    public void execute() {
        advancedJobRunner.start();
        LOGGER.info("Running IPC Socket Processor Thread.");
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            try {
                serverSocket = new ServerSocket(PORT);
                while(true) {
                    final Socket socket = serverSocket.accept();
                    final InputStream is = socket.getInputStream();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String s = reader.readLine();
                    final Link link = getLink(s);
                    process(link);
                    onUpdate("Run URL check");
                    System.out.println(s);
                    reader.close();
                    is.close();
                    socket.close();
                }
            } catch(final Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        });

    }

    public void startMessageDispatcher() {
        LOGGER.info("Running message dispatcher");
        final BlockingQueue<String> messageQueue = ipcMessageHandler.getIpcMessageQueue();
        final ExecutorService executorService = Executors.newSingleThreadExecutor();
        executorService.submit(() -> {
            while(true) {
                if (messageQueue.size() > 0) {
                    final String message = messageQueue.poll();
                    onUpdate(message);
                }
            }
        });
    }

    private void onUpdate(final String message) {
        try {
            Socket socket = new Socket(InetAddress.getByName("127.0.0.1"), 9877);
            OutputStream outputStream = socket.getOutputStream();
            PrintWriter writer = new PrintWriter(outputStream, true);
            writer.println(message);
            System.out.println("Sent message to C# program: " + "Result");
            socket.close();
        } catch(Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
    }


    private Link getLink(final String message) throws JsonProcessingException {
        final EnrichedLink enrichedLink = MAPPER.readValue(message, EnrichedLink.class);
        return enrichedLink;
    }

    private void process(final Link discoveryLink) {
        final EnrichedLink updatedMessage = EnrichedLink.of(((EnrichedLink) discoveryLink), "DISCOVERING");
        try {
            final String json = MAPPER.writeValueAsString(updatedMessage);
            ipcMessageHandler.getIpcMessageQueue().put(json);
        } catch(Exception e) {
            e.printStackTrace();
        }
        final Link link = discoveryProcessor.discover(discoveryLink);
        final EnrichedLink enrichedLink = EnrichedLink.of(updatedMessage, link.getUrl(), "DISCOVERED");
        if (link != null) {
            advancedJobRunner.publish(enrichedLink);
        }
    }

}
