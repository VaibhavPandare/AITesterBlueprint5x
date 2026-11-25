package com.example.salesforce.pages;

import io.qameta.allure.Step;
import java.time.Duration;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {
    private static final Duration WAIT_TIMEOUT = Duration.ofSeconds(20);

    private final WebDriver driver;
    private final WebDriverWait wait;

    @FindBy(xpath = "//input[@id='username']")
    private WebElement username;

    @FindBy(xpath = "//input[@id='password']")
    private WebElement password;

    @FindBy(xpath = "//input[@id='Login']")
    private WebElement loginButton;

    @FindBy(xpath = "//input[@id='rememberUn']")
    private WebElement rememberMe;

    @FindBy(xpath = "//div[@id='error']")
    private WebElement loginError;

    public LoginPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, WAIT_TIMEOUT);
        PageFactory.initElements(driver, this);
    }

    @Step("Open Salesforce login page")
    public void open() throws WebDriverException {
        driver.get("https://login.salesforce.com/?login=1&locale=in");
        wait.until(ExpectedConditions.visibilityOf(username));
    }

    @Step("Enter username and password")
    public void enterCredentials(String user, String pass, boolean rememberUsername) throws WebDriverException {
        wait.until(ExpectedConditions.visibilityOf(username)).sendKeys(user);
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        wait.until(ExpectedConditions.visibilityOf(password)).sendKeys(pass);

        WebElement rememberMeCheckbox = wait.until(ExpectedConditions.visibilityOf(rememberMe));
        if (rememberMeCheckbox.isSelected() != rememberUsername) {
            rememberMeCheckbox.click();
        }
    }

    @Step("Submit Salesforce login")
    public void submitLogin() throws WebDriverException {
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
    }

    @Step("Read Salesforce login error")
    public String getLoginError() throws WebDriverException {
        return wait.until(ExpectedConditions.visibilityOf(loginError)).getText();
    }

    @Step("Check whether remember me is selected")
    public boolean isRememberMeSelected() throws WebDriverException {
        return wait.until(ExpectedConditions.visibilityOf(rememberMe)).isSelected();
    }
}
