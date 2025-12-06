package com.solflare.tests;

import com.solflare.api.TokensClient;
import com.solflare.base.BaseTest;
import com.solflare.enums.Network;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

@Epic("Solflare Wallet API")
@Feature("Portfolio Tokens API")
public class ApiTests extends BaseTest {

    private static final Logger logger = LogManager.getLogger(ApiTests.class);

    @Test(priority = 1, description = "Test Scenario 1: Devnet Token Validation")
    @Story("Devnet Token Validation")
    @Description("Verify that the API correctly returns devnet-specific tokens when network=devnet parameter is used")
    @Severity(SeverityLevel.CRITICAL)
    public void testDevnetTokensValidation() {
        logger.info("Starting Devnet Token Validation test");

        Response response = TokensClient.getTokens(
                getTokensEndpoint(),
                address,
                Network.DEVNET,
                requestSpec
        );

        logger.debug("API Response: {}", response.asPrettyString());

        List<Map<String, Object>> tokens = response.jsonPath().getList("tokens");
        logger.info("Received {} tokens from devnet", tokens.size());

        assertThat("There should be multiple tokens returned", tokens.size(), greaterThan(1));

        for (Map<String, Object> token : tokens) {
            validateTokenStructure(token);
        }

        logger.info("Devnet Token Validation test completed successfully");
    }

    @Test(priority = 2, description = "Test Scenario 2: SOL Token Validation")
    @Story("SOL Token Validation")
    @Description("Verify that when the network parameter is not provided, the API returns only the SOL token")
    @Severity(SeverityLevel.CRITICAL)
    public void testSolTokenWithoutNetwork() {
        logger.info("Starting SOL Token Validation test (without network parameter)");

        Response response = TokensClient.getTokensWithoutNetwork(
                getTokensEndpoint(),
                address,
                requestSpec
        );

        logger.debug("API Response (No Network): {}", response.asPrettyString());

        List<Map<String, Object>> tokens = response.jsonPath().getList("tokens");
        logger.info("Received {} token(s)", tokens.size());

        assertThat("Only one token should be returned", tokens.size(), is(1));

        Map<String, Object> solToken = tokens.get(0);
        logger.debug("SOL Token details: name={}, symbol={}, mint={}",
                solToken.get("name"), solToken.get("symbol"), solToken.get("mint"));

        assertThat("Token name should be Solana", solToken.get("name"), is("Solana"));
        assertThat("Token symbol should be SOL", solToken.get("symbol"), is("SOL"));
        assertThat("Token mint should match SOL mint", solToken.get("mint"), is("11111111111111111111111111111111"));
        assertThat("Token totalUiAmount should be valid", solToken.get("totalUiAmount"), is(notNullValue()));
        assertThat("totalUiAmount should be a number", solToken.get("totalUiAmount"), instanceOf(Number.class));

        if (solToken.containsKey("price")) {
            Map<String, Object> price = (Map<String, Object>) solToken.get("price");
            assertThat("Price map should contain 'price'", price.get("price"), is(notNullValue()));
            assertThat("Price map should contain 'usdPrice'", price.get("usdPrice"), is(notNullValue()));
            assertThat("Price map should contain 'change'", price.get("change"), is(notNullValue()));
            logger.debug("SOL price: ${}", price.get("usdPrice"));
        }

        logger.info("SOL Token Validation test completed successfully");
    }

    @Test(priority = 3, description = "Test Scenario 3: Break the API")
    @Story("Error Handling")
    @Description("Attempt to intentionally break the API by sending an invalid wallet address")
    @Severity(SeverityLevel.NORMAL)
    public void testBreakApiWithInvalidData() {
        String invalidAddress = "INVALID_ADDRESS_123";
        logger.info("Starting Break the API test with invalid address: {}", invalidAddress);

        Response response = TokensClient.getTokensRaw(
                getTokensEndpoint(),
                invalidAddress,
                Network.MAINNET,
                requestSpec
        );

        logger.debug("API Response for invalid input: {}", response.asPrettyString());
        logger.info("Received status code: {}", response.statusCode());

        assertThat("Status code should indicate client error", response.statusCode(), is(400));

        String errorMessage = response.jsonPath().getString("message");
        assertThat("Error message should be present", errorMessage, is(notNullValue()));
        assertThat("Error message should contain 'Invalid public key'", errorMessage, containsString("Invalid public key"));

        String dataMessage = response.jsonPath().getString("data.message");
        assertThat("Nested data.message should match", dataMessage, containsString("Invalid public key"));

        logger.info("Break the API test completed successfully - API correctly returned 400 error");
    }

    @Test(priority = 4, description = "Test Scenario 4: Returning to Mainnet After Switching to Devnet")
    @Story("Network Switching")
    @Description("Ensure that switching from mainnet to devnet and back to mainnet restores the original response")
    @Severity(SeverityLevel.CRITICAL)
    public void testSwitchBetweenMainnetAndDevnet() {
        logger.info("Starting Network Switching test");

        logger.info("Step 1: Fetching mainnet tokens");
        Response mainnetResponse1 = TokensClient.getTokens(
                getTokensEndpoint(),
                address,
                Network.MAINNET,
                requestSpec
        );
        logger.debug("Mainnet Response 1: {}", mainnetResponse1.asPrettyString());

        List<Map<String, Object>> mainnetTokens1 = mainnetResponse1.jsonPath().getList("tokens");
        logger.info("Mainnet tokens count (first request): {}", mainnetTokens1.size());

        logger.info("Step 2: Switching to devnet");
        Response devnetResponse = TokensClient.getTokens(
                getTokensEndpoint(),
                address,
                Network.DEVNET,
                requestSpec
        );
        logger.debug("Devnet Response: {}", devnetResponse.asPrettyString());

        List<Map<String, Object>> devnetTokens = devnetResponse.jsonPath().getList("tokens");
        logger.info("Devnet tokens count: {}", devnetTokens.size());

        assertThat("Devnet should contain at least as many tokens as mainnet",
                devnetTokens.size(), greaterThanOrEqualTo(mainnetTokens1.size()));
        assertThat("Devnet should have additional tokens compared to mainnet",
                devnetTokens.size(), greaterThan(mainnetTokens1.size()));

        logger.info("Step 3: Switching back to mainnet");
        Response mainnetResponse2 = TokensClient.getTokens(
                getTokensEndpoint(),
                address,
                Network.MAINNET,
                requestSpec
        );
        logger.debug("Mainnet Response 2: {}", mainnetResponse2.asPrettyString());

        List<Map<String, Object>> mainnetTokens2 = mainnetResponse2.jsonPath().getList("tokens");
        logger.info("Mainnet tokens count (second request): {}", mainnetTokens2.size());

        assertThat("Number of tokens on mainnet should remain consistent",
                mainnetTokens2.size(), is(mainnetTokens1.size()));

        logger.info("Validating token consistency between first and second mainnet requests");
        validateTokensMatch(mainnetTokens1, mainnetTokens2);

        logger.info("Network Switching test completed successfully");
    }

    @Step("Validate token structure")
    private void validateTokenStructure(Map<String, Object> token) {
        assertThat("Token should have a mint address", token.get("mint"), is(notNullValue()));
        assertThat("mint should be a string", token.get("mint"), instanceOf(String.class));

        assertThat("Token should have totalUiAmount", token.get("totalUiAmount"), is(notNullValue()));
        assertThat("totalUiAmount should be a number", token.get("totalUiAmount"), instanceOf(Number.class));

        if (token.containsKey("price")) {
            Map<String, Object> price = (Map<String, Object>) token.get("price");
            assertThat("Price map should contain 'price'", price.get("price"), is(notNullValue()));
            assertThat("Price should be numeric", price.get("price"), instanceOf(Number.class));
        }

        if (token.containsKey("coingeckoId")) {
            Object coingeckoId = token.get("coingeckoId");
            if (coingeckoId != null) {
                assertThat("coingeckoId should be a string", coingeckoId, instanceOf(String.class));
            }
        }

        if (token.containsKey("verified")) {
            assertThat("verified should be boolean", token.get("verified"), instanceOf(Boolean.class));
        }
    }

    @Step("Validate tokens match between responses")
    private void validateTokensMatch(List<Map<String, Object>> tokens1, List<Map<String, Object>> tokens2) {
        for (int i = 0; i < tokens1.size(); i++) {
            Map<String, Object> token1 = tokens1.get(i);
            Map<String, Object> token2 = tokens2.get(i);

            assertThat("Token name should match", token2.get("name"), is(token1.get("name")));
            assertThat("Token symbol should match", token2.get("symbol"), is(token1.get("symbol")));
            assertThat("Token mint should match", token2.get("mint"), is(token1.get("mint")));
            assertThat("Token totalUiAmount should match", token2.get("totalUiAmount"), is(token1.get("totalUiAmount")));
        }
        logger.debug("All {} tokens matched successfully", tokens1.size());
    }
}