package com.chswapna183_dotcom.pages;

import com.chswapna183_dotcom.util.TestUtil;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

public class HomePage {
    private final WebDriver driver;

    private final By logoutButton = By.cssSelector("a.button.secondary.radius");
    private final By flashMessage = By.id("flash");
    private final By pageHeader = By.cssSelector("div#content h2");

    public HomePage(WebDriver driver) {
        this.driver = driver;
    }

    public void waitForPage() {
        TestUtil.waitForVisible(driver, pageHeader);
        TestUtil.waitForVisible(driver, logoutButton);
    }

    public boolean isLogoutButtonDisplayed() {
        return !driver.findElements(logoutButton).isEmpty() && driver.findElement(logoutButton).isDisplayed();
    }

    public String getFlashMessage() {
        return TestUtil.cleanMessage(TestUtil.waitForVisible(driver, flashMessage).getText());
    }

    public LoginPage logout() {
        driver.findElement(logoutButton).click();
        return new LoginPage(driver);
    }
}

