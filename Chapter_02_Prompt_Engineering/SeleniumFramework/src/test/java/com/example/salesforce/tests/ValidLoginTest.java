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
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import org.testng.SkipException;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;

@Feature("Salesforce login")
public class ValidLoginTest {
    private WebDriver driver;
    private LoginPage loginPage;
    private String username;
    private String password;

    @BeforeTest
    public void setUp() {
        username = System.getenv("SALESFORCE_USERNAME");
        password = System.getenv("SALESFORCE_PASSWORD");
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            return;
        }

        try {
            driver = new EdgeDriver(new EdgeOptions());
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            driver.manage().window().maximize();
            loginPage = new LoginPage(driver);
        } catch (org.openqa.selenium.WebDriverException exception) {
            throw new IllegalStateException("Unable to start headed Edge for the valid-login test.", exception);
        }
    }

    @Test
    @Story("A valid user signs in")
    @Description("Checks that valid credentials leave the Salesforce login host and remember me can be selected.")
    public void validCredentialsShouldSignIn() throws WebDriverException {
        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            throw new SkipException(
                    "Set SALESFORCE_USERNAME and SALESFORCE_PASSWORD to run the valid-login test.");
        }

        loginPage.open();
        loginPage.enterCredentials(username, password, true);

        Assert.assertTrue(loginPage.isRememberMeSelected(), "Remember me should be selected before login.");
        loginPage.submitLogin();

        new WebDriverWait(driver, Duration.ofSeconds(30))
                .until(currentDriver -> !currentDriver.getCurrentUrl().contains("login.salesforce.com"));
        Assert.assertFalse(
                driver.getCurrentUrl().contains("login.salesforce.com"),
                "A successful login should navigate away from the Salesforce login host.");
    }

    @AfterTest(alwaysRun = true)
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
