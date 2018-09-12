package com.dianming.jd.tool;

public class RequestException extends RuntimeException {
    private static final long serialVersionUID = 2212235627306027640L;
    private final String ar;

    public RequestException(String ar) {
        this.ar = ar;
    }

    public String getAr() {
        return this.ar;
    }
}