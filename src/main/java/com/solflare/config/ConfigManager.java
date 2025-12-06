package com.solflare.config;

import lombok.Getter;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

@Getter
public class ConfigManager {

    private static ConfigManager instance;
    private final Properties properties;

    private final String baseUrl;
    private final String address;
    private final String network;
    private final String authUuid;
    private final String tokensEndpoint;

    private ConfigManager() {
        properties = new Properties();
        loadProperties();

        this.baseUrl = getProperty("base.url");
        this.address = getProperty("address");
        this.network = getProperty("network");
        this.authUuid = getProperty("auth.uuid");
        this.tokensEndpoint = getProperty("endpoint.tokens");
    }

    public static ConfigManager getInstance() {
        if (instance == null) {
            synchronized (ConfigManager.class) {
                if (instance == null) {
                    instance = new ConfigManager();
                }
            }
        }
        return instance;
    }

    private void loadProperties() {
        try (InputStream input = getClass().getClassLoader().getResourceAsStream("config.properties")) {
            if (input == null) {
                System.out.println("Unable to find config.properties, using default values");
                setDefaultProperties();
                return;
            }
            properties.load(input);
            System.out.println("Configuration loaded successfully from config.properties");
        } catch (IOException e) {
            System.err.println("Error loading config.properties: " + e.getMessage());
            setDefaultProperties();
        }
    }

    private void setDefaultProperties() {
        properties.setProperty("base.url", "https://wallet-api.solflare.com");
        properties.setProperty("address", "HuiTegTpNAU7EJXvn95HKEWBdFMtWZYko4yoFVQyCKUS");
        properties.setProperty("network", "devnet");
        properties.setProperty("auth.uuid", "0a6b8199-5f96-425a-b23d-680333fc6511");
        properties.setProperty("endpoint.tokens", "/v3/portfolio/tokens/{address}");
    }

    private String getProperty(String key) {
        String envKey = key.toUpperCase().replace(".", "_");
        String envValue = System.getenv(envKey);
        if (envValue != null && !envValue.isEmpty()) {
            return envValue;
        }

        String systemValue = System.getProperty(key);
        if (systemValue != null && !systemValue.isEmpty()) {
            return systemValue;
        }

        return properties.getProperty(key);
    }

    public String getAuthorizationToken() {
        return "Bearer " + authUuid;
    }
}