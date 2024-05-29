package com.enginex.processor;

import com.enginex.model.Link;

public interface CleanupProcessor {

    void cleanup (String directory, Link link) throws Exception;

}
