package com.enginex.system;

import java.io.IOException;

public interface StartupController {

    void createTempDirectory(final String filePath) throws IOException;

    void createLibraryDirectory(final String filePath) throws IOException;

}
