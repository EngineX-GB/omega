package com.enginex.model;

public class Link {

    private String url;

    private String filename;

    private StrategyType strategyType;

    private int number;

    public Link() {
        // no-args constructor
    }

    public Link(final int number, final String url, final String filename, final StrategyType strategyType) {
        this.number = number;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }


}
