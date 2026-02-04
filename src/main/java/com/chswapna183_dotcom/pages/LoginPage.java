package com.chswapna183_dotcom.pages;

import com.chswapna183_dotcom.config.ConfigLoader;
import com.chswapna183_dotcom.util.TestUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class LoginPage {
    private final WebDriver driver;

    private final By usernameInput = By.id("username");
    private final By passwordInput = By.id("password");
    private final By loginButton = By.cssSelector("button[type='submit']");
    private final By flashMessage = By.id("flash");

    public LoginPage(WebDriver driver) {
        this.driver = driver;
    }

    public LoginPage open() {
        driver.get(ConfigLoader.getBaseUrl() + "/login");
        return this;
    }

    public LoginPage typeUsername(String username) {
        TestUtil.waitForVisible(driver, usernameInput).clear();
        driver.findElement(usernameInput).sendKeys(username);
        return this;
    }

    public LoginPage typePassword(String password) {
        TestUtil.waitForVisible(driver, passwordInput).clear();
        driver.findElement(passwordInput).sendKeys(password);
        return this;
    }

    public HomePage loginExpectSuccess(String username, String password) {
        typeUsername(username);
        typePassword(password);
        driver.findElement(loginButton).click();
        HomePage homePage = new HomePage(driver);
        homePage.waitForPage();
        return homePage;
    }

    public LoginPage loginExpectFailure(String username, String password) {
        typeUsername(username);
        typePassword(password);
        driver.findElement(loginButton).click();
        TestUtil.waitForVisible(driver, flashMessage);
        return this;
    }

    public String getFlashMessage() {
        return TestUtil.cleanMessage(TestUtil.waitForVisible(driver, flashMessage).getText());
    }
}

