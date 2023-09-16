package com.enginex.model;

public class Link {

    private String url;

    private String filename;

    private StrategyType strategyType;

    public Link(final String url, final String filename, final StrategyType strategyType) {
        this.url = url;
        this.filename = filename;
        this.strategyType = strategyType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public StrategyType getStrategyType() {
        return strategyType;
    }

    public void setStrategyType(StrategyType strategyType) {
        this.strategyType = strategyType;
    }

}
