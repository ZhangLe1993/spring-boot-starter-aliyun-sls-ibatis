package com.biubiu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class AliyunSlsInitiator implements CommandLineRunner {

    private final static Logger logger = LoggerFactory.getLogger(AliyunSlsInitiator.class);

    public void run(String... args) throws Exception {
        logger.info("[AliyunSlsInitiator]>>>>>>>>>>>>>>>>>>>>>>>>>>>>> version 0.0.1-SNAPSHOT");
        logger.info("[AliyunSlsInitiator]>>>>>>>>>>>>>>>>>>>>>>>>>>>>> init success ");
    }

}
