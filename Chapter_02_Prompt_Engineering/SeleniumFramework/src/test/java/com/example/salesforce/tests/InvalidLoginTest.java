package com.example.salesforce.tests;

import com.example.salesforce.pages.LoginPage;
import io.qameta.allure.Description;
import io.qameta.allure.Feature;
import io.qameta.allure.Story;
import java.time.Duration;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Feature("Salesforce login")
public class InvalidLoginTest {
    private WebDriver driver;
    private LoginPage loginPage;

    @BeforeTest
    public void setUp() {
        try {
            driver = new EdgeDriver(new EdgeOptions());
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().window().maximize();
            loginPage = new LoginPage(driver);
        } catch (org.openqa.selenium.WebDriverException exception) {
            throw new IllegalStateException("Unable to start headed Edge for the invalid-login test.", exception);
        }
    }

    @Test
    @Story("Invalid credentials are rejected")
    @Description("Checks that Salesforce displays its authentication error for invalid credentials.")
    public void invalidCredentialsShouldDisplayError() throws WebDriverException {
        loginPage.open();
        loginPage.enterCredentials("qa.invalid.user@example.invalid", "invalid-test-password", false);

        Assert.assertFalse(loginPage.isRememberMeSelected(), "Remember me should remain unselected.");
        loginPage.submitLogin();

        Assert.assertTrue(
                loginPage.getLoginError().contains("Please check your username and password"),
                "Salesforce should display its invalid-credentials error.");
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
