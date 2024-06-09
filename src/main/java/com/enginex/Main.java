package com.enginex;

import com.enginex.model.Link;
import com.enginex.model.Operation;
import com.enginex.model.Request;
import com.enginex.model.StrategyType;
import com.enginex.runner.ApplicationRunner;
import com.enginex.runner.impl.ApplicationRunnerImpl;

public class Main {

    public static void main(String[] args) throws Exception {
        final ApplicationRunner applicationRunner = new ApplicationRunnerImpl();
        applicationRunner.run(prepareRequest(args));
    }

    private static final Request prepareRequest(final String[] args) throws Exception {
        final Request request = new Request();
        switch(args[0]) {
            case "-i":
                request.setOperation(Operation.INTERACTIVE);
                request.setLink(new Link(1, args[1], args[2], StrategyType.valueOf(args[3].toUpperCase())));
                break;
            case "-t":
                request.setOperation(Operation.VIEW_CONFIG);
                break;
            case "-b":
                request.setOperation(Operation.BATCH);
                request.setInputFilePath(args[1]);
                break;
            case "-d":
                request.setOperation(Operation.DISCOVER_AND_BATCH);
                request.setInputFilePath(args[1]);
                break;
            case "-c":
                request.setOperation(Operation.CONCURRENT_DISCOVER_AND_BATCH);
                request.setInputFilePath(args[1]);
                break;
            case "-e":
                request.setOperation(Operation.EXPERIMENTAL);
                request.setInputFilePath(args[1]);
                break;
            case "-a":
                request.setOperation(Operation.AUDIT);
                request.setInputFilePath(args[1]);
                break;
            case "-g":
                request.setOperation(Operation.IPC);
                break;
            default:
                throw new Exception(String.format("Unknown Operation : %s", args[0]));
        }
        return request;
    }
}