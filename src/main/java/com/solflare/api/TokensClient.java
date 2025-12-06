package com.solflare.api;

import com.solflare.enums.Network;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import static io.restassured.RestAssured.given;

public class TokensClient {

    private static final Logger logger = LogManager.getLogger(TokensClient.class);

    public static Response getTokens(String endpoint, String address, Network network, RequestSpecification requestSpec) {
        logger.info("GET request to {} with address={}, network={}", endpoint, address, network.getValue());

        Response response = given()
                .spec(requestSpec)
                .pathParam("address", address)
                .queryParam("network", network.getValue())
                .log().all()
                .when()
                .get(endpoint)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .response();

        logger.info("Response received with status code: {}", response.statusCode());
        return response;
    }

    public static Response getTokensWithoutNetwork(String endpoint, String address, RequestSpecification requestSpec) {
        logger.info("GET request to {} with address={}, no network parameter", endpoint, address);

        Response response = given()
                .spec(requestSpec)
                .pathParam("address", address)
                .log().all()
                .when()
                .get(endpoint)
                .then()
                .log().ifError()
                .statusCode(200)
                .extract()
                .response();

        logger.info("Response received with status code: {}", response.statusCode());
        return response;
    }

    public static Response getTokensRaw(String endpoint, String address, Network network, RequestSpecification requestSpec) {
        logger.info("GET request (raw) to {} with address={}, network={}", endpoint, address, network.getValue());

        Response response = given()
                .spec(requestSpec)
                .pathParam("address", address)
                .queryParam("network", network.getValue())
                .log().all()
                .when()
                .get(endpoint)
                .then()
                .log().all()
                .extract()
                .response();

        logger.info("Response received with status code: {}", response.statusCode());
        return response;
    }
}