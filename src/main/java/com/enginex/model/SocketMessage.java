package com.enginex.model;

public class SocketMessage {

    private int id;
    private Link link;
    private String status;

    public SocketMessage() {
        // no-args constructor
    }


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

}
