package com.chswapna183_dotcom.test;

import com.chswapna183_dotcom.pages.HomePage;
import com.chswapna183_dotcom.pages.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

public class HomePageTest extends UITestBase {

    @Test(groups = "ui")
    public void successfulLogin_navigatesToSecureArea() {
        LoginPage loginPage = new LoginPage(driver).open();

        HomePage homePage = loginPage.loginExpectSuccess("tomsmith", "SuperSecretPassword!");

        Assert.assertTrue(homePage.isLogoutButtonDisplayed(), "Logout button should be visible after login.");
        Assert.assertTrue(
                homePage.getFlashMessage().contains("You logged into a secure area"),
                "Expected successful login message."
        );
    }
}
