package com.enginex.processor.impl;

import com.enginex.model.Link;
import com.enginex.processor.DiscoveryProcessor;
import com.enginex.service.DiscoveryService;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DiscoveryProcessorImpl implements DiscoveryProcessor {

    private DiscoveryService discoveryService;

    public DiscoveryProcessorImpl(final DiscoveryService discoveryService) {
        this.discoveryService = discoveryService;
    }

    @Override
    public List<Link> discover(List<Link> links) {
        final List<Link> newLinks = new LinkedList<>();
        for (final Link link : links) {
            final Optional<String> url = retrieveUrl(link);
            if (url.isPresent()){
                newLinks.add(new Link(url.get(), link.getFilename()));
            }
        }
        return newLinks;
    }


    private Optional<String> retrieveUrl (final Link link) {
        int numberOfRetries = 0, maxRetries = 10;
        while (numberOfRetries <= maxRetries) {
            String url = discoveryService.getResourceUtl(link.getUrl());
            if (!url.equals("NULL_LINK_RETURNED")) {
                //contains the link
                if (!url.contains("-f1-")) {
                    url = url.replaceAll("-f[0-9]-", "-f1-"); // check this via a test
                }
                return Optional.of(url);
            }
            numberOfRetries++;
            System.out.println("[INFO] Retrying " + numberOfRetries + " time(s) to get resolved link for " + link.getUrl());
        }
        return Optional.empty();
    }
}
