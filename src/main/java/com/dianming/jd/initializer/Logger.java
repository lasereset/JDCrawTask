
package com.dianming.jd.initializer;


public class Logger {
    private String name;

    public Logger(String name) {
        this.name = name;
    }

    public void info(String content) {
        System.out.println("[" + this.name + "] " + content);
    }
}