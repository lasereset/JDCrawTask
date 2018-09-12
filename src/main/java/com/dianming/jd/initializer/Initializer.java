package com.dianming.jd.initializer;

import org.springframework.beans.factory.InitializingBean;

public class Initializer implements InitializingBean {
    private static final Logger log = new Logger("初始化");

    public void afterPropertiesSet() throws Exception {
        log.info("Initializer");
    }
}