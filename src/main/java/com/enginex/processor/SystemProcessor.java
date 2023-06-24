package com.enginex.processor;

import com.enginex.model.Link;

import java.util.List;

public interface SystemProcessor {

    void createDirectory (String directory) throws Exception;

    List<Link> readInputFile (String directory) throws Exception;

}
