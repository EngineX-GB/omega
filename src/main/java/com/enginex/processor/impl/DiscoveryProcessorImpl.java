package com.enginex.processor.impl;

import com.enginex.model.AuditResponseCode;
import com.enginex.model.Link;
import com.enginex.processor.DiscoveryProcessor;
import com.enginex.service.AuditService;
import com.enginex.service.DiscoveryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;
import java.util.List;
import java.util.Optional;

public class DiscoveryProcessorImpl implements DiscoveryProcessor {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscoveryProcessorImpl.class);

    private DiscoveryService discoveryService;

    private AuditService auditService;

    public DiscoveryProcessorImpl(final DiscoveryService discoveryService, final AuditService auditService) {
        this.discoveryService = discoveryService;
        this.auditService = auditService;
    }

    @Override
    public List<Link> discover(List<Link> links) {
        final List<Link> newLinks = new LinkedList<>();
        for (final Link link : links) {
            final AuditResponseCode responseCode = auditService.isDuplicate(link);
            if ( responseCode != AuditResponseCode.DUPLICATE) {
                final Optional<String> url = retrieveUrl(link);
                if (url.isPresent()){
                    auditService.update(link);
                    newLinks.add(new Link(link.getNumber(), url.get(), link.getFilename(), link.getStrategyType()));
                }
            } else {
                LOGGER.warn("Audit Service Response for :  {} ->. {}", link.getFilename(), responseCode.name());
            }
        }
        return newLinks;
    }

    @Override
    public Link discover(final Link link) {
        final AuditResponseCode responseCode = auditService.isDuplicate(link);
        if (responseCode != AuditResponseCode.DUPLICATE) {
            final Optional<String> url = retrieveUrl(link);
            if (url.isPresent()) {
                auditService.update(link);
                return new Link(link.getNumber(), url.get(), link.getFilename(), link.getStrategyType());
            }
        }
        else {
            LOGGER.warn("Audit Service Response for :  {} ->. {}", link.getFilename(), responseCode.name());
        }
        return null;
    }

    @Override
    public Link verifyDuplicateLink(Link link) {
        final AuditResponseCode responseCode = auditService.isDuplicate(link);
        if (responseCode != AuditResponseCode.DUPLICATE) {
            return link;
        }
        LOGGER.warn("Audit Service Response for :  {} ->. {}", link.getFilename(), responseCode.name());
        return null;
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
            LOGGER.info("Retrying " + numberOfRetries + " time(s) to get resolved link for [" +link.getNumber()+ "]: " + link.getUrl());
        }
        return Optional.empty();
    }
}
