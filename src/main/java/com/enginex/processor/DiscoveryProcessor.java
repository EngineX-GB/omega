package com.enginex.processor;

import com.enginex.model.Link;

import java.util.List;

public interface DiscoveryProcessor {

    List<Link> discover(final List<Link> links);

}
