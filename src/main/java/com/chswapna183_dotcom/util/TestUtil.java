package com.chswapna183_dotcom.util;

import com.chswapna183_dotcom.config.ConfigLoader;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public final class TestUtil {
    private TestUtil() {
    }

    public static WebElement waitForVisible(WebDriver driver, By locator) {
        return waitForVisible(driver, locator, ConfigLoader.getTimeout());
    }

    public static WebElement waitForVisible(WebDriver driver, By locator, Duration timeout) {
        return new WebDriverWait(driver, timeout).until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    public static String cleanMessage(String text) {
        if (text == null) {
            return "";
        }
        return text.replace("×", "").trim();
    }
}

