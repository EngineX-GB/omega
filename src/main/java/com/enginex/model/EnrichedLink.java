package com.enginex.model;

public class EnrichedLink extends Link {

    private String id;

    private String status;

    public EnrichedLink() {

    }
    public EnrichedLink(final String id, final String status, final int number, final String url,
                        final String filename, final StrategyType strategyType) {
        super(number, url, filename, strategyType);
        this.id = id;
        this.status = status;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public static EnrichedLink of (final EnrichedLink link, String newStatus) {
        return new EnrichedLink(link.id, newStatus, link.getNumber(), link.getUrl(), link.getFilename(), link.getStrategyType());
    }

    public static EnrichedLink of (final EnrichedLink link, String discoveredUrl, String newStatus) {
        return new EnrichedLink(link.id, newStatus, link.getNumber(), discoveredUrl, link.getFilename(), link.getStrategyType());
    }



    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
