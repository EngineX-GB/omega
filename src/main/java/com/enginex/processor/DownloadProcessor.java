package com.enginex.processor;

import java.io.IOException;
import java.net.MalformedURLException;

public interface DownloadProcessor {

    Boolean download (String source, String target) throws IOException;

}
