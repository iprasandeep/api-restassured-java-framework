package com.prasandeep.api.listeners;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.nio.file.Path;
import java.nio.file.Paths;

public class TestListener implements ITestListener {

    private static ExtentReports extent;
    private static final ThreadLocal<ExtentTest> TEST = new ThreadLocal<>();

    @Override
    public void onStart(ITestContext context) {
        Path reportPath = Paths.get("reports", "extent-report.html");
        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath.toFile());
        spark.config().setDocumentTitle("API RestAssured Framework");
        spark.config().setReportName("REST API Automation");
        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Framework", "RestAssured + TestNG");
        extent.setSystemInfo("Author", "prasandeep");
    }

    @Override
    public void onTestStart(ITestResult result) {
        TEST.set(extent.createTest(result.getMethod().getMethodName()));
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        TEST.get().log(Status.PASS, "Passed");
    }

    @Override
    public void onTestFailure(ITestResult result) {
        TEST.get().log(Status.FAIL, result.getThrowable());
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        TEST.get().log(Status.SKIP, "Skipped");
    }

    @Override
    public void onFinish(ITestContext context) {
        if (extent != null) {
            extent.flush();
        }
        TEST.remove();
    }
}
