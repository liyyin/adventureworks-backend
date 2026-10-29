package com.refresh.customer_api.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@Profile("dev")
public class DevStartupLogger implements CommandLineRunner {

    @Override
    public void run(String ... args){
        log.info("=== Aplikasi berjalan dalam mode DEV. Swagger: http://localhost:8080/api/docs ===");
    }
}
