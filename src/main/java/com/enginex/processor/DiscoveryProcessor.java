package com.enginex.processor;

import com.enginex.model.Link;

import java.util.List;

public interface DiscoveryProcessor {

    List<Link> discover(final List<Link> links);

    Link discover(final Link link);

    /**
     * This function will only check to see if the link exists in the audit service.
     * If it does not exist, then return the same link to proceed with the next step
     * (to actually download the link)
     * @param link       - the link
     * @return              the same link (to be used by the next process to proceed with downloading)
     */
    Link verifyDuplicateLink(final Link link);

}
