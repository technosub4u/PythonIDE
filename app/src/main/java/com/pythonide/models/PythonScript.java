package com.pythonide.models;

import java.util.Date;

public class PythonScript {
    private String code;
    private String output;
    private Date timestamp;

    public PythonScript(String code, String output) {
        this.code = code;
        this.output = output;
        this.timestamp = new Date();
    }

    public String getCode() {
        return code;
    }

    public String getOutput() {
        return output;
    }

    public Date getTimestamp() {
        return timestamp;
    }
}
