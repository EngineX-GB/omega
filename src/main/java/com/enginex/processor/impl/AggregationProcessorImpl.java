package com.enginex.processor.impl;

import com.enginex.model.ProcessResult;
import com.enginex.processor.FileAggregationProcessor;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AggregationProcessorImpl implements FileAggregationProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(AggregationProcessorImpl.class);
    @Override
    public Boolean aggregate(String directory, String libraryDirectory, String filename) throws Exception {
        LOGGER.info("Aggregating the data");
        generateManifestFile(directory);
        String ffMpegPath = System.getProperty("ffmpeg.path");
        String[] cmdArgs = {"cmd", "/c", ffMpegPath + "/ffmpeg.exe -nostats -loglevel 0 -f concat -i list.txt -c copy " + libraryDirectory + "/" + filename +".mp4"};
        final ProcessResult res = runProcess(cmdArgs, directory);
        return res.getReturnCode() == 0 ? true : false;
    }
    private ProcessResult runProcess(final String[] cmdArguments, String directory) throws Exception{
        final ProcessBuilder processBuilder = new ProcessBuilder();
        processBuilder.command(cmdArguments);
        processBuilder.directory(new File(directory));
        final Process process = processBuilder.start();
        final String processOutput = IOUtils.toString(process.getInputStream(), StandardCharsets.UTF_8);
        final int exitValue = process.waitFor();
        final String processMessage = "";
        if (exitValue == 1) {
            final String processError = IOUtils.toString(process.getErrorStream(), StandardCharsets.UTF_8);
            System.err.println(processError);
        }
        return new ProcessResult(exitValue, processMessage);
    }

    private void generateManifestFile(String directoryPath) throws Exception {
        Path directory = Paths.get(directoryPath);
        List<String> fileNames = new ArrayList<>();

        Files.walkFileTree(directory, new SimpleFileVisitor<>() {
            @Override
            public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) {
                fileNames.add("file " + file.getFileName().toString());
                return FileVisitResult.CONTINUE;
            }
        });

        Path listFilePath = directory.resolve(directoryPath + "/list.txt");
        Files.write(listFilePath, fileNames);
    }
}
