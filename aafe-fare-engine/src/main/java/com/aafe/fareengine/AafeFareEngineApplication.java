package com.aafe.fareengine;

import com.aafe.fareengine.config.FareEngineProperties;
import com.aafe.fareengine.config.ServiceChargeProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties({FareEngineProperties.class, ServiceChargeProperties.class})
public class AafeFareEngineApplication {

    public static void main(String[] args) {
        SpringApplication.run(AafeFareEngineApplication.class, args);
    }

}
