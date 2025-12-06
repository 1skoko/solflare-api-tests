package com.solflare.base;

import com.solflare.config.ConfigManager;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.BeforeClass;

@Getter
public class BaseTest {

    private static final Logger logger = LogManager.getLogger(BaseTest.class);

    protected ConfigManager config;
    protected String address;
    protected RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
        logger.info("Initializing test configuration");

        config = ConfigManager.getInstance();
        address = config.getAddress();

        requestSpec = new RequestSpecBuilder()
                .setBaseUri(config.getBaseUrl())
                .addHeader("Authorization", config.getAuthorizationToken())
                .setContentType("application/json")
                .build();

        logger.info("=== Test Setup Complete ===");
        logger.info("Base URL: {}", config.getBaseUrl());
        logger.info("Address: {}", address);
        logger.info("Network: {}", config.getNetwork());
        logger.info("Authorization: {}", config.getAuthorizationToken());
        logger.info("===========================");
    }

    protected String getTokensEndpoint() {
        return config.getTokensEndpoint();
    }
}