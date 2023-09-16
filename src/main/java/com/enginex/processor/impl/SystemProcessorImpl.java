package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.model.StrategyType;
import com.enginex.processor.SystemProcessor;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class SystemProcessorImpl implements SystemProcessor {
    @Override
    public void createDirectory(String directory) throws Exception{
        final Path dirpath = Paths.get(directory);
        if (Files.notExists(dirpath)) {
            Files.createDirectories(dirpath);
        }
    }

    @Override
    public List<Link> readInputFile(String filePath) throws Exception {
        final List<Link> links = new ArrayList<>();
        final Path path = Paths.get(filePath);
        if (Files.notExists(path) || Files.lines(path).count() == 0) {
            throw new Exception ("File : " + filePath + " does not exist or is empty");
        }
        Files.lines(path).forEach( s -> {
            String[] spl = s.split("\\|");
            links.add(new Link(spl[2], spl[0], StrategyType.valueOf(spl[1].toUpperCase())));
        });
        return links;
    }
}
