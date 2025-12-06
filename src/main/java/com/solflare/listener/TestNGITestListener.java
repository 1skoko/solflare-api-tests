package com.solflare.listener;

import io.qameta.allure.Attachment;
import lombok.extern.slf4j.Slf4j;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

@Slf4j
public class TestNGITestListener implements ITestListener {

    @Override
    public void onStart(ITestContext context) {
        log.info("*** Execution of test set {} started ***", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        log.info("*** Executing test case '{}' ...", result.getMethod().getMethodName());
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        log.info("*** Test case '{}' result - PASS", result.getMethod().getMethodName());
        saveTextLog(result.getMethod().getMethodName() + " PASSED");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        log.error("*** Test case '{}' result - FAIL", result.getMethod().getMethodName());
        saveTextLog(result.getMethod().getMethodName() + " FAILED. Reason: " + result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        log.warn("*** Test case '{}' was skipped", result.getMethod().getMethodName());
        saveTextLog(result.getMethod().getMethodName() + " SKIPPED");
    }

    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        log.info("*** Test case '{}' FAILED with {}% success",
                result.getMethod().getMethodName(),
                result.getMethod().getSuccessPercentage());
        saveTextLog(result.getMethod().getMethodName() + " FAILED with " + result.getMethod().getSuccessPercentage() + "% success");
    }

    @Override
    public void onFinish(ITestContext context) {
        log.info("*** Execution of test set {} finished ***", context.getName());
    }

    @Attachment(value = "{0}", type = "text/plain")
    public static String saveTextLog(String message) {
        return message;
    }
}
