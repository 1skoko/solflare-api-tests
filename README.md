# Solflare Wallet API Test Automation

REST API test automation framework for Solflare Wallet API using REST Assured, TestNG, and Allure Reports.

🔗 **Repository:** [https://github.com/1skoko/solflare-api-tests](https://github.com/1skoko/solflare-api-tests)

## 📋 Table of Contents

- [Prerequisites](#prerequisites)
- [Project Structure](#project-structure)
- [Installation](#installation)
- [Configuration](#configuration)
- [Running Tests](#running-tests)
- [Allure Reports](#allure-reports)
- [Test Scenarios](#test-scenarios)
- [Troubleshooting](#troubleshooting)

## 🔧 Prerequisites

Before you begin, ensure you have the following installed:

- **Java JDK 17** or higher
  ```bash
  java -version
  ```

- **Maven 3.6+**
  ```bash
  mvn -version
  ```

- **Allure Command Line** (for report generation)
  ```bash
  # macOS
  brew install allure
  
  # Windows (using Scoop)
  scoop install allure
  
  # Linux
  sudo apt-add-repository ppa:qameta/allure
  sudo apt-get update
  sudo apt-get install allure
  
  # Verify installation
  allure --version
  ```

## 📁 Project Structure

```
solflare-api-tests/
├── allure-results/
├── logs/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com.solflare/
│   │   │       ├── api/
│   │   │       │   └── TokensClient.java
│   │   │       ├── base/
│   │   │       │   └── BaseTest.java
│   │   │       ├── config/
│   │   │       │   └── ConfigManager.java
│   │   │       ├── enums/
│   │   │       │   └── Network.java
│   │   │       └── listener/
│   │   │           └── TestNGITestListener.java
│   │   └── resources/
│   │       ├── allure.properties
│   │       └── environment.properties
│   └── test/
│       ├── java/
│       │   └── com.solflare.tests/
│       │       └── ApiTests.java
│       └── resources/
│           ├── config.properties
│           └── log4j2.xml
├── target/
├── .gitignore
├── pom.xml
├── testng.xml
└── README.md
```

## 🚀 Installation

### 1. Clone the Repository

```bash
git clone https://github.com/1skoko/solflare-api-tests.git
cd solflare-api-tests
```

### 2. Install Dependencies

```bash
mvn clean install
```

This will download all required dependencies including:
- REST Assured 5.3.1
- TestNG 7.8.0
- Allure TestNG 2.24.0
- Log4j2 2.22.1
- Jackson 2.16.0
- Lombok 1.18.30

## ⚙️ Configuration

### config.properties

Located in `src/test/resources/config.properties`:

```properties
# API Configuration
base.url=https://wallet-api.solflare.com
address=HuiTegTpNAU7EJXvn95HKEWBdFMtWZYko4yoFVQyCKUS
network=devnet

# Authentication
auth.uuid=0a6b8199-5f96-425a-b23d-680333fc6511

# Endpoints
endpoint.tokens=/v3/portfolio/tokens/{address}
```

### Environment Variable Override

You can override configuration using environment variables:

```bash
# Override base URL
export BASE_URL=https://wallet-api.solflare.com

# Override address
export ADDRESS=YourWalletAddressHere

# Override auth UUID
export AUTH_UUID=your-uuid-here
```

Priority: **Environment Variables** > **System Properties** > **config.properties**

## 🧪 Running Tests

### Run All Tests

```bash
mvn clean test
```

### Run with TestNG XML

```bash
mvn clean test -DsuiteXmlFile=testng.xml
```

### Run with Custom Configuration

```bash
mvn clean test "-Daddress=CustomAddress" "-Dnetwork=mainnet" "-Dbase.url=https://wallet-api.solflare.com"
```

## 📊 Allure Reports

### Generate and View Report

After running tests, generate and serve the Allure report:

```bash
# Generate report
allure generate

# Serve report (opens in browser automatically)
allure serve
```


### Clear Previous Results

```bash
# Clean allure results before new test run
rm -rf allure-results allure-report
mvn clean test
allure serve allure-results
```

## 📝 Test Scenarios

The framework includes 4 comprehensive test scenarios:

### Test Scenario 1: Devnet Token Validation
- **Priority:** 1
- **Description:** Verify API returns devnet-specific tokens when `network=devnet` parameter is used
- **Validations:**
  - Multiple tokens returned (not just SOL)
  - Each token has valid mint address
  - totalUiAmount has valid values
  - Field types validated (price, coingeckoId, verified)

### Test Scenario 2: SOL Token Validation
- **Priority:** 2
- **Description:** Verify API returns only SOL token when network parameter is not provided
- **Validations:**
  - Only one token returned
  - Token name is "Solana"
  - Token symbol is "SOL"
  - Mint address matches expected value
  - Price object contains required fields

### Test Scenario 3: Break the API
- **Priority:** 3
- **Description:** Attempt to break API by sending invalid wallet address
- **Validations:**
  - Returns 400 Bad Request
  - Error message is present
  - Error message contains "Invalid public key"

### Test Scenario 4: Network Switching
- **Priority:** 4
- **Description:** Verify switching between mainnet and devnet works correctly
- **Validations:**
  - Mainnet returns consistent results
  - Devnet returns additional tokens
  - Switching back to mainnet returns identical results
  - No API errors during network switching

## 🔍 Logging

Logs are generated in the `logs/` directory:

- **test-execution.log** - General test execution logs
- **api-requests.log** - Detailed API request/response logs

### Log Levels

Configure in `src/test/resources/log4j2.xml`:
- **DEBUG** - Detailed information for diagnosis
- **INFO** - General informational messages
- **WARN** - Warning messages
- **ERROR** - Error messages
