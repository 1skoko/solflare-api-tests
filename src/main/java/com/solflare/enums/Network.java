package com.solflare.enums;

public enum Network {
    MAINNET("mainnet"),
    DEVNET("devnet"),
    TESTNET("testnet");

    private final String value;

    Network(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    @Override
    public String toString() {
        return value;
    }

}
