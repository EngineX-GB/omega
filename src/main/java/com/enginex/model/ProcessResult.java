package com.enginex.model;

public class ProcessResult {

    private Integer returnCode;

    private String message;

    public ProcessResult (Integer returnCode, String message) {
        this.returnCode = returnCode;
        this.message = message;
    }

    public Integer getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(Integer returnCode) {
        this.returnCode = returnCode;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

}