package com.enginex.runner;

import com.enginex.model.Request;

public interface ApplicationRunner {

    void run(Request request) throws Exception;

}
